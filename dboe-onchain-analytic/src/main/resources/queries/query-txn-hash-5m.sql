SELECT distinct t.HASH
FROM analytics.dboe_transactions_realtime t
LEFT OUTER JOIN analytics.%s f ON t.hash = f.TransactionHash AND f.TxnTimestamp >= date_sub(current_time, INTERVAL 5 MINUTE)
WHERE t.block_timestamp >= date_sub(current_time, INTERVAL 5 MINUTE) and f.TransactionHash is NULL