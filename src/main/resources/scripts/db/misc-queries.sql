create view eod_closes as
select
	p.date, p.inst_id, 0.5*(bid+ask) as close, p.timestamp
from _dbpub_prices p
inner join
(
	select max(timestamp) as timestamp, date, inst_id from _dbpub_prices
	group by date, inst_id
) m on p.inst_id = m.inst_id and p.date = m.date and p.timestamp = m.timestamp

create view daily_pnl as
select t.inst_id, t.broker, t.account, t.portfolio, t.date,
sum(size) as size, sum(size*i.multiplier*c.close*r.rate) - sum(size*avg_price*i.multiplier*r.rate) as pnl,
sum(abs(size)*r.rate * cr.rate) as fee
from _dbpub_trades t
inner join _dbpub_inst_ids i on t.inst_id = i.inst_id and t.date = i.date
inner join eod_closes c on t.date = c.date and t.inst_id = c.inst_id
inner join fx_rate r on i.currency = r.currency
inner join commission_rate cr on i.underlying = cr.underlying and t.broker = cr.broker
group by t.inst_id, t.broker, t.account, t.portfolio, t.date
