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
TransactionHash, TxnTimestamp, SenderAddress, ReceiverAddress, Amount, CurrencyAddress, m.dboe_chain_name as chain,
COALESCE(l.underlying, s.underlying, '') AS underlying,
COALESCE(l.expiry, s.expiry, 0) AS expiry,
COALESCE(l.kind, s.kind, '') AS kind,
COALESCE(l.instr_id, s.instr_id, '') AS instr_id,
case when s.short_contract_address is not null then ABS(s.strike - s.cond_strike) ELSE 0 end AS Collateral,
case when (s.instr_id IS NULL and l.instr_id IS null) then 0 ELSE 1 END AS isOption,
case when s.short_contract_address is not null then -1 when l.long_contract_address is not null then 1 ELSE 0 END AS isLongOrShort
from dboe_transfers t
INNER JOIN chain_mapping m ON t.Chain = m.name
LEFT outer join dboe_academy.dboe_all_options l ON t.CurrencyAddress = l.long_contract_address AND l.chain = m.dboe_chain_name
LEFT OUTER JOIN dboe_academy.dboe_all_options s ON t.CurrencyAddress = s.short_contract_address AND s.chain = m.dboe_chain_name
WHERE t.TxnTimestamp >= (now() - INTERVAL 15 day);

create or replace view dboe_transfer_option_instr AS
select distinct
TransactionHash, instr_id, m.dboe_chain_name as chain
from dboe_transfers t
INNER JOIN chain_mapping m ON t.Chain = m.name
inner join dboe_academy.dboe_all_options o ON (t.CurrencyAddress = o.long_contract_address OR t.CurrencyAddress = o.short_contract_address) AND o.chain = m.dboe_chain_name;


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
sum(volume * avgPx) as pos_val,
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
plan, name, airdrop_date, Address, sum(token_reward) as token_reward
from dboe_airdrop_records
group by 1,2,3,4
UNION ALL
SELECT distinct
'first trade' AS plan, r.name, r.airdrop_date, Address, r.first_trade_bonus as token_reward
FROM analytics.dboe_wallet_first_trade t
inner join dboe_airdrop_phases r on t.first_trade_date between r.starting_date and r.ending_date

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
LEFT OUTER JOIN analytics.dboe_wallet_first_trade t ON r.wallet_address = t.Address

CREATE OR REPLACE VIEW dboe_active_option_traded_value AS

SELECT o.chain, t.instr_id, SUM(ABS(volume)) * s.avg_spot/2 AS tradedValue
FROM analytics.dboe_wallet_txn t
INNER JOIN  dboe_all_options o ON o.expiry >= cast(date_format(CURRENT_DATE(), '%Y%m%d') AS UNSIGNED) and t.instr_id = o.instr_id AND t.chain = o.chain
INNER JOIN
(
	select DATE, underlying, avg(spot) as avg_spot
	from dboe_intraday_spot
	WHERE spot > 0
	GROUP BY 1, 2
) s ON o.underlying = s.underlying AND cast(date_format(t.TxnTimestamp, '%Y%m%d') AS UNSIGNED) = s.date
group BY o.chain, t.instr_id, s.avg_spot

CREATE OR REPLACE VIEW dboe_new_wallet_count as
SELECT a.date, a.numNewWallets, k.newWalletKyt, t.numberOfWalletSubmittedOrders
from
(
	SELECT FLOOR(timestamp/1000000) as date, COUNT(*) AS numNewWallets
	FROM dboe_tc_agreements
	where FLOOR(timestamp/1000000) >= cast(date_format(date(date_sub(current_date, INTERVAL 7 Day)), '%Y%m%d') AS UNSIGNED)
	GROUP BY 1
) a
LEFT outer JOIN (
	SELECT FLOOR(timestamp/1000000) as date, COUNT(*) AS newWalletKyt
	FROM dboe_kyt_addresses
	where FLOOR(timestamp/1000000) >= cast(date_format(date(date_sub(current_date, INTERVAL 7 Day)), '%Y%m%d') AS UNSIGNED)
	GROUP BY 1
) k ON a.date = k.date
LEFT OUTER JOIN (
	SELECT cast(date_format(date_sub(TxnTimestamp, INTERVAL -8 HOUR), '%Y%m%d') AS unsigned) AS date, COUNT(DISTINCT Address) AS numberOfWalletSubmittedOrders
	FROM (
		SELECT TransactionHash, TxnTimestamp, SenderAddress as Address FROM analytics.dboe_transfers
        WHERE TIMESTAMPDIFF(MINUTE, TxnTimestamp, CURRENT_TIMESTAMP) < 10080
        UNION ALL
        SELECT TransactionHash, TxnTimestamp, ReceiverAddress as Address FROM analytics.dboe_transfers
        WHERE TIMESTAMPDIFF(MINUTE, TxnTimestamp, CURRENT_TIMESTAMP) < 10080
	) X
	GROUP BY 1
) t ON a.date = t.date

CREATE OR replace VIEW dboe_prev_ref as
SELECT
	r.*, '4h' as delay
FROM (
	SELECT r.CHAIN, r.instr_id, r.currency, MAX(in_timestamp) AS in_timestamp
	FROM _dboe_ref_prices r
	INNER JOIN _dboe_option_instr i ON r.chain = i.chain AND r.instr_id = i.instr_id AND r.currency = i.currency
	WHERE in_timestamp <= (UNIX_TIMESTAMP()*1000 - 7200000) AND i.expiry >= cast(date_format((now()),'%Y%m%d') as UNSIGNED)
	GROUP BY 1,2,3
) x
INNER JOIN _dboe_ref_prices r ON x.chain = r.chain AND x.instr_id = r.instr_id AND x.currency = r.currency AND x.in_timestamp = r.in_timestamp


CREATE OR replace VIEW dboe_spot_prev_ref as
SELECT
	r.*, s.quote_token as quote_token, s.base_token as base_token, '4h' as delay
FROM (
	SELECT CHAIN, address,  MAX(in_timestamp) AS in_timestamp
	FROM _dboe_spot_ref_prices
	WHERE in_timestamp <= (UNIX_TIMESTAMP()*1000 - 7200000)
	GROUP BY 1,2
) x
INNER JOIN _dboe_spot_ref_prices r ON x.chain = r.chain AND x.address = r.address AND x.in_timestamp = r.in_timestamp
inner join dboe_spot_markets s on r.chain = s.chain and r.address = s.address

CREATE OR REPLACE VIEW dboe_total_liquidity_dashboard as
SELECT oi.DATE, eod_oi AS eod_open_interest, totalNotional AS total_traded_notional, numOfTrade AS num_txn, tradedValue AS total_traded_value, totalFee AS total_fee,
case when oi.date = cast(date_format(CURRENT_DATE(), '%Y%m%d') AS UNSIGNED) then 1 ELSE 0 end AS active
FROM (
	SELECT DATE, SUM(eod_oi) AS eod_oi
	FROM dboe_eod_open_interest
	GROUP BY 1
) oi
INNER JOIN (
	SELECT t.DATE, SUM(totalQty * avg_spot) AS totalNotional, SUM(numOfTrade) AS numOfTrade
	FROM analytics.dboe_daily_trade t
	INNER JOIN dboe_avg_spot s ON t.date = s.date and t.underlying = s.underlying
	GROUP BY 1
) t ON oi.date = t.date
INNER join analytics.dboe_daily_fee f ON t.date = f.date


--- on Analytics
create or replace view dboe_active_options as
SELECT m.name AS CHAIN, i.instr_id, i.long_contract_address, i.short_contract_address
FROM dboe_academy._dboe_option_instr i
INNER JOIN  analytics.chain_mapping m ON i.chain = m.dboe_chain_name
WHERE expiry >= cast(date_format((now()),'%Y%m%d') as unsigned)


CREATE OR replace view dboe_latest_holder_balance as
SELECT chain, instr_id, wallet, SUM(balance) AS balance
FROM (
	SELECT i.chain, i.instr_id, h.contract_address, h.holder_address AS wallet,
		h.latest_balance AS balance
	FROM analytics.dboe_token_holders h
	INNER join _dboe_option_instr i ON i.long_contract_address = h.contract_address
	INNER JOIN analytics.chain_mapping m ON i.chain = m.dboe_chain_name
	WHERE h.chain = m.name AND i.expiry >= cast(date_format((now()),'%Y%m%d') as UNSIGNED)
	UNION ALL
	SELECT i.chain, i.instr_id, h.contract_address, h.holder_address AS wallet,
		-h.latest_balance AS balance
	FROM analytics.dboe_token_holders h
	INNER join _dboe_option_instr i ON i.short_contract_address = h.contract_address
	INNER JOIN analytics.chain_mapping m ON i.chain = m.dboe_chain_name
	WHERE h.chain = m.name AND i.expiry >= cast(date_format((now()),'%Y%m%d') as UNSIGNED)
) x GROUP BY 1,2,3


CREATE OR REPLACE VIEW dboe_wallet_trades as
SELECT Address, count(distinct TransactionHash) as numOfTrades, SUM(ABS(notional)) AS tradedValue
FROM analytics.dboe_wallet_txn
GROUP BY 1

create or replace view dboe_wallet_trades_invites as
SELECT a.Address, a.numOfInvitations, coalesce(b.numOfTrades,0) AS numOfTrades, coalesce(b.tradedValue, 0.0) AS tradedValue
FROM (
	SELECT
		i.wallet_address AS Address,
		count(distinct r.wallet_address) AS numOfInvitations
	FROM referral_info i
	inner JOIN referral_ack r ON right(i.wallet_address, 8) = r.referral_code AND r.timestamp > i.timestamp
	GROUP BY 1
) a
left outer JOIN (
	SELECT Address, count(distinct TransactionHash) as numOfTrades, SUM(ABS(notional)) AS tradedValue
	FROM analytics.dboe_wallet_txn
	GROUP BY 1
) b ON a.Address = b.Address

UNION ALL
SELECT b.Address AS Address, coalesce(a.numOfInvitations,0) AS numOfInvitations, b.numOfTrades, b.tradedValue
FROM (
	SELECT
		i.wallet_address AS Address,
		count(distinct r.wallet_address) AS numOfInvitations
	FROM referral_info i
	inner JOIN referral_ack r ON right(i.wallet_address, 8) = r.referral_code AND r.timestamp > i.timestamp
	GROUP BY 1
) a
right outer JOIN (
	SELECT Address, count(distinct TransactionHash) as numOfTrades, SUM(ABS(notional)) AS tradedValue
	FROM analytics.dboe_wallet_txn
	GROUP BY 1
) b ON a.Address = b.Address



-- Copy Trade --

CREATE OR REPLACE VIEW dboe_copytrade_portfolio as
SELECT
	o.CHAIN, o.wallet, 'Options' AS category, o.instr_id AS token, pos, ref_px,
	pos * (ref_px - (case when pos < 0 then abs(i.cond_strike - i.strike)  ELSE 0 END)) AS val
FROM dboe_copytrade_options_positions o
INNER JOIN _dboe_option_instr i ON o.chain = i.chain AND o.instr_id = i.instr_id
INNER JOIN dboe_copytrade_latest_timestamp t ON o.chain = t.chain AND o.wallet = t.wallet AND o.timestamp = t.options_latest_timestamp
UNION all
SELECT
	p.CHAIN, p.wallet, 'Spot' as category, token, pos, ref_px,
	pos * ref_px AS val
FROM dboe_copytrade_spot_positions p
INNER JOIN dboe_copytrade_latest_timestamp t ON p.chain = t.chain AND p.wallet = t.wallet AND p.timestamp = t.spot_latest_timestamp



CREATE OR REPLACE VIEW dboe_copytrade_position as
SELECT p.date, 'Options' AS category, p.wallet, p.instr_id AS token, p.chain, p.pos, p.ref_px, i.underlying, i.expiry, i.strike, i.cond_strike, i.long_contract_address, i.short_contract_address, i.currency_address, i.ob_address, i.option_factory_address, i.clearing_address
FROM dboe_copytrade_options_positions p
INNER JOIN dboe_copytrade_latest_timestamp t ON p.chain = t.chain AND p.wallet = t.wallet AND p.timestamp = t.options_latest_timestamp
INNER JOIN dboe_all_options i ON p.chain = i.chain AND p.instr_id = i.instr_id

UNION ALL
SELECT p.date, 'Spot' AS category, p.wallet, p.token, p.chain, p.pos, p.ref_px, null as underlying, 0 as expiry, 0 AS strike, 0 AS cond_strike, i.base_token AS long_contract_address, i.base_token AS short_contract_address, i.quote_token AS currency_address, i.address AS ob_address, NULL AS option_factory_address, NULL AS clearing_address
FROM dboe_copytrade_spot_positions p
INNER JOIN dboe_copytrade_latest_timestamp t ON p.chain = t.chain AND p.wallet = t.wallet AND p.timestamp = t.spot_latest_timestamp
INNER JOIN dboe_spot_markets i ON p.chain = i.chain AND p.token = i.base_name AND i.quote_name = 'USDT'

--- Not used yet (for performances mining)
CREATE OR REPLACE VIEW dboe_copytrade_daily_portfolio as
SELECT DATE, TIMESTAMP, CHAIN, wallet, SUM(val) AS portfolio_val
FROM (
	SELECT
		DATE, TIMESTAMP, o.CHAIN, wallet, 'Options' AS category,
		SUM(pos * (ref_px - (case when pos < 0 then abs(i.cond_strike - i.strike)  ELSE 0 END)) ) AS val
	FROM dboe_copytrade_options_positions o
	INNER JOIN _dboe_option_instr i ON o.chain = i.chain AND o.instr_id = i.instr_id
	GROUP BY DATE, TIMESTAMP, o.CHAIN, wallet
	UNION all
	SELECT
		DATE, TIMESTAMP, CHAIN, wallet, 'Spot' as category,
		SUM(pos * ref_px) AS val
	FROM dboe_copytrade_spot_positions
	GROUP BY  DATE, TIMESTAMP, CHAIN, wallet
) x
GROUP BY 1,2,3,4
ORDER BY CHAIN, DATE, TIMESTAMP


--- Analytic ---
CREATE OR REPLACE VIEW intraday_option_price_spot as
SELECT p.date, p.timestamp, i.kind, i.expiry, i.underlying, i.strike, i.instr_id, 0.5 * (p.bid+p.ask) AS midprice, s.spot AS spot
FROM dboe_academy.dboe_intraday_price p
INNER JOIN dboe_academy._dboe_option_instr i ON p.instr_id = i.instr_id AND p.chain = i.chain AND p.currency = i.currency
INNER JOIN dboe_academy.dboe_intraday_spot s ON i.underlying = s.underlying AND s.date = p.date AND p.timestamp = s.timestamp
WHERE p.date <= i.expiry AND p.bid > 0 AND p.ask > 0 AND p.timestamp % 1000 = 0


CREATE OR REPLACE VIEW dboe_liquidity_30d AS
SELECT * from dboe_academy.dboe_total_liquidity_dashboard


----- Update Liquidity DB Events----
REPLACE INTO analytics.dboe_daily_trade
SELECT cast(date_format(date(date_sub(TxnTimestamp, INTERVAL -8 HOUR)), '%Y%m%d') AS UNSIGNED) AS date, CHAIN,
		underlying, expiry, kind, instr_id, COUNT(DISTINCT TransactionHash) AS numOfTrade, SUM(Amount) AS totalQty
FROM analytics.dboe_enriched_transfers
WHERE isOption = 1 AND TxnTimestamp >= timestamp(date_format(date(date_sub(CURRENT_DATE(), INTERVAL 4 DAY)), '%Y%m%d'))
GROUP BY 1,2,3,4,5,6;

REPLACE INTO analytics.dboe_daily_fee
SELECT cast(date_format(date(date_sub(TxnTimestamp, INTERVAL -8 HOUR)), '%Y%m%d') AS UNSIGNED) AS date, chain,
SUM(Amount) AS totalFee, SUM(Amount) * 100.0 as tradedValue
FROM analytics.dboe_enriched_transfers
where TxnTimestamp >= timestamp(date_format(date(date_sub(CURRENT_DATE(), INTERVAL 4 DAY)), '%Y%m%d')) AND ReceiverAddress IN ('0x649fb2a8ebd926faf4375c7ed7259e74d1d7851d', '0xe39578ba69805150f869ac5703f128b2cd713595')
GROUP BY 1, 2;

REPLACE INTO dboe_avg_spot
select DATE, underlying, avg(spot) as avg_spot
from dboe_intraday_spot
WHERE spot > 0 AND DATE > cast(date_format(date(date_sub(CURRENT_DATE(), INTERVAL 4 DAY)), '%Y%m%d') AS UNSIGNED)
GROUP BY 1, 2;

REPLACE INTO dboe_eod_open_interest
SELECT
o.date, o.chain, i.underlying, i.expiry, o.currency, i.kind, SUM(o.open_interest * s.avg_spot) AS eod_oi
from
(
	SELECT instr_id, chain, currency, DATE, MAX(TIMESTAMP) AS timestamp
	FROM _dboe_open_interest o
	WHERE MOD(o.TIMESTAMP, o.DATE) < 230000 AND o.date >= cast(date_format(date(date_sub(CURRENT_DATE(), INTERVAL 4 DAY)), '%Y%m%d') AS UNSIGNED)
	GROUP BY 1,2,3,4
) v
INNER JOIN _dboe_open_interest o ON v.instr_id = o.instr_id AND v.timestamp = o.timestamp AND o.chain = v.chain AND o.currency = v.currency
INNER JOIN _dboe_option_instr i ON i.instr_id = o.instr_id AND i.chain = o.chain AND i.currency = o.currency and i.expiry >= o.date
INNER JOIN dboe_avg_spot s ON i.underlying = s.underlying AND v.date = s.date
GROUP BY o.chain, o.currency, o.date, i.underlying, i.expiry, i.kind;

REPLACE INTO dboe_academy.dboe_copytrade_latest_timestamp
SELECT o.chain, o.wallet, o.timestamp AS options_latest_timestamp, s.timestamp AS spot_latest_timestamp
FROM
(
	SELECT CHAIN, wallet, MAX(TIMESTAMP) AS timestamp
	FROM dboe_copytrade_options_positions
	GROUP BY CHAIN, wallet
) o
INNER JOIN (
	SELECT CHAIN, wallet, MAX(TIMESTAMP) AS timestamp
	FROM dboe_copytrade_spot_positions
	GROUP BY CHAIN, wallet
) s ON o.chain = s.chain AND o.wallet = s.wallet;

INSERT ignore INTO dboe_wallet_first_trade
select SenderAddress AS Address, cast(date_format(MIN(TxnTimestamp), '%Y%m%d') as unsigned) AS first_trade_date
from analytics.dboe_transfers
WHERE TxnTimestamp > date_sub(NOW(), INTERVAL 2 DAY)
GROUP BY 1
UNION ALL
select ReceiverAddress AS Address, cast(date_format(MIN(TxnTimestamp), '%Y%m%d') as unsigned) AS first_trade_date
from analytics.dboe_transfers
WHERE TxnTimestamp > date_sub(NOW(), INTERVAL 2 DAY)
GROUP BY 1


REPLACE INTO dboe_airdrop_records
SELECT
cast(date_format(date(t.TxnTimestamp), '%Y%m%d') as UNSIGNED) AS DATE, 'volume-based' AS plan, r.name, r.airdrop_date, Address, sum(abs(notional)) * r.airdrop_rate_1k_notional/1000 as token_reward
from analytics.dboe_wallet_txn t
inner join dboe_airdrop_phases r on cast(date_format(date(t.TxnTimestamp), '%Y%m%d') as unsigned) between r.starting_date and r.ending_date
WHERE t.TxnTimestamp > date_sub(NOW(), INTERVAL 4 DAY)
group by 1,2,3,4,5

REPLACE INTO dboe_airdrop_records
SELECT
0 AS DATE, 'referral bonus' AS plan, b.name, b.airdrop_date, r.referrer_wallet AS Address, b.bonus_amount AS token_reward
FROM
(
    select i.email as referrer_email, i.wallet_address as referrer_wallet, COUNT(DISTINCT r.wallet_address) as numOfReferral
    from referral_info i
    LEFT OUTER JOIN referral_ack r on right(i.wallet_address, 8) = r.referral_code and r.timestamp > i.timestamp
    GROUP BY 1,2
) r
INNER JOIN dboe_airdrop_bonuses b ON r.numOfReferral >= b.min_no_referral AND r.numOfReferral <= b.max_no_referral