package com.ngontro86.dboe.web3j.inmemory

class InMemoryDBOEClob {

    Map<String, BigInteger> refPxMap = [:]
    Map<String, Map<Integer, Collection<BigInteger>>> bids = [:]
    Map<String, Map<Integer, Collection<BigInteger>>> asks = [:]

    void setRefPx(String instrId, BigInteger px) {
        refPxMap[instrId] = px
    }

    void toPrice(String instrId, boolean buySell, double amount, int pxLevel, int timeoutMins) {
        def bidAsks = buySell ? bids : asks
        bidAsks.putIfAbsent(instrId, [:])
        bidAsks.get(instrId).putIfAbsent(pxLevel, [] as Collection)
        bidAsks.get(instrId).get(pxLevel).add((amount * Math.pow(10, 18)).toBigInteger())
    }
}
