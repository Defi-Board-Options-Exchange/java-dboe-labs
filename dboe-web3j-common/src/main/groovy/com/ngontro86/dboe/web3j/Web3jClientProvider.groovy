package com.ngontro86.dboe.web3j

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@DBOEComponent
class Web3jClientProvider {

    @ConfigValue(config = "ethereumNodeUrl")
    private String ethereumNodeUrl


    @Bean
    Web3j web3jClient() {
        return Web3j.build(new HttpService(ethereumNodeUrl))
    }
}
