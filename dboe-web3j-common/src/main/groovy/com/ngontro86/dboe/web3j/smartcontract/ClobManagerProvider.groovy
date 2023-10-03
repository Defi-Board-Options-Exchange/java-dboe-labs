package com.ngontro86.dboe.web3j.smartcontract


import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class ClobManagerProvider {

    @Bean
    ClobManager clobManager() {
        return new Web3jClobManager()
    }
}
