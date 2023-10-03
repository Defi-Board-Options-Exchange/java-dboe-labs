package com.ngontro86.market.volatility

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.market.volatility.downloader.VolDownloader
import org.apache.logging.log4j.Logger

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
        underlyings.each { underlying ->
            surfaces.putIfAbsent(underlying, new ParamlessPolynomialSurface())
            def volData = volDownloader.loadVols(underlying)
            volData.each { expiryUtc, map ->
                surfaces.get(underlying).addDataThenFit(expiryUtc, map)
            }
        }
    }

    @Override
    double impliedVol(String underlying, long expiryUtc, double simpleMoneyness) {
        if (!surfaces.containsKey(underlying)) {
            return 0d
        }
        return surfaces.get(underlying).estVol(expiryUtc, simpleMoneyness)
    }
}
