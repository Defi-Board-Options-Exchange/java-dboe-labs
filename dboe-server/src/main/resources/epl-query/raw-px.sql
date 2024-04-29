select
    i.underlying as underlying,
    i.instr_id as instr_id,
    i.expiry as expiry, i.ltt as ltt, i.kind as kind,
    i.strike as strike, i.cond_strike as cond_strike,
    s.spot as spot, Math.log(i.strike/s.spot) as moneyness,
    p.bid as bid, r.ref_price as ref, p.ask as ask,
    t.time_to_expiry as time_to_expiry
from DboeRefPriceWin r
left outer join DboePriceWin p on p.instr_id = r.instr_id and p.chain = r.chain and p.currency = r.currency
inner join DboeOptionInstrWin i on r.instr_id = i.instr_id
inner join DboeOptionTimeToExpiryWin(time_to_expiry>0) t on r.instr_id = t.instr_id
inner join DboeSpotWin s on i.underlying = s.underlying