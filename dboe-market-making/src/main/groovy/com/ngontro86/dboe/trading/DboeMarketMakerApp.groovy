package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.trading.spreading.OptionsOrderManager
import com.ngontro86.dboe.trading.spreading.SpotOrderManager
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
    private OptionsSpendingLimitApprovalManager optionsSpendingLimitApprovalManager

    @Inject
    private SpotSpendingLimitApprovalManager spotSpendingLimitApprovalManager

    @Inject
    private OptionRefPriceBySpotMoveCalibrator refPriceBySpotMoveCalibrator

    @Inject
    private OptionsOrderManager orderManager

    @Inject
    private SpotOrderManager spotOrderManager

    @ConfigValue(config = "spotEnabled")
    private Boolean spotEnabled = true

    @ConfigValue(config = "optionsEnabled")
    private Boolean optionsEnabled = true

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

        if(optionsEnabled) {
            logger.info("Start Options Spreading with On-Chain CLOB...")
            orderManager.startSpreading()
        }
        if (spotEnabled) {
            logger.info("Start Spot Spreading with On-Chain CLOB...")
            spotOrderManager.startSpreading()
        }
    }
}
