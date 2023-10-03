package com.ngontro86.dboe.web3j

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.config.MaskedConfig
import org.springframework.context.annotation.Bean
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager

import javax.annotation.PostConstruct
import javax.inject.Inject

@DBOEComponent
class TxnManagerProvider {

    @ConfigValue(config = "chainId")
    private Integer chainId = 56

    @Inject
    private Web3j web3j

    @ConfigValue(config = "credential")
    private MaskedConfig credential

    private Credentials credentials

    @PostConstruct
    private void init() {
        credentials = Credentials.create(credential.unmaskedValue)
    }

    @Bean
    RawTransactionManager txnManager() {
        return new RawTransactionManager(web3j, credentials, chainId)
    }

    @Bean
    Credentials cred() {
        return credentials
    }

    RawTransactionManager onDemandTxnManager(String credential) {
        return new RawTransactionManager(web3j, Credentials.create(credential), chainId)
    }
}
