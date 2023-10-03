package com.ngontro86.market.volatility

import com.ngontro86.common.annotations.ConfigValue

import javax.annotation.PostConstruct

class FlatVolEstimator implements VolatilityEstimator {

    @ConfigValue(config = "volMap")
    private Collection volMap = ["ETH:55", "BTC:49"]

    private Map<String, Double> vols = [:]

    @PostConstruct
    private void init() {
        volMap.each {
            def toks = it.toString().split(":")
            vols[toks[0]] = Double.valueOf(toks[1]) / 100d
        }
    }

    @Override
    void setUnderlyings(Set<String> underlyings) {

    }

    @Override
    double impliedVol(String underlying, long expiryUtc, double simpleMoneyness) {
        return vols[underlying]
    }
}
