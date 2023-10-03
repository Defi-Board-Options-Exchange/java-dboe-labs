package com.ngontro86.market.time


import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class TimeSourceProvider {

    @Bean
    TimeSource timeSource() {
        return new SystemTimeSource()
    }
}
