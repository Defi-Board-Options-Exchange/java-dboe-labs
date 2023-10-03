package com.ngontro86.market.instruments

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class ExchangeSpecsLoaderProvider {

    @ConfigValue(config = "activeOptionLoader")
    private String activeOptionLoader = "RestBased"

    @Bean
    ExchangeSpecsLoader load() {
        return activeOptionLoader == 'RestBased' ? new RestBasedExchangeSpecsLoader() : new Web3jBasedExchangeSpecsLoader()
    }
}
