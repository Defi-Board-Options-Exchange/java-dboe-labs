package com.ngontro86.dboe.trading.hedge

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class HedgingExecutorProvider {

    @ConfigValue(config = "hedgingMode")
    private String hedgingMode = "FuturesHedge"

    @Bean
    HedgingExecutor hedge() {
        return hedgingMode == 'FuturesHedge' ? new FuturesHedger() : null
    }
}
