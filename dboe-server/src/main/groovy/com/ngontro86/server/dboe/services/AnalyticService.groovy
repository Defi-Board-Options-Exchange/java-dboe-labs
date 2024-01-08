package com.ngontro86.server.dboe.services

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.ERC20
import com.ngontro86.market.web3j.GreekRisk
import com.ngontro86.market.web3j.Web3OptionPortfolioManager
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

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                loadActiveOptions()
            }
        }, 1, 30, TimeUnit.MINUTES)
    }

    private void loadActiveOptions() {
        def options = cepEngine.queryMap("select * from DboeOptionInstrWin")
        logger.info("Loading ${options.size()} Options...")
        optionPortfolioManager.initOptions(options)
    }

    Map<String, GreekRisk> greeks(Collection<String> wallets) {
        Map<String, GreekRisk> risks = [:]
        def optionGreeks = cepEngine.queryMap("select * from DboeOptionGreekWin").collectEntries {
            [(it['instr_id']): it]
        } as Map<String, Map>

        optionPortfolioManager.portfolio(wallets).each { instrId, pos ->
            def opt = optionPortfolioManager.optionByInstrId.get(instrId).first() as Map
            def underlying = opt['underlying']
            risks.putIfAbsent(underlying, new GreekRisk())

            if (optionGreeks.containsKey(instrId)) {
                def spot = optionGreeks.get(instrId)['spot']
                def greek = optionGreeks.get(instrId)['greek']
                risks.get(underlying).delta += pos * spot * greek[1]
                risks.get(underlying).vega += 100d * pos * greek[2]
                risks.get(underlying).gamma += pos * greek[3]
                risks.get(underlying).theta += 365d * pos * greek[4]
            }
        }
        return risks
    }
}
