package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.smartcontract.ClobManager
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import com.ngontro86.market.price.SpotPricer
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Lazy(false)
@DBOEComponent
class OptionRefPriceBySpotMoveCalibrator<T> {
    @Logging
    private Logger logger

    @Inject
    private ClobManager<T> clobManager

    @Inject
    private ExchangeSpecsLoader dexSpecsLoader

    private Map<String, T> clobMap = [:]

    @Inject
    private SpotPricer spotPricer

    @ConfigValue(config = "checkSpotMoveMin")
    private Integer checkSpotMoveMin = 2

    @ConfigValue(config = "spotMoveThresholdBps")
    private Double spotMoveThresholdBps = 50.0

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    @ConfigValue(config = "priceOracleEnabled")
    private Boolean priceOracleEnabled = false

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)

    private Map<String, Double> prevSpots = [:]

    @PostConstruct
    private void init() {
        reload()

        println "Scheduling reloading of CLOB and Underlyings for every 15 min..."
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                reload()
            }
        }, 1, 15, TimeUnit.MINUTES)

        println "Scheduling spot monitoring for every ${checkSpotMoveMin} min..."
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                updateRefBySpotMove()
            }
        }, 1, checkSpotMoveMin, TimeUnit.MINUTES)
    }

    void reload() {
        def clobInfo = dexSpecsLoader.loadClobs(chain)
        logger.info("Found: ${clobInfo.size()} CLOB for chain: ${chain}")

        clobInfo.groupBy { it['ob_sc_address'] }
                .keySet()
                .each { clobMap[it] = clobManager.load(it) }

        underlyings().each {
            if (!prevSpots.containsKey(it) && spotPricer.spot(it) != null) {
                prevSpots.put(it, spotPricer.spot(it))
            }
        }
    }

    void updateRefBySpotMove() {
        try {
            underlyings().each { und ->
                def latestSpot = spotPricer.spot(und)
                println "${und}, latest spot: ${latestSpot}, prev recorded: ${prevSpots.get(und)}"
                if (prevSpots.containsKey(und) && Math.abs(latestSpot / prevSpots.get(und) - 1d) * 10000d >= spotMoveThresholdBps) {
                    updateRefs(und)
                    prevSpots.put(und, latestSpot)
                }
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    void updateRefs(String underlying) {
        if (priceOracleEnabled) {
            def expClobPair = dexSpecsLoader.loadOptions(chain).findAll {
                it['underlying'] == underlying && clobMap.containsKey(it['ob_address'])
            }.collectEntries { [(it['expiry']): it['ob_address']] }

            println "Found: ${expClobPair.size()} expiry and clob pair to update..."

            expClobPair.each { exp, obAddr ->
                println "Calibrating Refs: ${chain}, ${underlying}, ${exp}"
                clobManager.calibrateRefs(clobMap.get(obAddr), underlying, exp)
            }
        }
    }

    private Set<String> underlyings() {
        return dexSpecsLoader.loadOptions(chain).groupBy { it['underlying'] }.keySet()
    }
}
