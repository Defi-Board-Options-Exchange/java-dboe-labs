package com.ngontro86.market.volatility


import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoints

class ParamlessPolynomialSurface {

    private Map<Long, PolynomialFunction> surface = [:] as TreeMap

    void addDataThenFit(long expiryUtc, Map<Double, Double> data) {
        def obs = new WeightedObservedPoints()
        data.each { moneyness, vol ->
            obs.add(moneyness, vol)
        }
        if (data.size() > 5) {
            surface.putIfAbsent(expiryUtc, new PolynomialFunction(PolynomialCurveFitter.create(3).fit(obs.toList())))
        }
    }

    double estVol(long expiryUtc, double simpleMoneyness) {
        if (surface.containsKey(expiryUtc)) {
            return surface.get(expiryUtc).value(simpleMoneyness)
        }
        def twoNearests = Utils.twoNearest(surface.keySet(), expiryUtc)
        if (twoNearests[0] == twoNearests[1]) {
            return surface.get(twoNearests[0]).value(simpleMoneyness)
        }
        def est1 = surface.get(twoNearests[0]).value(simpleMoneyness)
        def est2 = surface.get(twoNearests[1]).value(simpleMoneyness)
        return est1 + (est2 - est1) * (expiryUtc - twoNearests[0]) / (twoNearests[1] - twoNearests[0])
    }

}
