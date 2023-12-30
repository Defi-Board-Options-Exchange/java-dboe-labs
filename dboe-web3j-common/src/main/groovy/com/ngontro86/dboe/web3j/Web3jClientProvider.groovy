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

    @ConfigValue(config = "web3jApiKey")
    private String web3jApiKey

    @ConfigValue(config = "web3jApiName")
    private String web3jApiName = 'X-goog-api-key'

    @Bean
    Web3j web3jClient() {
        def httpService = new HttpService(ethereumNodeUrl)
        if (web3jApiKey != null) {
            httpService.addHeader(web3jApiName, web3jApiKey)
        }
        return Web3j.build(httpService)
    }
}
