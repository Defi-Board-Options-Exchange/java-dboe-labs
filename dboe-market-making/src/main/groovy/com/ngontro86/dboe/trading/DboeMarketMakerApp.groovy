package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import com.ngontro86.market.volatility.VolatilityEstimator
import org.apache.logging.log4j.Logger

import javax.inject.Inject

class DboeMarketMakerApp {

    @Logging
    private Logger logger

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    @Inject
    private ExchangeSpecsLoader dexSpecsLoader

    @Inject
    private DeltaNeutralMarketMaker deltaNeutralMarketMaker

    @Inject
    private VolatilityEstimator volEstimator

    @EntryPoint
    void start() {
        logger.info("Approving Spending Limit if not done first")
        volEstimator.setUnderlyings(dexSpecsLoader.loadOptions(chain).collect { it['underlying'] } as Set)
        logger.info("Start self-hedging Trader")
        deltaNeutralMarketMaker.startSelfHedge()
    }
}
