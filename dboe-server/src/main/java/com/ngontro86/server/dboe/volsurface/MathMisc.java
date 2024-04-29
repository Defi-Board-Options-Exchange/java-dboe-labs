package com.ngontro86.server.dboe.volsurface;

class MathMisc {
	// returns an array containing [premium, delta, vega]
	public static double[] black76(double strike, double atm_forward, double vol, double t, double r, Kind kind) {
		assert atm_forward > 0 && t > 0 && vol > 0 && r >= 0;
		double discount = Math.exp(-t * r);
		double k = Math.log(strike / atm_forward);
		double sqrt_t = Math.sqrt(t), opt = vol * sqrt_t;
		double d1 = -k / opt + 0.5 * opt, d2 = d1 - opt;
		double phi_d2 = Math.exp(-d2 * d2 * 0.5) / Math.sqrt(2 * 3.14159265358979);
		double Phi_1 = 0.5 * (1 + erf(d1 / Math.sqrt(2))), Phi_2 = 0.5 * (1 + erf(d2 / Math.sqrt(2)));
		double vega = discount * strike * phi_d2 * sqrt_t;
		double premium, delta;
		if (kind == Kind.C) {
			premium = atm_forward * discount * (Phi_1 - Math.exp(k) * Phi_2);
			delta = discount * Phi_1;
		} else {
			premium = atm_forward * discount * (Math.exp(k) * (1 - Phi_2) - (1 - Phi_1));
			delta = discount * (Phi_1 - 1);
		}
		return new double[]{premium, delta, vega};
	}

	private static double erf(double z) {
		double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
		// use Horner's method
		double ans = 1 - t * Math.exp(-z * z - 1.26551223
				+ t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + t * (-0.18628806 + t * (0.27886807
				+ t * (-1.13520398 + t * (1.48851587 + t * (-0.82215223 + t * (0.17087277))))))))));
		return (z >= 0) ? ans : -ans;
	}
}
