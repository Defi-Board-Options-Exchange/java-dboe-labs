package com.ngontro86.dboe.web3j.smartcontract

import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.DBOEClob
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

import static com.ngontro86.dboe.web3j.Utils.padding

class Web3jClobManager implements ClobManager<DBOEClob> {

    @Logging
    private Logger logger

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    private Web3j web3j

    @Inject
    private RawTransactionManager rawTxnManager

    private final double SCALE = Math.pow(10, 18)

    @Override
    DBOEClob load(String address) {
        logger.info("Loading CLOB at: ${address}")
        return DBOEClob.load(address, web3j, rawTxnManager, gasProvider)
    }

    @Override
    void toPrice(DBOEClob clob, String instrId, boolean buySell, double amount, int pxLevel, int timeoutMins) {
        logger.info("toPrice: ${instrId}, ${buySell}, ${amount}, ${pxLevel}, ${timeoutMins}")
        clob.toPrice(padding(32, instrId as byte[]),
                buySell,
                (amount * SCALE).toBigInteger(),
                pxLevel as BigInteger,
                timeoutMins * 60 as BigInteger
        ).send()
    }

    @Override
    List<ClobQuote> userQuotes(DBOEClob clob, String instrId) {
        def quotes = [] as List<ClobQuote>
        [true, false].each { bs ->
            def tuple3 = clob.userQuotes(rawTxnManager.fromAddress, padding(32, instrId as byte[]), bs).send()
            for (int idx = 0; idx < tuple3.component1().size(); idx++) {
                quotes << new ClobQuote(buySell: bs, pxLevel: tuple3.component1().get(idx), amount: tuple3.component2().get(idx), timestamp: tuple3.component3().get(idx))
            }
        }
        return quotes
    }

    @Override
    Long[] refPx(DBOEClob clob, String instrId) {
        def tuple3 = clob.refInfo(padding(32, instrId as byte[])).send()
        return [tuple3.component1() as Long, tuple3.component2() as Long] as Long[]
    }

    @Override
    Long currentRefPx(DBOEClob clob, String instrId) {
        return clob.refInfo(padding(32, instrId as byte[])).send().component1() as Long
    }

    @Override
    void calibrateRef(DBOEClob clob, String instrId) {

    }

    @Override
    void calibrateRefs(DBOEClob clob, String underlying, int expiry) {

    }

    @Override
    void setRef(DBOEClob clob, String instrId, long ref) {
        clob.updateRefPrice(padding(32, instrId as byte[]), ref as BigInteger).send()
    }

    @Override
    void cancelOrder(DBOEClob clob, String instrId, boolean bs, int pxLevel, BigInteger timestamp) {
        clob.toCancel(padding(32, instrId as byte[]), bs, pxLevel as BigInteger, timestamp).send()
    }
}
