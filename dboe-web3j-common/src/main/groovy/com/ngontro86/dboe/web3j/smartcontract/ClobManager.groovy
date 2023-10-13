package com.ngontro86.dboe.web3j.smartcontract

interface ClobManager<T> {

    T load(String address)

    void toPrice(T clob, String instrId, boolean buySell, double amount, int pxLevel, int timeoutMins)

    List<ClobQuote> userQuotes(T clob, String instrId)

    Long[] refPx(T clob, String instrId)

    Long currentRefPx(T clob, String instrId)

    void calibrateRef(T clob, String instrId)

    void calibrateRefs(T clob, String underlying, int expiry)

    void cancelOrder(T clob, String instrId, boolean bs, int pxLevel, BigInteger timestamp)

    void setRef(T clob, String instrId, long ref)
}