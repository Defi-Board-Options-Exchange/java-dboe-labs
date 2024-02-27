package com.ngontro86.dboe.web3j.smartcontract

import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.Web3jReadWrite
import com.ngontro86.dboe.web3j.DBOEClearingHouse
import com.ngontro86.dboe.web3j.Utils
import org.apache.logging.log4j.Logger
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

@DBOEComponent
class ClearingHouseManager {

    @Logging
    private Logger logger

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    @Web3jReadWrite
    private RawTransactionManager rawTxnManager

    private Map<String, DBOEClearingHouse> clearingHouses = [:]

    void initClearingHouseIfNeeded(String addr) {
        if (!clearingHouses.containsKey(addr)) {
            clearingHouses.put(addr, DBOEClearingHouse.load(addr, web3j, rawTxnManager, gasProvider))
        }
    }

    void enableOptionTrading(String addr, String underlying, int expiry) {
        println "Enable Option Trading: ${addr} for ${underlying} and ${expiry}..."
        if (clearingHouses.containsKey(addr)) {
            clearingHouses.get(addr).enableTrading(Utils.padding(32, underlying as byte[]), expiry as BigInteger).send()
        }
    }
}
