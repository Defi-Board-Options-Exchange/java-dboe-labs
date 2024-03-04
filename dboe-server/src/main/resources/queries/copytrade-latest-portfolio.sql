SELECT p.*
FROM dboe_copytrade_portfolio p
INNER JOIN (
	SELECT CHAIN, wallet, MAX(TIMESTAMP) AS timestamp
	FROM dboe_copytrade_portfolio
	WHERE chain = '%s' wallet = '%s'
	GROUP BY CHAIN, wallet
) t ON p.chain = t.chain AND p.wallet = t.wallet AND p.timestamp = t.timestamp