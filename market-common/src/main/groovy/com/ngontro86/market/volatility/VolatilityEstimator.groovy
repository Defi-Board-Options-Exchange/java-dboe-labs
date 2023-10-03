package com.ngontro86.market.volatility

interface VolatilityEstimator {
    void setUnderlyings(Set<String> underlyings)

    double impliedVol(String underlying, long expiryUtc, double simpleMoneyness)
}