select
    i.underlying as underlying,
    i.expiry as expiry,
    i.kind as kind,
    t.time_to_expiry as timeToExpiry,
    Math.log(i.strike/s.spot) as moneyness,
    v.ref_iv as vol,
    s.spot as atm_price,
    i.strike as strike,
    v.in_timestamp as in_timestamp
from DboeImpliedVolWin v
inner join DboeOptionInstrWin i on v.instr_id = i.instr_id
inner join DboeOptionTimeToExpiryWin(time_to_expiry>0) t on i.instr_id = t.instr_id
inner join DboeSpotWin s on i.underlying = s.underlying