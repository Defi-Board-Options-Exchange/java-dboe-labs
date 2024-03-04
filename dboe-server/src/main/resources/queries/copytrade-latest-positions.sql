SELECT p.date, 'Options' AS category, p.instr_id AS token, p.chain, p.pos, p.ref_px, p.timestamp as asOf
FROM dboe_copytrade_options_positions p
INNER JOIN (
	SELECT CHAIN, wallet, MAX(TIMESTAMP) AS timestamp
	FROM dboe_copytrade_options_positions
	WHERE chain = '%s' wallet = '%s'
	GROUP BY CHAIN, wallet
) t ON p.chain = t.chain AND p.wallet = t.wallet AND p.timestamp = t.timestamp

UNION ALL
SELECT p.date, 'Spot' AS category, p.token, p.chain, p.pos, p.ref_px, p.timestamp as asOf
FROM dboe_copytrade_spot_positions p
INNER JOIN (
	SELECT CHAIN, wallet, MAX(TIMESTAMP) AS timestamp
	FROM dboe_copytrade_spot_positions
	WHERE chain = '%s' wallet = '%s'
	GROUP BY CHAIN, wallet
) t ON p.chain = t.chain AND p.wallet = t.wallet AND p.timestamp = t.timestamp