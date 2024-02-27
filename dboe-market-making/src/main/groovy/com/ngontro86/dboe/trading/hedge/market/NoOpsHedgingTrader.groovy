package com.ngontro86.dboe.trading.hedge.market

class NoOpsHedgingTrader implements HedgingTrader {

    @Override
    void setUnderlyings(Set<String> underlyings) {}

    @Override
    void cancelOpenOrders() {}

    @Override
    Map<String, Double> loadPositions() { return [:] }

    @Override
    void hedgeRisk(String underlying, double absDelta) {}
}
