package com.ngontro86.dboe.web3j

import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Web3jReadWrite
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

@DBOEComponent
class Web3jManager {

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    @Web3jReadWrite
    private RawTransactionManager txnManager

    @Inject
    private ContractGasProvider gasProvider

    String getWallet() {
        return txnManager.fromAddress
    }

}
