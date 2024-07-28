package com.ngontro86.dboe.ref.updater


import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.Web3jReadWrite
import com.ngontro86.dboe.web3j.DBOEOnchainPredictionMarket
import com.ngontro86.dboe.web3j.Utils
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

class DboeOnchainPredictionMarketApp {

    @Logging
    private Logger logger

    @ConfigValue(config = "onchainPredictionMarketAddress")
    private String onchainPredictionMarketAddress

    @ConfigValue(config = "activeSec")
    private Integer activeSec = 60 * 10

    @ConfigValue(config = "finalSettleSec")
    private Integer finalSettleSec = 60 * 15

    @ConfigValue(config = "blockSec")
    private Integer blockSec = 60 * 5

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    @Web3jReadWrite
    private RawTransactionManager txnManager

    @EntryPoint
    void run() {
        try {
            long pivot = com.ngontro86.dboe.ref.updater.Utils.getTimeUtc(20240101, 80000) / 1000
            def predictionMarket = DBOEOnchainPredictionMarket.load(onchainPredictionMarketAddress, web3j, txnManager, gasProvider)
            int noOfMarket = predictionMarket.noOfMarkets().send()
            if(noOfMarket > 0) {
                def lastMarketId = predictionMarket.marketIds(noOfMarket - 1).send()
                def lastMarketInfo = predictionMarket.markets(lastMarketId).send()
                if (lastMarketInfo.component3() >= System.currentTimeMillis() / 1000) {
                    println "Latest Market ID: ${lastMarketId} to Final Settle..."
                    predictionMarket.finalSettle(lastMarketId).send()
                }
            }
            def nextMarketId = "00000${noOfMarket + 1}".toString()
            println "Setup next Market: ${nextMarketId}"
            predictionMarket.setup(
                    Utils.padding(32, nextMarketId as byte[]),
                    com.ngontro86.dboe.ref.updater.Utils.getTimeUtcWithBlock((long)(System.currentTimeMillis() / 1000), pivot, (long)blockSec) as BigInteger,
                    activeSec as BigInteger,
                    finalSettleSec as BigInteger
            ).send()
            println "Done"
            System.exit(0)
        } catch (Exception e) {
            e.printStackTrace()
            logger.error(e)
        }
    }

}
