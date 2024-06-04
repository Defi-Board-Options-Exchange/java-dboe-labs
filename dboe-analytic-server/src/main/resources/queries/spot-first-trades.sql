SELECT lower(SenderAddress) as Address FROM  analytics.dboe_spot_transfers
UNION ALL
SELECT lower(ReceiverAddress) as Address FROM analytics.dboe_spot_transfers