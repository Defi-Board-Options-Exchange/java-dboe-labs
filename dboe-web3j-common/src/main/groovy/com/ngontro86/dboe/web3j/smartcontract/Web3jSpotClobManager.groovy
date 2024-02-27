package com.ngontro86.dboe.web3j.smartcontract

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.Web3jReadWrite
import com.ngontro86.dboe.web3j.DBOESpotMarket
import com.ngontro86.dboe.web3j.ERC20
import com.ngontro86.dboe.web3j.TxnManagerDbProvider
import com.ngontro86.dboe.web3j.annotations.OtherTokenLoader
import com.ngontro86.dboe.web3j.token.TokenLoader
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.annotation.PostConstruct
import javax.inject.Inject

class Web3jSpotClobManager implements SpotClobManager<DBOESpotMarket> {

    @Logging
    private Logger logger

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    private TxnManagerDbProvider txnManagerProvider

    private RawTransactionManager rawTxnManager

    @ConfigValue(config = "spotWallet")
    private String spotWallet

    @Inject
    @OtherTokenLoader
    private TokenLoader<ERC20> tokenLoader

    @PostConstruct
    private void init() {
        rawTxnManager = txnManagerProvider.rwOnDemandTxnManager(spotWallet)
    }

    @Override
    DBOESpotMarket load(String address) {
        logger.info("Loading CLOB at: ${address}")
        return DBOESpotMarket.load(address, web3j, rawTxnManager, gasProvider)
    }

    @Override
    void toPrice(DBOESpotMarket clob, boolean buySell, double amount, int pxLevel, int timeoutMins) {
        logger.info("toPrice: ${buySell}, ${amount}, ${pxLevel}, ${timeoutMins}")
        clob.toPrice(buySell,
                (amount * quoteDecimal(clob)).toBigInteger(),
                pxLevel as BigInteger,
                timeoutMins * 60 as BigInteger,
                rawTxnManager.fromAddress
        ).send()
    }

    private BigInteger quoteDecimal(DBOESpotMarket clob) {
        Math.pow(10, tokenLoader.decimals(tokenLoader.load(clob.baseToken().send())))
    }

    @Override
    List<ClobQuote> userQuotes(DBOESpotMarket clob) {
        def quotes = [] as List<ClobQuote>
        [true, false].each { bs ->
            def tuple3 = clob.userQuotes(rawTxnManager.fromAddress, bs).send()
            for (int idx = 0; idx < tuple3.component1().size(); idx++) {
                quotes << new ClobQuote(buySell: bs, pxLevel: tuple3.component1().get(idx), amount: tuple3.component2().get(idx), timestamp: tuple3.component3().get(idx))
            }
        }
        return quotes
    }

    @Override
    double currentRefPx(DBOESpotMarket clob) {
        def refs = clob.refInfo().send()
        return refs.component2() / refs.component3() as double
    }

    @Override
    void cancelOrder(DBOESpotMarket clob, boolean bs, int pxLevel, BigInteger timestamp) {
        clob.toCancel(bs, pxLevel as BigInteger, timestamp).send()
    }
}
