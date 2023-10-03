package com.ngontro86.dboe.trading.hedge.market

interface HedgingTrader {
    void setUnderlyings(Set<String> underlyings)

    void cancelOpenOrders()

    Map<String, Double> loadPositions()

    void hedgeRisk(String underlying, double absDelta)
}
