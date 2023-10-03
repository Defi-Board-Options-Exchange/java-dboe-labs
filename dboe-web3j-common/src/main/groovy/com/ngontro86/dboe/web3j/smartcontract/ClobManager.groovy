package com.ngontro86.dboe.web3j.smartcontract

import com.ngontro86.dboe.web3j.DBOEClob

interface ClobManager<T> {

    T load(String address)

    void toPrice(T clob, String instrId, boolean buySell, double amount, int pxLevel, int timeoutMins)

    List<ClobQuote> userQuotes(T clob, String instrId)

    Long[] refPx(DBOEClob clob, String instrId)

    Long currentRefPx(DBOEClob clob, String instrId)

    void calibrateRef(T clob, String instrId)

    void cancelOrder(T clob, String instrId, boolean bs, int pxLevel, BigInteger timestamp)

    void setRef(DBOEClob clob, String instrId, long ref)
}