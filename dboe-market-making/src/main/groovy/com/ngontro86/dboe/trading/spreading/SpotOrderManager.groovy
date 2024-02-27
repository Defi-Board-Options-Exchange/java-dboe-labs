package com.ngontro86.dboe.trading.spreading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.smartcontract.SpotClobManager
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static java.util.concurrent.TimeUnit.MINUTES

@DBOEComponent
class SpotOrderManager<T> {

    @Logging
    private Logger logger

    @Inject
    private SpotClobManager<T> clobManager

    @Inject
    private ExchangeSpecsLoader dexSpecsLoader

    private Map<String, T> clobMap = [:]

    @ConfigValue(config = "defaultTimeoutMin")
    private Integer defaultTimeoutMin = 3600

    @ConfigValue(config = "defaultQuotingNotional")
    private Integer defaultQuotingNotional = 30

    @ConfigValue(config = "qtyIncrementPct")
    private Double qtyIncrementPct = 25.0

    @ConfigValue(config = "spreadingCheckMin")
    private Integer spreadingCheckMin = 30

    @ConfigValue(config = "spotFocusedSpreads")
    private Collection spotFocusedSpreads = ['5', '6']

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)

    @PostConstruct
    private void init() {
        def spotPairs = dexSpecsLoader.loadSpotPairs(chain)
        logger.info("Found: ${spotPairs.size()} Spot Pairs for chain: ${chain}")
        spotPairs.each {
            clobMap[it['address']] = clobManager.load(it['address'])
        }
    }

    void startSpreading() {
        logger.info("Start Spreading every ${spreadingCheckMin} mins")
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                spreading()
            }
        }, 0, spreadingCheckMin, MINUTES)
    }

    private void spreading() {
        def spotPairs = dexSpecsLoader.loadSpotPairs(chain)
        println "Spreading: Found: ${spotPairs.size()} options for chain: ${chain}"
        Collections.shuffle(spotPairs)
        spotPairs.each { pair ->
            println "Spreading on ${pair['base_name']}/${pair['quote_name']}"
            spreadingOne(pair)
        }
    }

    private void spreadingOne(pair) {
        try {
            def fp = clobManager.currentRefPx(clobMap[pair['address']])
            if (fp == 0d) {
                println "Zero ref px, ${pair['instr_id']}..."
                return
            }
            def quotes = clobManager.userQuotes(clobMap[pair['address']])
            [true, false].each { bs ->
                def amount = defaultQuotingNotional / fp
                spotFocusedSpreads.each { lvl ->
                    def onchainQs = quotes.findAll { it.buySell == bs && it.pxLevel == Integer.valueOf(lvl) }
                    if (onchainQs.isEmpty() || onchainQs.first().amount == 0) {
                        amount *= (1.0 + Math.random() * qtyIncrementPct / 100.0)
                        amount = Math.min(amount, 999000.0)
                        println "Spot:toPrice(): ${pair['base_name']}/${pair['quote_name']}, BuySell: ${bs ? 'B' : 'S'}, amt: ${amount}, ${fp}, lvl: ${lvl}, timeout: ${defaultTimeoutMin}"
                        clobManager.toPrice(clobMap[pair['address']], bs, amount, Integer.valueOf(lvl), defaultTimeoutMin)
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
