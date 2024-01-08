package com.ngontro86.market.pricing

import org.apache.commons.math3.distribution.NormalDistribution

import static com.ngontro86.market.pricing.Black76.NormalDistributionWrapper.normal
import static com.ngontro86.market.pricing.OptionKind.Call
import static com.ngontro86.market.pricing.OptionKind.Put

class Black76 {

    static double price(map) {
        return priceMap(map.option)
    }

    static double priceDboe(map) {
        return priceDboeMap(map.option)
    }

    static Map greek(map) {
        return greekMap(map.option)
    }

    /**
     * @param map
     * @return price, delta, vega, gamma and theta
     */
    static Double[] greekDboe(map) {
        return greekDboeMap(map.option)
    }

    private static double MIN_VOL = 0.01, MAX_VOL = 5.0
    private static double PX_DIFF_PCT_EPSILON = 0.25 //0.25%
    private static double PX_DIFF_EPSILON = 0.001
    private static int MAX_ITERATION = 20

    static double iv(double px, String kind, double atm, double strike, double condStrike, double timeToExpiry) {
        iv(px, [kind: kind == 'Call' ? Call : Put, atm: atm, strike: strike, condStrike: condStrike, t: timeToExpiry, r: 0d])
    }

    static double iv(double px, Map opt) {
        impliedVol(px, opt, MIN_VOL, MAX_VOL, 1)
    }

    private static double impliedVol(double px, Map opt, double minVol, double maxVol, int cnt) {
        double midVol = (minVol + maxVol) / 2
        opt << [vol: midVol]
        double estPx = priceDboe option: opt
        boolean stop = Math.abs(px - estPx) <= PX_DIFF_EPSILON || Math.abs(estPx / px - 1.0) * 100.0 <= PX_DIFF_PCT_EPSILON || cnt > MAX_ITERATION
        if (stop) {
            return midVol
        }
        return impliedVol(px, opt, estPx > px ? minVol : midVol, estPx > px ? midVol : maxVol, cnt + 1)
    }

    private static double priceDboeMap(Map option) {
        priceOption(option.kind, option.atm, option.strike, option.r, option.t, option.vol) - priceOption(option.kind, option.atm, option.condStrike, option.r, option.t, option.condVol > 0d ? option.condVol : option.vol)
    }

    private static Double[] greekDboeMap(Map option) {
        def g1 = greekOption(option.kind, option.atm, option.strike, option.r, option.t, option.vol)
        def g2 = greekOption(option.kind, option.atm, option.condStrike, option.r, option.t, option.vol)

        return [g1.px - g2.px, g1.delta - g2.delta, g1.vega - g2.vega, g1.gamma - g2.gamma, g1.theta - g2.theta]
    }

    private static double priceMap(Map option) {
        return priceOption(option.kind, option.atm, option.strike, option.r, option.t, option.vol)
    }

    private static Map greekMap(Map option) {
        return greekOption(option.kind, option.atm, option.strike, option.r, option.t, option.vol)
    }

    private static class NormalDistributionWrapper {
        static NormalDistribution normal = new NormalDistribution()
    }

    static double priceOption(OptionKind kind, double atm, double strike, double r, double timeToMaturity, double vol) {
        def d1 = (Math.log(atm / strike) + vol * vol * timeToMaturity / 2) / vol / Math.sqrt(timeToMaturity)
        def d2 = d1 - vol * Math.sqrt(timeToMaturity)
        return kind == Call ? Math.exp(-r * timeToMaturity) * (atm * normal.cumulativeProbability(d1) - strike * normal.cumulativeProbability(d2)) :
                Math.exp(-r * timeToMaturity) * (strike * normal.cumulativeProbability(-d2) - atm * normal.cumulativeProbability(-d1))
    }

    private static Map greekOption(OptionKind kind, double atm, double strike, double r, double timeToMaturity, double vol) {
        def d1 = (Math.log(atm / strike) + vol * vol * timeToMaturity / 2) / vol / Math.sqrt(timeToMaturity)
        def d2 = d1 - vol * Math.sqrt(timeToMaturity)
        def px = kind == Call ? Math.exp(-r * timeToMaturity) * (atm * normal.cumulativeProbability(d1) - strike * normal.cumulativeProbability(d2)) :
                Math.exp(-r * timeToMaturity) * (strike * normal.cumulativeProbability(-d2) - atm * normal.cumulativeProbability(-d1))

        def delta = kind == Call ? Math.exp(-r * timeToMaturity) * normal.cumulativeProbability(d1) : -(Math.exp(-r * timeToMaturity) * normal.cumulativeProbability(-d1))

        def vega = atm * Math.exp(-r * timeToMaturity) * normal.density(d1) * Math.sqrt(timeToMaturity)

        def gamma = Math.exp(-r * timeToMaturity) * normal.density(d1) / (atm * vol * Math.sqrt(timeToMaturity))

        def vomma = atm * Math.exp(-r * timeToMaturity) * normal.density(d1) * Math.sqrt(timeToMaturity) * d1 * d2 / vol

        def theta = kind == Call ? -(atm * Math.exp(-r * timeToMaturity) * normal.density(d1) * vol / 2 / Math.sqrt(timeToMaturity) + r * strike * Math.exp(-r * timeToMaturity) * normal.cumulativeProbability(d2) - r * atm * Math.exp(-r * timeToMaturity) * normal.cumulativeProbability(d1)) :
                -(atm * Math.exp(-r * timeToMaturity) * normal.density(d1) * vol / 2 / Math.sqrt(timeToMaturity) - r * strike * Math.exp(-r * timeToMaturity) * normal.cumulativeProbability(-d2) + r * atm * Math.exp(-r * timeToMaturity) * normal.cumulativeProbability(-d1))
        return [
                'px'   : px,
                'delta': delta,
                'vega' : vega,
                'gamma': gamma,
                'vomma': vomma,
                'theta': theta
        ]
    }
}
