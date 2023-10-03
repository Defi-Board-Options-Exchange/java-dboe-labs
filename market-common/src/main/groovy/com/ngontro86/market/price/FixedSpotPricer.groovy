package com.ngontro86.market.price

import com.ngontro86.common.annotations.ConfigValue

import javax.annotation.PostConstruct

class FixedSpotPricer implements SpotPricer {

    @ConfigValue(config = "spots")
    private Collection spots = ["ETH:1900", "BTC:30000"]

    private Map spotMap = [:]

    @PostConstruct
    private void init() {
        spots.each {
            def toks = it.toString().split(":")
            spotMap[toks[0]] = Double.valueOf(toks[1])
        }
    }

    @Override
    double spot(String underlying) {
        return spotMap.getOrDefault(underlying, 0d)
    }

    @Override
    void update(String underlying, double spot) {
        spotMap.put(underlying, spot)
    }
}
