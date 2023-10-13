package com.ngontro86.dboe.web3j.smartcontract

import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.dboe.web3j.inmemory.InMemoryDBOEClob

@DBOEComponent
class InMemoryClobManager implements ClobManager<InMemoryDBOEClob> {

    @Override
    InMemoryDBOEClob load(String address) {
        return new InMemoryDBOEClob()
    }

    @Override
    void toPrice(InMemoryDBOEClob clob, String instrId, boolean buySell, double amount, int pxLevel, int timeoutMins) {
        clob.toPrice(instrId, buySell, amount, pxLevel, timeoutMins)
    }

    @Override
    List<ClobQuote> userQuotes(InMemoryDBOEClob clob, String instrId) {
        return []
    }

    @Override
    Long[] refPx(InMemoryDBOEClob clob, String instrId) {
        return new Long[0]
    }

    @Override
    Long currentRefPx(InMemoryDBOEClob clob, String instrId) {
        return null
    }

    @Override
    void calibrateRef(InMemoryDBOEClob clob, String instrId) {

    }

    @Override
    void calibrateRefs(InMemoryDBOEClob clob, String underlying, int expiry) {

    }

    @Override
    void cancelOrder(InMemoryDBOEClob clob, String instrId, boolean bs, int pxLevel, BigInteger timestamp) {

    }

    @Override
    void setRef(InMemoryDBOEClob clob, String instrId, long ref) {

    }

}
