package com.ngontro86.market.instruments

interface ExchangeSpecsLoader {
    Collection loadChains()
    Collection loadOptions(String chain)
    Collection loadClobs(String chain)
    Collection loadOptionChainMarket(String chain, String und, int expiry)
    Collection orderbook(String chain, String instrId)

    Collection<Map> loadSpotPairs(String chain)

    Map<String, Double> refPrices(String chain)
}