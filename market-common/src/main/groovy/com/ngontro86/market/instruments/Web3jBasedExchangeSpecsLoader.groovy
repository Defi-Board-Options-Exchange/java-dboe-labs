package com.ngontro86.market.instruments

class Web3jBasedExchangeSpecsLoader implements ExchangeSpecsLoader {

    @Override
    Collection loadChains() {
        return []
    }

    @Override
    Collection loadOptions(String chain) {
        return []
    }

    @Override
    Collection loadClobs(String chain) {
        return []
    }

    @Override
    Collection loadOptionChainMarket(String chain, String und, int expiry) {
        return []
    }

    @Override
    Collection orderbook(String chain, String instrId) {
        return []
    }

    @Override
    Collection<Map> loadSpotPairs(String chain) {
        return []
    }

    @Override
    Map<String, Double> refPrices(String chain) {
        return [:]
    }
}
