package com.ngontro86.server.dboe.volsurface;

public class Spread {
	public final double strike, strike_cond;
	public final Kind kind;
	private double bid = Double.NaN, ask = Double.NaN, weight = 1.0;
	private double strike_vol = Double.NaN, strike_cond_vol = Double.NaN;

	public Spread(double p_strike, double p_strike_cond, Kind p_kind) {
		kind = p_kind;
		strike = p_strike;
		strike_cond = p_strike_cond;
		assert strike > 0 && strike_cond > 0;
	}

	// Set the bid and the ask directly. Use this for setting the market-observed prices
	@SuppressWarnings("unused")
	public Spread set_bid_ask(double p_bid, double p_ask) {
		assert p_bid >= 0 || Double.isNaN(p_bid); // bid = 0 is acceptable
		assert p_ask > 0 || Double.isNaN(p_ask);
		bid = p_bid;
		ask = p_ask;
		// Prices are set directly, as opposed to via strike vols; So we invalidate the strike vols;
		strike_vol = strike_cond_vol = Double.NaN;
		return this;
	}

	// Set the bid and the ask as a function of strike vols. Use this for testing
	public void set_bid_ask(double atm_forward, double p_strike_vol, double p_strike_cond_vol, double t, double r,
							double spread_ratio) {
		strike_vol = p_strike_vol;
		strike_cond_vol = p_strike_cond_vol;
		double price = MathMisc.black76(strike, atm_forward, strike_vol, t, r, kind)[0] -
				MathMisc.black76(strike_cond, atm_forward, strike_cond_vol, t, r, kind)[0];
		bid = price * (1 - spread_ratio / 2);
		ask = price * (1 + spread_ratio / 2);
	}

	// Define the importance weight of this spread for the purpose of vol fitting. By default, all spreads got weight=1,
	// but the user can choose to change that.
	@SuppressWarnings("unused")
	public void set_weight(double p_weight) {
		weight = p_weight;
	}

	// Defined as the average of bid/ask. If bid or ask is not well-defined, use the other one. If neither is, then nan.
	public double get_price() {
		if (bid >= 0 && ask > 0)
			return 0.5 * (bid + ask);
		if (ask > 0) return ask;
		if (bid >= 0) return bid;
		return Double.NaN;
	}

	public double get_weight() {
		return weight;
	}

	@Override
	public String toString() {
		return String.format("%s %.0f/%.0f observed bid/ask @(%.1f/%.1f), hidden strike vols @(%.1f,%.1f)",
				kind, strike, strike_cond, bid, ask, 100 * strike_vol, 100 * strike_cond_vol);
	}
}
