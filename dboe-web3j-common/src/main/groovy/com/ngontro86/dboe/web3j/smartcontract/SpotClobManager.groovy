package com.ngontro86.dboe.web3j.smartcontract

interface SpotClobManager<T> {

    T load(String address)

    void toPrice(T clob, boolean buySell, double amount, int pxLevel, int timeoutMins)

    List<ClobQuote> userQuotes(T clob)

    double currentRefPx(T clob)

    void cancelOrder(T clob, boolean bs, int pxLevel, BigInteger timestamp)
}