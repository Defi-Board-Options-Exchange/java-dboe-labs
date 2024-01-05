package com.ngontro86.market.volatility

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.market.volatility.downloader.VolDownloader
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static java.util.concurrent.TimeUnit.MINUTES

class FloatingVolEstimator implements VolatilityEstimator {

    @Logging
    private Logger logger

    @Inject
    private VolDownloader volDownloader

    private Map<String, ParamlessPolynomialSurface> surfaces = [:] as ConcurrentHashMap

    private Set<String> underlyings = [] as Set

    @ConfigValue(config = "reloadSurfaceFreqMin")
    private Integer reloadSurfaceFreqMin = 30

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @ConfigValue(config = "volMap")
    private Collection volMap = ["SOL:155", "BTC:75"]

    private Map<String, Double> vols = [:]

    @PostConstruct
    private void init() {
        volMap.each {
            def toks = it.toString().split(":")
            vols[toks[0]] = Double.valueOf(toks[1]) / 100d
        }
    }

    @Override
    void setUnderlyings(Set<String> underlyings) {
        logger.info("Setting underlyings: ${underlyings}")
        if (this.underlyings.isEmpty()) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                void run() {
                    loadAndFitVolSurface()
                }
            }, reloadSurfaceFreqMin, reloadSurfaceFreqMin, MINUTES)
        }

        this.underlyings.addAll(underlyings)
        loadAndFitVolSurface()
    }

    protected void loadAndFitVolSurface() {
        surfaces.clear()
        underlyings.each { underlying ->
            loadOneSurface(underlying)
        }
    }

    private void loadOneSurface(String underlying) {
        try {
            def volData = volDownloader.loadVols(underlying)
            volData.each { expiryUtc, map ->
                surfaces.putIfAbsent(underlying, new ParamlessPolynomialSurface())
                surfaces.get(underlying).addDataThenFit(expiryUtc, map)
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    @Override
    double impliedVol(String underlying, long expiryUtc, double simpleMoneyness) {
        if (!surfaces.containsKey(underlying)) {
            return vols[underlying]
        }
        return surfaces.get(underlying).estVol(expiryUtc, simpleMoneyness)
    }
}
