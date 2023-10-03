package com.ngontro86.market.price


import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class SpotPricerProvider {

    @Bean
    SpotPricer spotPricer() {
        return new RealtimeSpotPricer()
    }
}
