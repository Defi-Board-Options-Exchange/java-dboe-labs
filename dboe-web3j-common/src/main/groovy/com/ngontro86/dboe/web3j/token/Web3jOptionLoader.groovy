package com.ngontro86.dboe.web3j.token

import com.ngontro86.common.annotations.ConfigValue

import javax.annotation.PostConstruct

class Web3jOptionLoader extends Web3jAbstractTokenLoader {

    @ConfigValue(config = "rwWallet")
    private String rwWallet

    @PostConstruct
    private void init() {
        txnManager = txnManagerProvider.rwOnDemandTxnManager(rwWallet)
    }
}
