package com.ngontro86.dboe.trading

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.market.price.SpotPricerProvider
import com.ngontro86.market.time.TimeSourceProvider
import com.ngontro86.market.web3j.GreekRisk
import com.ngontro86.market.web3j.Web3OptionPortfolioManager
import com.ngontro86.market.volatility.VolEstimatorProvider
import com.ngontro86.dboe.web3j.token.TokenLoaderProvider
import org.junit.Before
import org.junit.Test

class Web3OptionPortfolioManagerTest {
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

        env = ComponentEnv.env([Web3OptionPortfolioManager,
                                TokenLoaderProvider,
                                VolEstimatorProvider,
                                SpotPricerProvider,
                                TimeSourceProvider,
                                ConfigValuePostProcessor,
                                LoggerPostProcessor])
    }

    @Test
    void "should able to measure risk for a short portfolio"() {
        def web3OptionPM = env.component(Web3OptionPortfolioManager)
        web3OptionPM.initOptions(
                [
                        [
                                "cond_strike"           : 2300,
                                "kind"                  : "Call",
                                "strike"                : 2000,
                                "underlying"            : "ETH",
                                "ltt"                   : 150000,
                                "instr_id"              : "E2000C616",
                                "long_contract_address" : "longtoken1",
                                "expiry"                : 20230616,
                                "short_contract_address": "shorttoken1"
                        ],
                        [
                                "cond_strike"           : 1300,
                                "kind"                  : "Put",
                                "strike"                : 1600,
                                "underlying"            : "ETH",
                                "ltt"                   : 150000,
                                "instr_id"              : "E1600P616",
                                "long_contract_address" : "longtoken2",
                                "expiry"                : 20230616,
                                "short_contract_address": "shorttoken2"
                        ]
                ]
        )

        assert web3OptionPM.impliedRisk()['ETH'] == new GreekRisk(-0.6419650621761294, -136.22798870924078, -0.009145006048482113, 6275.412730617713)
    }

    @Test
    void "should able to measure risk for a long portfolio"() {
        def web3OptionPM = env.component(Web3OptionPortfolioManager)

        web3OptionPM.setPortfolioWallets(['mmwallet'])
        web3OptionPM.initOptions(
                [
                        [
                                "cond_strike"           : 2300,
                                "kind"                  : "Call",
                                "strike"                : 2000,
                                "underlying"            : "ETH",
                                "ltt"                   : 150000,
                                "instr_id"              : "E2000C616",
                                "long_contract_address" : "longtoken1",
                                "expiry"                : 20230616,
                                "short_contract_address": "shorttoken1"
                        ],
                        [
                                "cond_strike"           : 1300,
                                "kind"                  : "Put",
                                "strike"                : 1600,
                                "underlying"            : "ETH",
                                "ltt"                   : 150000,
                                "instr_id"              : "E1600P616",
                                "long_contract_address" : "longtoken2",
                                "expiry"                : 20230616,
                                "short_contract_address": "shorttoken2"
                        ]
                ]
        )
        assert web3OptionPM.impliedRisk()['ETH'] == new GreekRisk(1.4974218711525857, 318.17134694737456, 0.021358891956466713, -14656.72759371731)
    }

}
