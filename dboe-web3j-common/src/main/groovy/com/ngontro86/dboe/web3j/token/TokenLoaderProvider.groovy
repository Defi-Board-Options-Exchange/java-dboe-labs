package com.ngontro86.dboe.web3j.token


import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class TokenLoaderProvider {

    @Bean
    TokenLoader loader() {
        return new Web3jTokenLoader()
    }
}
