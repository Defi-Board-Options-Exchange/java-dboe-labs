create or replace view dboe_static_smart_contract_address AS
select distinct
	m.name as chain,
	ob_address, option_factory_address, clearing_address,
	max(expiry) as ending_effective_day,
	min(floor(timestamp/1000000)) as starting_effective_day,
	case when cast(date_format(curdate(),'%Y%m%d') as unsigned) between min(floor(timestamp/1000000)) and max(expiry) then 1 else 0 end as is_active
from dboe_academy._dboe_address_divider d
inner join chain_mapping m on d.chain = m.dboe_chain_name
group by 1,2,3,4

create or replace view dboe_enriched_transfers AS
select
TransactionHash, TxnTimestamp, SenderAddress, ReceiverAddress, Amount, CurrencySymbol, CurrencyAddress, m.dboe_chain_name as chain,
case when t.CurrencyAddress = o.short_contract_address then ABS(o.strike - o.cond_strike) ELSE 0 end AS Collateral,
case when o.instr_id IS NULL then 0 ELSE 1 END AS isOption,
case when t.CurrencyAddress = o.short_contract_address then -1 when t.CurrencyAddress = o.long_contract_address then 1 ELSE 0 END AS isLongOrShort
from dboe_transfers t
INNER JOIN chain_mapping m ON t.Chain = m.name
LEFT outer join dboe_academy.dboe_all_options o on t.CurrencySymbol = o.instr_id AND o.chain = m.dboe_chain_name;

create or replace view dboe_transfer_option_instr AS
select distinct
TransactionHash, CurrencySymbol AS instr_id, m.dboe_chain_name as chain
from dboe_transfers t
INNER JOIN chain_mapping m ON t.Chain = m.name
inner join dboe_academy.dboe_all_options o on t.CurrencySymbol = o.instr_id AND o.chain = m.dboe_chain_name;


create or replace view dboe_wallet_txn AS
SELECT x.TransactionHash, x.Chain, max(TxnTimestamp) AS TxnTimestamp, Address, o.instr_id, SUM(optionAmount) AS volume, SUM(notional) AS notional, case when SUM(optionAmount) != 0 then SUM(notional)/SUM(optionAmount) ELSE 0 end AS avgPx
FROM (
	SELECT TransactionHash, CHAIN, TxnTimestamp, SenderAddress AS Address, -SUM(Amount*isLongOrShort) AS optionAmount, SUM(Amount * (1 - isOption)) - SUM(Amount*Collateral*isOption*isLongOrShort) AS notional
	FROM dboe_enriched_transfers
	GROUP BY TransactionHash, CHAIN, TxnTimestamp, SenderAddress
	UNION ALL
	SELECT TransactionHash, CHAIN, TxnTimestamp, ReceiverAddress AS Address, SUM(Amount*isLongOrShort) AS optionAmount, -SUM(Amount * (1 - isOption)) + SUM(Amount*Collateral*isOption*isLongOrShort) AS notional
	FROM dboe_enriched_transfers
	GROUP BY TransactionHash, CHAIN, TxnTimestamp, ReceiverAddress
) x
INNER JOIN dboe_transfer_option_instr o ON x.TransactionHash = o.TransactionHash AND x.Chain = o.Chain
GROUP BY x.TransactionHash, x.CHAIN, Address, o.instr_id

create or replace view dboe_wallet_position as
select
chain,
Address as wallet_id, instr_id,
sum(volume) as pos,
sum(volume * avgPx) as val,
case when sum(volume) != 0 then sum(volume * avgPx)/sum(volume) ELSE 0 end as avg_px
from analytics.dboe_wallet_txn
group by 1,2,3;




--------------------- Other View -----------------
create or replace view dboe_all_options as
select
`i`.`instr_id` AS `instr_id`,`i`.`chain` AS `chain`,`i`.`collateral_group` AS `collateral_group`,`i`.`underlying` AS `underlying`,
`i`.`kind` AS `kind`,`i`.`expiry` AS `expiry`,`i`.`ltt` AS `ltt`,`i`.`multiplier` AS `multiplier`,`i`.`strike` AS `strike`,`i`.`cond_strike` AS `cond_strike`,
`i`.`long_contract_address` AS `long_contract_address`,`i`.`short_contract_address` AS `short_contract_address`,`i`.`currency` AS `currency`,
`c`.`address` AS `currency_address`,
`d`.`ob_address` AS `ob_address`, `d`.`option_factory_address` AS `option_factory_address`,`d`.`clearing_address` AS `clearing_address`,
`m`.`strike_scale` AS `underlying_px_scale`
from `_dboe_option_instr` `i`
inner join `_dboe_address_divider` `d` on `i`.`chain` = `d`.`chain` and `i`.`underlying` = `d`.`underlying` and `i`.`expiry` = `d`.`expiry`
inner join `dboe_collateral` `c` on `i`.`chain` = `c`.`chain` and `i`.`currency` = `c`.`token`
inner join `_dboe_underlying_market` `m` on `i`.`underlying` = `m`.`underlying`
where `i`.`expiry` >= cast(date_format((now() - interval 14 day),'%Y%m%d') as unsigned)


create or replace view dboe_options_universe as
select
`i`.`instr_id` AS `instr_id`,`i`.`chain` AS `chain`,`i`.`collateral_group` AS `collateral_group`,`i`.`underlying` AS `underlying`,
`i`.`kind` AS `kind`,`i`.`expiry` AS `expiry`,`i`.`ltt` AS `ltt`,`i`.`multiplier` AS `multiplier`,`i`.`strike` AS `strike`,`i`.`cond_strike` AS `cond_strike`,
`i`.`long_contract_address` AS `long_contract_address`,`i`.`short_contract_address` AS `short_contract_address`,`i`.`currency` AS `currency`,
`c`.`address` AS `currency_address`,
`d`.`ob_address` AS `ob_address`, `d`.`option_factory_address` AS `option_factory_address`,`d`.`clearing_address` AS `clearing_address`,
`m`.`strike_scale` AS `underlying_px_scale`
from `_dboe_option_instr` `i`
inner join `_dboe_address_divider` `d` on `i`.`chain` = `d`.`chain` and `i`.`underlying` = `d`.`underlying` and `i`.`expiry` = `d`.`expiry`
inner join `dboe_collateral` `c` on `i`.`chain` = `c`.`chain` and `i`.`currency` = `c`.`token`
inner join `_dboe_underlying_market` `m` on `i`.`underlying` = `m`.`underlying`
where `i`.`expiry` >= 20230728

create or replace view dboe_wallet_airdrop as
SELECT
'volume-based' AS plan, r.name, r.airdrop_date, Address, sum(abs(notional)) * r.airdrop_rate_1k_notional/1000 as token_reward
from analytics.dboe_wallet_txn t
inner join dboe_airdrop_phases r on cast(date_format(date(t.TxnTimestamp), '%Y%m%d') as unsigned) between r.starting_date and r.ending_date
group by 1,2,3,4
UNION ALL
SELECT
'referral bonus' AS plan, b.name, b.airdrop_date, r.referrer_wallet AS Address, b.bonus_amount AS token_reward
FROM
(
	select referrer_email, referrer_wallet, COUNT(DISTINCT referee_wallet) as numOfReferral
	from dboe_wallet_refer_stats
	GROUP BY 1,2
) r
INNER JOIN dboe_airdrop_bonuses b ON r.numOfReferral >= b.min_no_referral AND r.numOfReferral <= b.max_no_referral



create or replace view dboe_wallet_refer_stats as
SELECT
	i.email AS referrer_email,
	i.wallet_address AS referrer_wallet,
	i2.email AS referee_email,
	r.wallet_address AS referee_wallet,
	case when t.Address IS NULL then 0 ELSE 1 END AS referee_transacted_status,
	r.timestamp AS acked_timestamp
FROM referral_info i
LEFT outer JOIN referral_ack r ON right(i.wallet_address, 8) = r.referral_code AND r.timestamp > i.timestamp
LEFT OUTER JOIN referral_info i2 ON r.wallet_address = i2.wallet_address
LEFT OUTER JOIN (
	select distinct Address
	from analytics.dboe_wallet_txn
) t ON r.wallet_address = t.Address


SELECT Address, SmartContractMethodSignatureHash,
case WHEN SmartContractMethodSignatureHash = '6c4ffcf4' then 'toTrade'
	WHEN SmartContractMethodSignatureHash = '1a0f33fb' then 'toPrice'
	WHEN SmartContractMethodSignatureHash = 'bd66528a' then 'claim'
	WHEN SmartContractMethodSignatureHash = 'cf9484ae' then 'Refresh Ref Px'
	ELSE 'N.A' END AS method
FROM dboe_transactions WHERE TxnTimestamp > '2023-08-24 00:08:24'


CREATE OR REPLACE VIEW dboe_liquidity_14d as
SELECT
v.chain, v.currency, v.date, t.numOfTrades, t.tradedValue, t.totalFeeCollected, v.open_interest
FROM
(
	SELECT
	o.chain, o.currency, o.date, SUM(o.open_interest) AS open_interest
	from
	(
		SELECT o.instr_id, o.chain, o.currency, o.DATE, MAX(o.TIMESTAMP) AS timestamp
		FROM _dboe_open_interest o
		INNER JOIN dboe_all_options i ON i.instr_id = o.instr_id AND i.chain = o.chain AND i.currency = o.currency and i.expiry >= o.date
		WHERE MOD(o.TIMESTAMP, o.DATE) < 230000 AND o.date >= 20230813
		GROUP BY 1,2,3,4
	) v
	INNER JOIN _dboe_open_interest o ON v.instr_id = o.instr_id AND v.timestamp = o.timestamp AND o.chain = v.chain AND o.currency = v.currency
	GROUP BY 1,2,3
) v
INNER JOIN
(
	SELECT cast(date_format(date(date_sub(TxnTimestamp, INTERVAL -8 HOUR)), '%Y%m%d') AS UNSIGNED) AS date, chain,
	COUNT(*)/2 AS numOfTrades, SUM(Amount) AS totalFeeCollected, SUM(Amount) * 100.0/0.30 as tradedValue
	FROM analytics.dboe_enriched_transfers
	where TxnTimestamp >= '2023-08-13'AND currencySymbol like 'USD%' AND ReceiverAddress = '0x649fb2a8ebd926faf4375c7ed7259e74d1d7851d'
	GROUP BY 1, 2
) t ON t.date = v.date AND t.chain = v.chain


CREATE OR REPLACE VIEW dboe_active_option_traded_value as
SELECT o.chain, t.instr_id, SUM(ABS(notional))/2 AS tradedValue
FROM analytics.dboe_wallet_txn t
INNER JOIN  dboe_all_options o ON o.expiry >= cast(date_format(CURRENT_DATE(), '%Y%m%d') AS UNSIGNED) and t.instr_id = o.instr_id AND t.chain = o.chain
group BY 1, 2

CREATE OR REPLACE VIEW dboe_new_wallet_count as
SELECT a.date, a.numNewWallets, k.newWalletKyt, t.numberOfWalletSubmittedOrders
from
(
	SELECT FLOOR(timestamp/1000000) as date, COUNT(*) AS numNewWallets
	FROM dboe_tc_agreements
	GROUP BY 1
) a
LEFT outer JOIN (
	SELECT FLOOR(timestamp/1000000) as date, COUNT(*) AS newWalletKyt
	FROM dboe_kyt_addresses
	GROUP BY 1
) k ON a.date = k.date
LEFT OUTER JOIN (
	SELECT cast(date_format(date_sub(TxnTimestamp, INTERVAL -8 HOUR), '%Y%m%d') AS unsigned) AS date, COUNT(DISTINCT Address) AS numberOfWalletSubmittedOrders
	FROM (
		SELECT TransactionHash, TxnTimestamp, SenderAddress as Address FROM analytics.dboe_transfers
		UNION
		SELECT TransactionHash, TxnTimestamp, ReceiverAddress as Address FROM analytics.dboe_transfers
	) X
	GROUP BY 1
) t ON a.date = t.date
WHERE a.date >= cast(date_format(date(date_sub(current_date, INTERVAL 14 Day)), '%Y%m%d') AS UNSIGNED)

CREATE OR replace VIEW dboe_prev_ref as
SELECT
	r.*
FROM (
	SELECT CHAIN, instr_id, currency, MAX(in_timestamp) AS in_timestamp
	FROM _dboe_ref_prices
	WHERE in_timestamp <= (UNIX_TIMESTAMP()*1000 - 7200000)
	GROUP BY 1,2,3
) x
INNER JOIN _dboe_ref_prices r ON x.chain = r.chain AND x.instr_id = r.instr_id AND x.currency = r.currency AND x.in_timestamp = r.in_timestamp

CREATE OR REPLACE VIEW dboe_total_liquidity_dashboard as
SELECT
t.num_txn, t.total_traded_value, t.total_fee, v.open_interest
FROM
(
	SELECT
	SUM(o.open_interest * avg_spot) AS open_interest
	from
	(
		SELECT o.instr_id, o.chain, o.currency, o.DATE, MAX(o.TIMESTAMP) AS timestamp, avg_spot
		FROM _dboe_open_interest o
		INNER JOIN dboe_options_universe i ON i.instr_id = o.instr_id AND i.chain = o.chain AND i.currency = o.currency and i.expiry >= o.date
		INNER JOIN
		(
			select DATE, underlying, avg(spot) as avg_spot from dboe_intraday_spot
			WHERE spot > 0
			GROUP BY 1, 2
		) s ON i.underlying = s.underlying AND o.date= s.date
		WHERE o.currency <> 'NUSD' and MOD(o.TIMESTAMP, o.DATE) < 230000 AND o.date >= 20230813
		GROUP BY 1,2,3,4
	) v
	INNER JOIN _dboe_open_interest o ON v.instr_id = o.instr_id AND v.timestamp = o.timestamp AND o.chain = v.chain AND o.currency = v.currency
) v
INNER JOIN
(
	SELECT cast(COUNT(*)/2 AS UNSIGNED) AS num_txn, SUM(Amount) AS total_fee, SUM(Amount) * 100.0/0.30 as total_traded_value
	FROM analytics.dboe_enriched_transfers
	where TxnTimestamp >= '2023-08-13'AND currencySymbol like 'USD%' AND ReceiverAddress = '0x649fb2a8ebd926faf4375c7ed7259e74d1d7851d'
) t