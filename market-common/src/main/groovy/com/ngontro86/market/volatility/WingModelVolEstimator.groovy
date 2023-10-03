package com.ngontro86.market.volatility

class WingModelVolEstimator implements VolatilityEstimator {

    @Override
    void setUnderlyings(Set<String> underlyings) {

    }

    @Override
    double impliedVol(String underlying, long expiryUtc, double simpleMoneyness) {
        return 0
    }

}
