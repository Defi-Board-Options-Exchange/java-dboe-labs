SELECT distinct t.HASH
FROM analytics.dboe_transactions_realtime t
LEFT OUTER JOIN analytics.dboe_transfers f ON t.hash = f.TransactionHash AND f.TxnTimestamp >= date_sub(current_time, INTERVAL 2 MINUTE)
WHERE t.block_timestamp >= date_sub(current_time, INTERVAL 2 MINUTE) and f.TransactionHash is NULL