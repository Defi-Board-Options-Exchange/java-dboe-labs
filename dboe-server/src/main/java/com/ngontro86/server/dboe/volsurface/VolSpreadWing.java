package com.ngontro86.server.dboe.volsurface;

import java.util.*;

/**
 * Author: Vladimir Barzov
 * Vladimir lent help by sharing his independent work for a smooth volatilities backout from DBOE markets
 */
public class VolSpreadWing {
	static private final double VOL_MIN = 0.05, VOL_MAX = 2.99; // min and max strike vol

	// Number of iterations over each set of coeffs
	static private final int MAX_NUM_REFITS = 10;
	// Set of coeffs to exclude from jiggling. May need think of how to set them intelligently based on time to expiry
	// and volatility.
	static private final Collection<String> EXCLUDE_COEFFS = new HashSet<>(Arrays.asList("dsm", "usm"));

	private boolean fitted = false; // Whether the surface has been fitted and is good to use.

	private double F; // Function of atm_forward and ref_price depending on SSR.
	private final double SSR, VCR, SCR; // Let's discuss how and if at all we want to set those.

	private final long expiry_utc; // expiration timestamp in milliseconds since epoch, e.g. 1715353200000L

	private final String[] cfnames = new String[]{
			"vr", "ref_price", "sr", "pc", "cc", "dc", "uc", "dsm", "usm"
	};

	private final MDouble[][] coeffs; // we link them to vr, sr, ...

	// coeffs, sensitivities, second derivatives
	private final MDouble ref_price,
			vr, sr, pc, cc,
			dc, uc, dsm, usm, d_vr, d_ref_price,
			d_sr, d_pc, d_cc, d_dc, d_uc, d_dsm,
			d_usm, d_ref_price2, d_dc2, d_uc2, d_dsm2, d_usm2;

	public VolSpreadWing(long p_expiry_utc) {
		expiry_utc = p_expiry_utc;

		coeffs = new MDouble[9][3];
		for (int i = 0; i != coeffs.length; ++i)
			for (int j = 0; j != 3; ++j)
				coeffs[i][j] = new MDouble(0.0);

		// link the coefficients with the double array coeffs, so they can be iterated over
		vr = coeffs[0][0];
		d_vr = coeffs[0][1];
		//MDouble d_vr2 = coeffs[0][2];
		ref_price = coeffs[1][0];
		d_ref_price = coeffs[1][1];
		d_ref_price2 = coeffs[1][2];
		sr = coeffs[2][0];
		d_sr = coeffs[2][1];
		// d_sr2 = coeffs[2][2];
		pc = coeffs[3][0];
		d_pc = coeffs[3][1];
		// d_pc2 = coeffs[3][2];
		cc = coeffs[4][0];
		d_cc = coeffs[4][1];
		//d_cc2 = coeffs[4][2];
		dc = coeffs[5][0];
		d_dc = coeffs[5][1];
		d_dc2 = coeffs[5][2];
		uc = coeffs[6][0];
		d_uc = coeffs[6][1];
		d_uc2 = coeffs[6][2];
		dsm = coeffs[7][0];
		d_dsm = coeffs[7][1];
		d_dsm2 = coeffs[7][2];
		usm = coeffs[8][0];
		d_usm = coeffs[8][1];
		d_usm2 = coeffs[8][2];

		// set some reasonable default values
		F = 0.0;
		vr.set(0.6, 0.01, VOL_MAX);
		sr.set(-0.3, -2.0, 1.0);
		pc.set(1.0, -10.0, 30.0);
		cc.set(1.0, -10.0, 30.0);
		dc.set(-0.2, -2.0, -0.001);
		uc.set(0.2, 0.001, 2.0);
		dsm.set(2.5, 0.1, 10.0);
		usm.set(2.5, 0.1, 10.0);
		ref_price.set(0.0, Double.NaN, Double.NaN); //NaN for min/max will not impose trimming
		SSR = 0.0; // SSR = 0.0 means F = ref_price, i.e. static vol surface
		VCR = 0.0;
		SCR = 0.0;
	}

	public double get_t(long timestamp_utc) {
		return (double) (expiry_utc - timestamp_utc) / (86400000L * 365L);
	}

	public boolean is_fitted() {
		return fitted;
	}

	public double get_vol(double atm_forward, double strike_price) {
		if (!fitted) return Double.NaN;
		return compute_vol(atm_forward, strike_price);
	}

	public void set_ref_price(double p_ref_price) {
		ref_price.set(p_ref_price);
	}

	public double get_estimated_price(double atm_forward, long timestamp_utc, double r, Spread spread) {
		assert fitted;
		double strike_vol = get_vol(atm_forward, spread.strike);
		double strike_cond_vol = get_vol(atm_forward, spread.strike_cond);
		double t = get_t(timestamp_utc);
		return MathMisc.black76(spread.strike, atm_forward, strike_vol, t, r, spread.kind)[0] -
				MathMisc.black76(spread.strike_cond, atm_forward, strike_cond_vol, t, r, spread.kind)[0];
	}

	public void refit(double atm_forward, long timestamp_utc, double r, List<Spread> spreads) {
		double t = get_t(timestamp_utc);
		int counter = 0;  // Max number of retries

		fitted = false;
		int[] long_short_legs = new int[]{-1, 1};

		//System.out.format("Start  : %s\n", this);
		while (++counter < MAX_NUM_REFITS) {
			for (int cindex = 0; cindex < cfnames.length; ++cindex) {
				String cfname = cfnames[cindex];
				if (EXCLUDE_COEFFS.contains(cfname))
					continue;
				MDouble[] coeff_info = coeffs[cindex];
				// compute the error and its sensitivity to this coefficient
				double derror_dcoeff = 0, derror_dcoeff2 = 0; // Sensitivity of the error wrt given coeff
				for (Spread spread : spreads) {
					double target_price = spread.get_price();
					if (Double.isNaN(target_price))
						continue; // We skip this spread as its price is not known, e.g. missing quotes
					double estimated_price = 0; // The estimated price given our current surface
					double dprice_dcoeff = 0, dprice_dcoeff2 = 0; // Sensitivity of the spread price wrt given coeff
					for (int long_short_leg : long_short_legs) {
						double strike_price = long_short_leg == 1 ? spread.strike : spread.strike_cond;
						double vol = compute_vol(atm_forward, strike_price);
						double[] greeks = MathMisc.black76(strike_price, atm_forward, vol, t, r, spread.kind);
						double premium = greeks[0], vega = greeks[2];
						estimated_price += long_short_leg * premium;
						dprice_dcoeff += long_short_leg * vega * coeff_info[1].v;
						dprice_dcoeff2 += long_short_leg * vega * coeff_info[2].v;
					}
					double error = estimated_price - target_price;
					fitted = true; // at least one iteration has gone through

					double weight = spread.get_weight();
					// Use squared weight, just like in a regression
					derror_dcoeff += weight * weight * error * dprice_dcoeff;
					derror_dcoeff2 += weight * weight * (error * dprice_dcoeff2 + dprice_dcoeff * dprice_dcoeff);
				}

				// Adjust according to the error sensitivity(slope) and curvature wrt that particular coefficient
				if (derror_dcoeff2 != 0)
					coeff_info[0].v -= derror_dcoeff / derror_dcoeff2;

				coeff_info[0].trim(); // make sure they fit coeff within its own restrictions
				//System.out.format("c%d [%s]: %s\n", counter, cfname, this);
			}
		}
	}

	// Use this to manually update a subset of the coefficients. Returns true if any of the coefficients was updated
	public void update_coeffs(Map<String, Double> coeff_vals) {
		boolean has_change = false;
		for (int i = 0; i < cfnames.length; ++i) {
			String cfname = cfnames[i];
			if (!coeff_vals.containsKey(cfname))
				continue;
			double val = coeff_vals.get(cfname);
			assert Double.isFinite(val);
			System.out.println("Updating " + cfname + " from " + coeffs[i][0] + " to " + val);
			coeffs[i][0].set(val);
			has_change = true;
		}
		if (has_change) {
			// Coeffs have been directly set -- so consider it as good as fitted.
			fitted = true;
		}
	}

	// String representation of all coeffs
	@Override
	public String toString() {
		int i = -1;
		StringBuilder sb = new StringBuilder();
		for (String cfname : cfnames)
			sb.append(String.format("%s=%.2f, ", cfname, coeffs[++i][0].v));
		return sb.toString();
	}

	private double compute_vol(double atm_forward, double strike_price) {
		assert ref_price.v > 0;

		// Zero the sensitivities
		for (int i = 0; i != coeffs.length; ++i)
			coeffs[i][1].v = coeffs[i][2].v = 0.0;

		double fr_ratio = atm_forward / ref_price.v;
		F = ref_price.v * Math.exp(SSR * Math.log(fr_ratio));
		double vc = vr.v + VCR * SSR * (1 - fr_ratio);
		double sc = sr.v + SCR * SSR * (1 - fr_ratio);

		double x = Math.log(strike_price / F), d_x = 0, d_x2 = 0.0, vol;

		if (x <= 0) {
			if (x > dc.v) {
				vol = vc + sc * x + pc.v * x * x;
				d_sr.v = x;
				d_pc.v = x * x;
				d_x = sc + 2.0 * pc.v * x;
				// second derivatives
				d_x2 = 2.0 * pc.v;
				// region = 3;
			} else if (x > dc.v * (1.0 + dsm.v)) {
				double aux1 = (1.0 + 1.0 / dsm.v);
				vol = vc - aux1 * pc.v * dc.v * dc.v - sc * dc.v / 2.0 / dsm.v + aux1 * (2.0 * pc.v * dc.v + sc) * x
						- (pc.v + sc / (2.0 * dc.v)) / dsm.v * x * x;

				d_sr.v = -dc.v / (2.0 * dsm.v) + aux1 * x - x * x / (2.0 * dc.v * dsm.v);
				d_pc.v = aux1 * dc.v * (2.0 * x - dc.v) - x * x / dsm.v;
				d_dc.v = -sc / (2.0 * dsm.v) + aux1 * 2.0 * pc.v * (x - dc.v)
						+ sc * x * x / (2.0 * dsm.v * dc.v * dc.v);
				d_dsm.v = (pc.v * dc.v * dc.v + sc * dc.v / 2.0 - (2.0 * pc.v * dc.v + sc) * x
						+ (pc.v + sc / (2.0 * dc.v)) * x * x) / (dsm.v * dsm.v);
				d_x = aux1 * (2.0 * pc.v * dc.v + sc) - (2.0 * pc.v + sc / dc.v) / dsm.v * x;

				// second derivatives
				d_dc2.v = -aux1 * 2.0 * pc.v - sc * x * x / (dsm.v * dc.v * dc.v * dc.v);
				d_dsm2.v = -2.0 * d_dsm.v / dsm.v;
				d_x2 = -(2.0 * pc.v + sc / dc.v) / dsm.v;

				// region = 2;
			} else {
				vol = vc + dc.v * (2.0 + dsm.v) * sc / 2.0 + (1.0 + dsm.v) * pc.v * dc.v * dc.v;
				d_sr.v = dc.v * (2.0 + dsm.v) / 2.0;
				d_pc.v = (1.0 + dsm.v) * dc.v * dc.v;
				d_dc.v = (2.0 + dsm.v) * sc / 2.0 + (1.0 + dsm.v) * 2.0 * dc.v * pc.v;
				d_dsm.v = dc.v * sc / 2.0 + pc.v * dc.v * dc.v;
				// second derivatives
				d_dc2.v = (1.0 + dsm.v) * 2.0 * pc.v;
				// region = 1;
			}
		} else {
			if (x < uc.v) {
				vol = vc + sc * x + cc.v * x * x;
				d_sr.v = x;
				d_cc.v = x * x;
				d_x = sc + 2.0 * cc.v * x;
				// second derivatives
				d_x2 = 2.0 * cc.v;
				// region = 4;

			} else if (x < uc.v * (1 + usm.v)) {
				double aux1 = (1.0 + 1.0 / usm.v);
				vol = vc - aux1 * cc.v * uc.v * uc.v - sc * uc.v / (2.0 * usm.v) + aux1 * (2.0 * cc.v * uc.v + sc) * x
						- (cc.v + sc / (2.0 * uc.v)) / usm.v * x * x;
				d_sr.v = -uc.v / (2.0 * usm.v) + aux1 * x - x * x / (2.0 * uc.v * usm.v);
				d_cc.v = aux1 * uc.v * (2 * x - uc.v) - x * x / usm.v;
				d_uc.v = -sc / (2.0 * usm.v) + aux1 * 2.0 * cc.v * (x - uc.v)
						+ sc * x * x / (2.0 * usm.v * uc.v * uc.v);
				d_usm.v = (cc.v * uc.v * uc.v + sc * uc.v / 2.0 - (2.0 * cc.v * uc.v + sc) * x
						+ (cc.v + sc / (2.0 * uc.v)) * x * x) / (usm.v * usm.v);
				d_x = aux1 * (2.0 * cc.v * uc.v + sc) - (2.0 * cc.v + sc / uc.v) / usm.v * x;
				// second derivatives
				d_uc2.v = -aux1 * 2.0 * cc.v - sc * x * x / (usm.v * uc.v * uc.v * uc.v);
				d_usm2.v = -2.0 * d_usm.v / usm.v;
				d_x2 = -(2.0 * cc.v + sc / uc.v) / usm.v;

				// region = 5;
			} else {
				vol = vc + uc.v * (2.0 + usm.v) * sc / 2.0 + (1.0 + usm.v) * cc.v * uc.v * uc.v;
				d_sr.v = uc.v * (2.0 + usm.v) / 2.0;
				d_cc.v = (1.0 + usm.v) * uc.v * uc.v;
				d_uc.v = (2.0 + usm.v) * sc / 2.0 + (1.0 + usm.v) * 2.0 * uc.v * cc.v;
				d_usm.v = uc.v * sc / 2.0 + cc.v * uc.v * uc.v;
				// second derivatives
				d_uc2.v = (1.0 + usm.v) * 2.0 * cc.v;
				// region = 6;
			}
		}

		// vr
		d_vr.v = 1.0;

		// spot (all of :d_vr.v, dvr_datm, dsc_datm: are constants

		//datm = d_vr.v * dvr_datm + d_sr.v * dsc_datm + d_x * dx_datm;
		//datm2 = d_sc_atm * dsc_datm + d_x_atm * dx_datm - d_x * dx_datm / atm_forward;

		// ref_price.v
		d_ref_price.v = d_x * (SSR - 1.0) / ref_price.v;
		d_ref_price2.v = (1.0 - SSR) / (ref_price.v * ref_price.v) * (d_x2 * (1.0 - SSR) + d_x);
		return new MDouble(vol, VOL_MIN, VOL_MAX).v;
	}
}
