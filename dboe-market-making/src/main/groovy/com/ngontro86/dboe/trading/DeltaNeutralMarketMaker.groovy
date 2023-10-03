package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.dboe.trading.hedge.HedgingExecutor
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import com.ngontro86.market.web3j.Web3OptionPortfolioManager
import org.apache.logging.log4j.Logger

import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static java.util.concurrent.TimeUnit.SECONDS

@DBOEComponent
class DeltaNeutralMarketMaker {

    @Logging
    private Logger logger

    @Inject
    private Web3OptionPortfolioManager optionPm

    @ConfigValue(config = "deltaHedgingFreqSec")
    private Integer deltaHedgingFreqSec = 300

    @Inject
    private HedgingExecutor hedgingExecutor

    @Inject
    private ExchangeSpecsLoader optionLoader

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    void startSelfHedge() {
        logger.info "Set Option tokens..."
        optionPm.initOptions(optionLoader.loadOptions(chain))
        sleep 500

        logger.info "Set underlying token of hedging interest..."
        hedgingExecutor.setUnderlyings(optionLoader.loadOptions(chain).collect { it['underlying'] } as Set)
        sleep 500

        logger.info "Run delta hedge periodically every ${deltaHedgingFreqSec} secs..."
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                deltaHedge()
            }
        }, 0, deltaHedgingFreqSec, SECONDS)
    }

    private void deltaHedge() {
        try {
            optionPm.impliedRisk().each { underlying, risk ->
                println "${underlying}, risk: ${risk}"
                hedgingExecutor.hedgeIfNeeded(underlying, risk)
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
