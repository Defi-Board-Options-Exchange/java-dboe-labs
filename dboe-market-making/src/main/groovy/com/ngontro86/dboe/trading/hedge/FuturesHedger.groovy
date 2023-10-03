package com.ngontro86.dboe.trading.hedge

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.trading.hedge.market.HedgingTrader
import com.ngontro86.market.web3j.GreekRisk
import org.apache.logging.log4j.Logger

import javax.inject.Inject

class FuturesHedger implements HedgingExecutor {

    @Logging
    private Logger logger

    @Inject
    private HedgingTrader hedger

    @ConfigValue(config = "deltaThreshold")
    private Double deltaThreshold

    @Override
    void setUnderlyings(Set<String> underlyings) {
        hedger.setUnderlyings(underlyings)
        hedger.cancelOpenOrders()
    }

    @Override
    void hedgeIfNeeded(String underlying, GreekRisk risk) {
        def portfolio = hedger.loadPositions()
        println "Risk delta: ${risk.delta}, portfolio: ${portfolio.getOrDefault(underlying, 0d)}"
        if (Math.abs(risk.delta + portfolio.getOrDefault(underlying, 0d)) >= deltaThreshold) {
            logger.info("Hedging: ${underlying}, residual delta: ${risk.delta + portfolio.getOrDefault(underlying, 0d)}")
            println("Hedging: ${underlying}, residual delta: ${risk.delta + portfolio.getOrDefault(underlying, 0d)}")
            hedger.hedgeRisk(underlying, risk.delta + portfolio.getOrDefault(underlying, 0d))
        }
    }
}
