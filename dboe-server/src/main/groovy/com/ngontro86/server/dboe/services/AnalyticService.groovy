package com.ngontro86.server.dboe.services

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.ERC20
import com.ngontro86.market.web3j.GreekRisk
import com.ngontro86.market.web3j.Web3OptionPortfolioManager
import com.ngontro86.market.web3j.Web3TokenPortfolioManager
import com.ngontro86.server.dboe.services.analytic.PortfolioRisk
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@DBOEComponent
class AnalyticService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cepEngine

    @Inject
    private Web3OptionPortfolioManager<ERC20> optionPortfolioManager
    @Inject
    private Web3TokenPortfolioManager tokenPortfolioManager

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                loadActiveOptions()
            }
        }, 1, 10, TimeUnit.MINUTES)
    }

    private void loadActiveOptions() {
        def options = cepEngine.queryMap("select * from DboeOptionInstrWin")
        logger.info("Loading ${options.size()} Options...")
        optionPortfolioManager.initOptions(options)
        def spots = cepEngine.queryMap("select * from DboeSpotMarketOverviewWin")
        logger.info("Loading ${spots.size()} Spot Pair...")
        tokenPortfolioManager.initPairs(spots)
    }

    private double usdtBalance(Collection<String> wallets) {
        try {
            def rec = tokenPortfolioManager.portfolio(wallets).find { it.key == 'USDT' }
            return rec == null ? 0d : rec.value
        } catch (Exception e) {
            logger.error(e)
            return 0d
        }
    }

    PortfolioRisk greeks(Set<String> underlyings, Collection<String> wallets) {
        Map<String, Double> vals = ['USDT': usdtBalance(wallets)]
        Map<String, GreekRisk> risks = [:]
        Map<String, Map<String, Double>> optionPos = [:]
        def optionGreeks = cepEngine.queryMap("select * from DboeOptionGreekWin").collectEntries {
            [(it['instr_id']): it]
        } as Map<String, Map>

        optionPortfolioManager.portfolio(underlyings, wallets).findAll { it.value != 0d }.each { instrId, pos ->
            def opt = optionPortfolioManager.optionByInstrId.get(instrId).first() as Map
            def underlying = opt['underlying']
            risks.putIfAbsent(underlying, new GreekRisk())
            vals.putIfAbsent(underlying, 0d)
            optionPos.putIfAbsent(underlying, [:])
            optionPos.get(underlying).put(instrId, pos)

            if (optionGreeks.containsKey(instrId)) {
                def spot = optionGreeks.get(instrId)['spot']
                def refPx = optionGreeks.get(instrId)['ref']
                def greek = optionGreeks.get(instrId)['greek']

                vals.put(underlying, vals.get(underlying) + pos * (pos < 0d ? (refPx - Math.abs(opt['strike'] - opt['cond_strike'])) : refPx))

                risks.get(underlying).delta += pos * spot * greek[1]
                risks.get(underlying).vega += pos * greek[2]
                risks.get(underlying).gamma += pos * spot * spot * greek[3]
                risks.get(underlying).theta += pos * greek[4] / 365d
            }
        }

        return new PortfolioRisk(vals: vals, greeks: risks, optionPos: optionPos)
    }

    Collection<Map> dmmQuote(String dmmAddr, String instrId) {
        cepEngine.queryMap("select * from DboeDmmQuoteWin(dmm='${dmmAddr}',instr_id='${instrId}')")
    }

    Collection<Map> dmmQuotes(String dmmAddr) {
        cepEngine.queryMap("select * from DboeDmmQuoteWin(dmm='${dmmAddr}')")
    }
}
