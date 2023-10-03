package com.ngontro86.market.web3j

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.Utils
import com.ngontro86.dboe.web3j.token.TokenLoader
import com.ngontro86.market.pricing.Black76
import com.ngontro86.market.price.SpotPricer
import com.ngontro86.market.time.TimeSource
import com.ngontro86.market.volatility.VolatilityEstimator
import org.apache.logging.log4j.Logger

import javax.inject.Inject

import static com.ngontro86.market.pricing.OptionKind.Call
import static com.ngontro86.market.pricing.OptionKind.Put

@DBOEComponent
class Web3OptionPortfolioManager<T> {

    @Logging
    private Logger logger

    @ConfigValue(config = "portfolioAddresses")
    private Collection<String> portfolioAddresses

    @Inject
    private TokenLoader<T> tokenLoader

    @Inject
    private VolatilityEstimator volEstimator

    @Inject
    private SpotPricer spotPricer

    @Inject
    private TimeSource time

    private Map<String, T> longTokens = [:]
    private Map<String, T> shortTokens = [:]
    private Map<String, Collection> optionByInstrId = [:]
    private Map<String, Double> positionPortfolio = [:]

    private final double DBOE_TOKEN_SCALE = Math.pow(10, 18)

    void setPortfolioWallets(Collection<String> addresses) {
        logger.info("Setting portfolio address: from ${portfolioAddresses} to ${addresses}")
        portfolioAddresses = addresses
    }

    void initOptions(Collection<Map> options) {
        options.each { option ->
            loadLongShortOption(option)
        }
        optionByInstrId = options.groupBy { it['instr_id'] }
    }

    Map<String, GreekRisk> impliedRisk() {
        try {
            refreshPortfolio()

            def greekRisks = [:] as Map<String, GreekRisk>
            positionPortfolio.each { instrId, pos ->

                def opt = optionByInstrId.get(instrId).first() as Map

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

            return greekRisks
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private void refreshPortfolio() {
        longTokens.each { instrId, longToken ->
            positionPortfolio[instrId] = portfolioAddresses.collect { addr ->
                tokenLoader.balanceOf(longToken, addr) - tokenLoader.balanceOf(shortTokens[instrId], addr)
            }.sum() / DBOE_TOKEN_SCALE
        }
        logger.info "Portfolio: ${positionPortfolio}"
        println "Portfolio: ${positionPortfolio}"
    }

    private void loadLongShortOption(Map option) {
        String issueId = option['instr_id']
        longTokens[issueId] = tokenLoader.load(option['long_contract_address'])
        shortTokens[issueId] = tokenLoader.load(option['short_contract_address'])
    }
}
