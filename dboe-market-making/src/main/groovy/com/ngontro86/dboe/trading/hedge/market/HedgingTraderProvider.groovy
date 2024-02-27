package com.ngontro86.dboe.trading.hedge.market

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class HedgingTraderProvider {

    @ConfigValue(config = "hedgingPlatform")
    private String hedgingPlatform = "Binance"

    @Bean
    HedgingTrader trader() {
        return hedgingPlatform == 'Binance' ? new BinanceTrader() : new NoOpsHedgingTrader()
    }
}
