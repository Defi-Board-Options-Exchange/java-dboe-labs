@Name('PriceSnapshotter') select bid, ask, inst_id, state, last_price, open_price, close_price, current_timestamp() as timestamp from PriceSnapshotWin(series='1M')
@Name('SpotSnapshotter') select underlying, spot, current_timestamp() as timestamp from SpotWin output snapshot at (*/1, *, *, *, *)
@Name('TradeSnapshotter') select * from TradeWin
@Name('InstIdSnapshotter') select * from InstWin
@Name('AliasInstIdSnapshotter') select * from AliasInstIdWin
@Name('OrderReqSnapshotter') select r.inst_id as inst_id, r.order_req_id as order_req_id, r.exchange_order_id as exchange_order_id, r.qty as qty, p.bid as bid, r.price as price, p.ask as ask, r.broker as broker, r.portfolio as portfolio, r.account as account, r.timestamp as timestamp from OrderReqWin r unidirectional inner join PriceWin p on r.inst_id = p.inst_id
@Name('OrderReqRejectedSnapshotter') select * from OrderReqRejectedWin