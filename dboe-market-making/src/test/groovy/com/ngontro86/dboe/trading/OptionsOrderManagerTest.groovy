package com.ngontro86.dboe.trading

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.dboe.trading.spreading.OptionsOrderManager
import com.ngontro86.market.price.SpotPricerProvider
import com.ngontro86.market.time.TimeSourceProvider
import com.ngontro86.market.web3j.Web3OptionPortfolioManager
import com.ngontro86.market.volatility.VolEstimatorProvider
import com.ngontro86.dboe.trading.test.InMemoryExchangeSpecsLoader
import com.ngontro86.dboe.web3j.token.TokenLoaderProvider
import org.junit.Before
import org.junit.Test

class OptionsOrderManagerTest {

    ComponentEnv env

    @Before
    void "init"() {
        [
                'portfolioAddresses'  : 'quantvu86',
                'volModel'            : 'Flat',
                'volMap'              : 'ETH:61,BTC:58',
                'spotPriceSource'     : 'Fixed',
                'spots'               : 'ETH:1920.5,BTC:30450.7',
                'realBlockchain'      : 'false',
                'blockchainConfigFile': 'prebuiltBlockchain',
                'currentTimeUtc'      : '1686718800000',
                'timeSource'          : 'fixed'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([OptionsOrderManager,
                                //InMemoryClobManager,
                                Web3OptionPortfolioManager,
                                TokenLoaderProvider,
                                InMemoryExchangeSpecsLoader,
                                VolEstimatorProvider,
                                SpotPricerProvider,
                                TimeSourceProvider,
                                ConfigValuePostProcessor,
                                LoggerPostProcessor])
    }

    @Test
    void "should init self hedge"() {
        env.component(OptionsOrderManager).startSpreading()
        sleep 10000
        println "DONE!"
    }
}
