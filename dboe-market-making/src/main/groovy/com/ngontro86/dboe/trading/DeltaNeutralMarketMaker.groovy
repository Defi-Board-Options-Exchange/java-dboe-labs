package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.trading.hedge.HedgingExecutor
import com.ngontro86.dboe.web3j.Utils
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import com.ngontro86.market.price.SpotPricer
import com.ngontro86.market.pricing.Black76
import com.ngontro86.market.time.TimeSource
import com.ngontro86.market.volatility.VolatilityEstimator
import com.ngontro86.market.web3j.GreekRisk
import com.ngontro86.market.web3j.Web3OptionPortfolioManager
import com.ngontro86.market.web3j.Web3TokenPortfolioManager
import org.apache.logging.log4j.Logger

import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static com.ngontro86.market.pricing.OptionKind.Call
import static com.ngontro86.market.pricing.OptionKind.Put
import static java.util.concurrent.TimeUnit.SECONDS

@DBOEComponent
class DeltaNeutralMarketMaker {

    @Logging
    private Logger logger

    @ConfigValue(config = "portfolioAddresses")
    private Collection<String> portfolioAddresses

    @Inject
    private Web3OptionPortfolioManager optionPm

    @Inject
    private Web3TokenPortfolioManager tokenPm

    @Inject
    private VolatilityEstimator volEstimator

    @Inject
    private SpotPricer spotPricer

    @Inject
    private TimeSource time

    @ConfigValue(config = "deltaHedgingFreqSec")
    private Integer deltaHedgingFreqSec = 300

    @Inject
    private HedgingExecutor hedgingExecutor

    @Inject
    private ExchangeSpecsLoader exchangeApiLoader

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    private static Collection<String> FIATS = ['USDT', 'USDC']

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    void startSelfHedge() {
        logger.info "Set Option tokens..."
        optionPm.initOptions(exchangeApiLoader.loadOptions(chain))
        tokenPm.initPairs(exchangeApiLoader.loadSpotPairs(chain))
        sleep 500

        logger.info "Set underlying token of hedging interest..."
        def underlyings = exchangeApiLoader.loadOptions(chain).collect { it['underlying'] } as Set
        underlyings.addAll(exchangeApiLoader.loadSpotPairs(chain).collect { it['base_underlying'] })
        hedgingExecutor.setUnderlyings(underlyings)
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
            def risks = impliedRisk()
            risks.each {
                println "Underlying: ${it.key}, greeks: ${it.value.toString()}"
            }
            risks.findAll { !FIATS.contains(it.key) }.each { underlying, risk ->
                println "${underlying}, risk: ${risk}"
                hedgingExecutor.hedgeIfNeeded(underlying, risk)
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private Map<String, GreekRisk> impliedRisk() {
        def greekRisks = [:] as Map<String, GreekRisk>
        try {
            println "Calculating Options portfolio"
            optionPm.portfolio(portfolioAddresses).findAll { it.value > 0d }.each { instrId, pos ->
                def opt = optionPm.optionByInstrId.get(instrId).first() as Map
                println "Option risk, pos: ${pos}, opt: ${opt}"
                greekRisks.putIfAbsent(opt['underlying'], new GreekRisk())

                def ttExpiry = Utils.timeDiffInYear(opt['expiry'], opt['ltt'], time.currentTimeMilliSec())
                def timeExpiryUtc = Utils.getTimeUtc(opt['expiry'], opt['ltt'])

                def greek1 = Black76.greek option: [
                        kind  : opt['kind'] == 'Call' ? Call : Put,
                        atm   : spotPricer.spot(opt['underlying']),
                        strike: opt['strike'],
                        r     : 0.0,
                        t     : ttExpiry,
                        vol   : volEstimator.impliedVol(opt['underlying'], timeExpiryUtc, Math.log(opt['strike'] / spotPricer.spot(opt['underlying'])))
                ]

                def greek2 = Black76.greek option: [
                        kind  : opt['kind'] == 'Call' ? Call : Put,
                        atm   : spotPricer.spot(opt['underlying']),
                        strike: opt['cond_strike'],
                        r     : 0.0,
                        t     : ttExpiry,
                        vol   : volEstimator.impliedVol(opt['underlying'], timeExpiryUtc, Math.log(opt['cond_strike'] / spotPricer.spot(opt['underlying'])))
                ]

                def risk = greekRisks.get(opt['underlying'])
                risk.delta += pos * (greek1['delta'] - greek2['delta'])
                risk.vega += pos * (greek1['vega'] - greek2['vega'])
                risk.gamma += pos * (greek1['gamma'] - greek2['gamma'])
                risk.theta += pos * (greek1['theta'] - greek2['theta'])
            }

            println "Calculating Spot portfolio"
            tokenPm.portfolio(portfolioAddresses).findAll { it.value > 0d }.each { underlying, pos ->
                greekRisks.putIfAbsent(underlying, new GreekRisk())
                println "Spot risk, underlying: ${underlying}, pos: ${pos}"
                greekRisks.get(underlying).delta += pos
            }

            return greekRisks
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
