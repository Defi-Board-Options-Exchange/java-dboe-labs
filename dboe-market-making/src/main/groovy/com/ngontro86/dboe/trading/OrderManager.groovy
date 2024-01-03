package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.trading.utils.MarketMakingUtils
import com.ngontro86.dboe.web3j.Utils
import com.ngontro86.dboe.web3j.smartcontract.ClobManager
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import com.ngontro86.market.price.SpotPricer
import com.ngontro86.market.time.TimeSource
import com.ngontro86.market.volatility.VolatilityEstimator
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static com.ngontro86.market.pricing.Black76.price
import static com.ngontro86.market.pricing.OptionKind.Call
import static com.ngontro86.market.pricing.OptionKind.Put
import static java.util.concurrent.TimeUnit.MINUTES

@DBOEComponent
class OrderManager<T> {

    @Logging
    private Logger logger

    @Inject
    private ClobManager<T> clobManager

    @Inject
    private ExchangeSpecsLoader dexSpecsLoader

    private Map<String, T> clobMap = [:]

    @ConfigValue(config = "defaultTimeoutMin")
    private Integer defaultTimeoutMin = 3600

    @ConfigValue(config = "defaultQuotingNotional")
    private Integer defaultQuotingNotional = 75

    @ConfigValue(config = "defaultSellQuotingNotional")
    private Integer defaultSellQuotingNotional = 5

    @ConfigValue(config = "maxCollateral")
    private Double maxCollateral = 500.0

    @ConfigValue(config = "qtyIncrementPct")
    private Double qtyIncrementPct = 25.0

    @ConfigValue(config = "spreadingCheckMin")
    private Integer spreadingCheckMin = 10

    @ConfigValue(config = "calibrateRefBuiltin")
    private Boolean calibrateRefBuiltin = false

    @ConfigValue(config = "calibrateRefMin")
    private Integer calibrateRefMin = 2


    @ConfigValue(config = "tier1Underlyings")
    private Collection tier1Underlyings = ['ETH', 'BTC']

    @ConfigValue(config = "tier1FocusedSpreads")
    private Collection tier1FocusedSpreads = ['5', '6']

    @ConfigValue(config = "tier2FocusedSpreads")
    private Collection tier2FocusedSpreads = ['5', '6']

    @ConfigValue(config = "longestTimeToExpiryInHour")
    private Double longestTimeToExpiryInHour = 26.0

    @Inject
    private VolatilityEstimator volEstimator

    @Inject
    private SpotPricer spotPricer

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    @Inject
    private TimeSource timeSource

    @ConfigValue(config = "priceOracleEnabled")
    private Boolean priceOracleEnabled = false

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)

    @PostConstruct
    private void init() {
        def clobInfo = dexSpecsLoader.loadClobs(chain)
        logger.info("Found: ${clobInfo.size()} CLOB for chain: ${chain}")

        def uniqueClobAddresses = clobInfo.collect { it['ob_sc_address'] } as HashSet
        uniqueClobAddresses.each { clobMap[it] = clobManager.load(it) }
    }

    void startSpreading() {
        logger.info("Start Spreading every ${spreadingCheckMin} mins")
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                spreading()
            }
        }, 0, spreadingCheckMin, MINUTES)

        logger.info("Start Calibrate ref every ${calibrateRefMin} mins")
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                calibrateRefs()
            }
        }, 0, calibrateRefMin, MINUTES)
    }

    private void calibrateRefs() {
        if (calibrateRefBuiltin) {
            def options = dexSpecsLoader.loadOptions(chain)
            logger.info("Found: ${options.size()} options for chain: ${chain}")
            println "CalibrateReds: Found: ${options.size()} options for chain: ${chain}"
            def fairPrices = options.collectEntries {
                [(it['instr_id']): priceDboe(it)]
            }
            options.each { opt ->
                calibrateRefOneOption(fairPrices, opt)
            }
        }
    }

    private void calibrateRefOneOption(Map<Object, Object> fairPrices, opt) {
        try {
            def fp = fairPrices.get(opt['instr_id']) * opt['underlying_px_scale']
            if (priceOracleEnabled) {
                println "Calibrating ref px: ${opt['instr_id']}, fp: ${fp}"
                clobManager.calibrateRef(clobMap[opt['ob_address']], opt['instr_id'])
            } else {
                println "Set ref px: ${opt['instr_id']}, fp: ${fp}"
                clobManager.setRef(clobMap[opt['ob_address']], opt['instr_id'], fp as long)
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private void spreading() {
        def options = dexSpecsLoader.loadOptions(chain).findAll {
            (Utils.getTimeUtc(it['expiry'], it['ltt']) - timeSource.currentTimeMilliSec()) / 3600000d <= longestTimeToExpiryInHour
        }

        logger.info("Found: ${options.size()} options for chain: ${chain}")
        println "Spreading: Found: ${options.size()} options for chain: ${chain}"

        options.each { opt ->
            spreadingOneOption(opt)
        }
    }

    private void spreadingOneOption(opt) {
        try {
            def quotes = clobManager.userQuotes(clobMap[opt['ob_address']], opt['instr_id'])
            def fp = clobManager.currentRefPx(clobMap[opt['ob_address']], opt['instr_id']) / opt['underlying_px_scale']
            if (fp == 0d) {
                return
            }
            [true, false].each { bs ->
                def amount = (bs ? defaultQuotingNotional / fp : Math.min(defaultSellQuotingNotional / fp, maxCollateral / (Math.abs(opt['strike'] - opt['cond_strike']) - fp)))
                focusedSpreads(opt['underlying']).each { lvl ->
                    def onchainQs = quotes.findAll { it.buySell == bs && it.pxLevel == Integer.valueOf(lvl) }
                    if (onchainQs.isEmpty() || onchainQs.first().amount == 0) {
                        amount *= (1.0 + Math.random() * qtyIncrementPct / 100.0)
                        def orderTimeOut = MarketMakingUtils.bestOrderTimeOutInMin(Utils.getTimeUtc(opt['expiry'], opt['ltt']), timeSource.currentTimeMilliSec(), Integer.valueOf(lvl))
                        if (orderTimeOut > 30) {
                            println "toPrice(): ${opt['instr_id']}, BuySell: ${bs ? 'B' : 'S'}, amt: ${amount}, ${fp}, lvl: ${lvl}, timeout: ${orderTimeOut}"
                            clobManager.toPrice(clobMap[opt['ob_address']], opt['instr_id'], bs, amount, Integer.valueOf(lvl), Math.min(orderTimeOut, defaultTimeoutMin))
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private double priceDboe(Map it) {
        try {
            def underlying = it['underlying']
            def spot = spotPricer.spot(underlying)
            def timeToExpiry = Utils.timeDiffInYear(it['expiry'], it['ltt'], timeSource.currentTimeMilliSec())
            def vol1 = volEstimator.impliedVol(underlying, Utils.getTimeUtc(it['expiry'], it['ltt']), Math.log(it['strike'] / spot)) / 100d
            def vol2 = volEstimator.impliedVol(underlying, Utils.getTimeUtc(it['expiry'], it['ltt']), Math.log(it['cond_strike'] / spot)) / 100d
            def px1 = price(option: [kind: it['kind'] == 'Call' ? Call : Put, atm: spot, strike: it['strike'], r: 0.0, t: timeToExpiry, vol: vol1])
            def px2 = price(option: [kind: it['kind'] == 'Call' ? Call : Put, atm: spot, strike: it['cond_strike'], r: 0.0, t: timeToExpiry, vol: vol2])

            return Math.max(0d, px1 - px2)
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private Collection focusedSpreads(String und) {
        return tier1Underlyings.contains(und) ? tier1FocusedSpreads : tier2FocusedSpreads
    }
}
