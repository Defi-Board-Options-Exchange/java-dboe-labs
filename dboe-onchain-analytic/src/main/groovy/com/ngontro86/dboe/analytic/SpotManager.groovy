package com.ngontro86.dboe.analytic


import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.Web3jReadOnly
import com.ngontro86.dboe.web3j.FspCalculator
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

import static com.ngontro86.dboe.web3j.Utils.padding

@DBOEComponent
class SpotManager {

    @Logging
    private Logger logger

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    @Web3jReadOnly
    private Web3j web3j

    @Inject
    @Web3jReadOnly
    private RawTransactionManager txnManager

    private Map<String, FspCalculator> fsps = [:]

    void setFspAddresses(Map<String, String> addrs) {
        def newPairs = addrs.findAll { !fsps.containsKey(it.key) }

        newPairs.each { k, v ->
            fsps[k] = FspCalculator.load(v, web3j, txnManager, gasProvider)
        }
    }

    double getSpot(String underlying, long asOfTime, int noOfSnapshot) {
        def spot = fsps[underlying].avgSpot(padding(32, underlying as byte[]), asOfTime as BigInteger, noOfSnapshot).send()
        def scale = fsps[underlying].priceScales(padding(32, underlying as byte[])).send()
        return spot / scale
    }
}
