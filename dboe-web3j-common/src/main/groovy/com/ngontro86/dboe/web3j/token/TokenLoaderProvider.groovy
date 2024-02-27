package com.ngontro86.dboe.web3j.token


import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.dboe.web3j.annotations.OptionLoader
import com.ngontro86.dboe.web3j.annotations.OtherTokenLoader
import org.springframework.context.annotation.Bean

@DBOEComponent
class TokenLoaderProvider {

    @Bean
    @OptionLoader
    TokenLoader optionLoader() {
        return new Web3jOptionLoader()
    }

    @Bean
    @OtherTokenLoader
    TokenLoader tokenLoader() {
        return new Web3jTokenLoader()
    }
}
