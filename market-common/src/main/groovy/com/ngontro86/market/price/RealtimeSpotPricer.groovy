package com.ngontro86.market.price

import java.util.concurrent.ConcurrentHashMap

class RealtimeSpotPricer implements SpotPricer {

    Map<String, Double> latest = [:] as ConcurrentHashMap
    Map<String, Double> prevs = [:] as ConcurrentHashMap

    @Override
    Double spot(String underlying) {
        return latest.get(underlying)
    }

    @Override
    void update(String underlying, double spot) {
        def prev = latest.put(underlying, spot)
        if (prev != null) {
            prevs.put(underlying, prev)
        }
    }
}
