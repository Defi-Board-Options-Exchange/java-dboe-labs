package com.ngontro86.dboe.analytic

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.dboe.web3j.ERC20
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import com.ngontro86.market.web3j.Web3OptionPortfolioManager
import com.ngontro86.market.web3j.Web3TokenPortfolioManager
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger

import javax.inject.Inject

import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static java.lang.System.currentTimeMillis

class DboeCopyTradeAnalyzerApp {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @Inject
    private ExchangeSpecsLoader exchangeApiLoader

    @Inject
    private Web3OptionPortfolioManager<ERC20> optionPm

    @Inject
    private Web3TokenPortfolioManager<ERC20> tokenPm

    @ConfigValue(config = "chain")
    private String chain = "AVAX"

    private static Collection<String> FIATS = ['USDT', 'USDC']

    @EntryPoint
    void go() {
        logger.info("~~~~~~~ DboeOnchainAnalyzerApp starting ~~~~~~~")

        optionPm.initOptions(exchangeApiLoader.loadOptions(chain))
        tokenPm.initPairs(exchangeApiLoader.loadSpotPairs(chain))

        def addresses = flatDao.queryStringList("select wallet_id from dboe_copytrade_leader_addresses")
        addresses.addAll(flatDao.queryStringList("select user_wallet from dboe_copytrade_subscriptions"))
        println "Number of Address: ${addresses.size()}"

        def refPrices = exchangeApiLoader.refPrices(chain)
        def currentTimeUtc = currentTimeMillis()
        def date = getTimeFormat(currentTimeUtc, "yyyyMMdd")
        def timestamp = getTimeFormat(currentTimeUtc, "yyyyMMddHHmmss")
        addresses.each { addr ->
            def optionRecords = optionPm.portfolio([addr])
                    .findAll { it.value != 0d }
                    .collect { instrId, pos ->
                        [
                                'date'     : date,
                                'wallet'   : addr,
                                'chain'    : chain,
                                'instr_id' : instrId,
                                'pos'      : pos,
                                'ref_px'   : ref(instrId, refPrices),
                                'timestamp': timestamp
                        ]
                    }
            println "Persisting ${optionRecords.size()} Option records for ${addr}"
            flatDao.persist("dboe_copytrade_options_positions", optionRecords)

            def spotRecords = tokenPm.portfolio([addr])
                    .findAll { it.value > 0d }
                    .collect { underlying, pos ->
                        [
                                'date'     : date,
                                'wallet'   : addr,
                                'chain'    : chain,
                                'token'    : underlying,
                                'pos'      : pos,
                                'ref_px'   : ref(underlying, refPrices),
                                'timestamp': timestamp
                        ]
                    }
            println "Persisting ${spotRecords.size()} Spot records for ${addr}"
            flatDao.persist("dboe_copytrade_spot_positions", spotRecords)
        }

        while (true) {
            logger.info("Pausing for 100 ms ...")
            Utils.pause(100)
        }
    }

    private double ref(String id, Map<String, Double> refPrices) {
        if (FIATS.contains(id)) {
            return 1d
        }
        return refPrices.getOrDefault(id, 0d)
    }

}
