package com.ngontro86.dboe.web3j.token

import com.ngontro86.common.annotations.ConfigValue

import javax.annotation.PostConstruct

class Web3jTokenLoader extends Web3jAbstractTokenLoader {

    @ConfigValue(config = "spotWallet")
    private String spotWallet

    @PostConstruct
    private void init() {
        txnManager = txnManagerProvider.rwOnDemandTxnManager(spotWallet)
    }
}
