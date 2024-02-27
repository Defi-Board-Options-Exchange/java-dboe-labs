package com.ngontro86.dboe.ref.updater

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Web3jReadWrite
import com.ngontro86.dboe.ref.updater.helper.DeribitVolDownloaderHelper
import com.ngontro86.dboe.web3j.DBOEGlobalPricingSystem
import com.ngontro86.market.volatility.ParamlessPolynomialSurface
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

import javax.inject.Inject

import static com.ngontro86.dboe.web3j.Utils.padding
import static java.lang.System.currentTimeMillis

class DboeVolSurfaceUpdaterApp {

    @Inject
    @Web3jReadWrite
    private Web3j web3j

    @Inject
    private ContractGasProvider gasProvider

    @Inject
    @Web3jReadWrite
    private RawTransactionManager txnManager

    @ConfigValue(config = "underlyings")
    private Collection underlyings

    @ConfigValue(config = "gpsAddress")
    private String gpsAddress

    @ConfigValue(config = "cutOffTimeToExpiry")
    private Double cutOffTimeToExpiry = 16.0 / 365.0

    @EntryPoint
    void loadAndFitVolSurface() {
        updateGps(pullSurfaces())
    }

    private Map<String, ParamlessPolynomialSurface> pullSurfaces() {
        Map<String, ParamlessPolynomialSurface> surfaces = [:]

        underlyings.each { underlying ->
            surfaces.putIfAbsent(underlying, new ParamlessPolynomialSurface())
            def volData = DeribitVolDownloaderHelper.loadVols(underlying, System.currentTimeMillis(), cutOffTimeToExpiry)
            volData.each { expiryUtc, map ->
                surfaces.get(underlying).addDataThenFit(expiryUtc, map)
            }
        }
        surfaces
    }

    private void updateGps(Map<String, ParamlessPolynomialSurface> surfaces) {
        def gps = DBOEGlobalPricingSystem.load(gpsAddress, web3j, txnManager, gasProvider)

        def moneynesses = [-0.3d, -0.25d, -0.2d, -0.15d, -0.1d, -0.05d, 0.0d, 0.05d, 0.1d, 0.15d, 0.2d, 0.25d, 0.3d]

        // 1h, 6h, 1d, 4d, 2w
        def timeToExpiries = [3600, 6 * 3600, 24 * 3600, 4 * 24 * 3600, 14 * 24 * 3600]

        underlyings.each { underlying ->
            def currentTime = currentTimeMillis()
            timeToExpiries.each { timeToExpiry ->
                def vols = moneynesses.collect { mn ->
                    surfaces.get(underlying).estVol(currentTime + (timeToExpiry * 1000), mn)
                }
                println "${underlying}, ${timeToExpiry}, ${moneynesses}, ${vols}"
                println "Pausing for few secs before publishing to Blockchain"
                sleep 5000

                gps.addVols(
                        padding(32, underlying as byte[]),
                        timeToExpiry as BigInteger,
                        moneynesses.collect { it * Math.pow(10, 18) as BigInteger },
                        vols.collect { it * Math.pow(10, 2) as BigInteger }
                ).send()
                println "DONE ${underlying}, ${timeToExpiry}!"
            }
        }
    }
}
