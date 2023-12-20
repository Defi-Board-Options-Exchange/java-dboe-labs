@Name('DboeDsp') select chain, instr_id, currency, avg(price) as dsp, current_timestamp() as timestamp from DboeBBOWin group by instr_id output snapshot at (55, 23, *, *, *)
@Name('DboePrice') select chain, instr_id, currency, bid, ask, bid_size, ask_size, current_timestamp() as timestamp from DboePriceWin output snapshot at (*/15, *, *, *, *)
@Name('DboeRefPrice') select chain, instr_id, currency, ref_price, current_timestamp() as in_timestamp from DboeRefPriceWin output snapshot at (*/15, *, *, *, *)
@Name('DboeSpot') select underlying, spot, current_timestamp() as timestamp from DboeSpotWin output snapshot at (*/15, *, *, *, *)
@Name('DboeInstr') select instr_id, chain, collateral_group, underlying, kind, expiry, ltt, strike, cond_strike, multiplier, currency, long_contract_address, short_contract_address from DboeOptionInstrWin output snapshot at (*/15, *, *, *, *)
@Name('DboeAddressDivider') select chain, underlying, expiry, ob_address, fsp_address, option_factory_address, clearing_address from DboeAddressDividerWin
@Name('DboeUnderlyingMarketWin') select underlying, strike_scale, option_price_scale, min_txn_value, qty_step from DboeUnderlyingMarketWin
@Name('DboeOpenInterestWin') select chain, instr_id, currency, open_interest, current_timestamp() as timestamp from DboeOpenInterestWin output snapshot at (*/5, *, *, *, *)
@Name('DboeGiftOpenWin') select name, wallet_id, open_key, reward, current_timestamp() as timestamp from DboeMysteriousGiftUserOpenWin output snapshot at (*/5, *, *, *, *)
@Name('DboeSpotMarketsWin') select address, chain, quote_token, base_token, quote_name, base_name, quote_decimal, base_decimal from DboeSpotMarketWin
@Name('DboeSpotRefPrice') select chain, address, cast(1.0 * ref_price/px_scale, double) as ref_price, current_timestamp() as in_timestamp from DboeSpotRefWin output snapshot at (*/15, *, *, *, *)