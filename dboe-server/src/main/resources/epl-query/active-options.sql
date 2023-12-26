select
    i.underlying as underlying,
    i.expiry as expiry,
    i.ltt as ltt,
    i.kind as kind,
    t.time_to_expiry as timeToExpiry,
    Math.log(i.strike/s.spot) as moneyness,
    s.spot as atm_price,
    i.strike as strike,
    current_timestamp() as in_timestamp
from DboeOptionInstrWin i
inner join DboeOptionTimeToExpiryWin(time_to_expiry>0) t on i.instr_id = t.instr_id
inner join DboeSpotWin s on i.underlying = s.underlying