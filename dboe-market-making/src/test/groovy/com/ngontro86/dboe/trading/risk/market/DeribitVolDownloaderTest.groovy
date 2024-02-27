package com.ngontro86.dboe.trading.risk.market

import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.market.time.TimeSourceProvider
import com.ngontro86.market.volatility.downloader.DeribitVolDownloader
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoints
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class DeribitVolDownloaderTest {

    ComponentEnv env

    @Before
    void "init"() {
        env = ComponentEnv.env([DeribitVolDownloader, LoggerPostProcessor, TimeSourceProvider])
    }

    @Test
    void "should be able to load vol surface from Deribit"() {
        def volDownloader = env.component(DeribitVolDownloader)
        ['BTC', 'ETH'].each { und ->
            volDownloader.loadVols(und).each { ttExpiry, map ->
                map.each { moneyness, vol ->
                    println "${und},${ttExpiry},${moneyness},${vol}"
                }
            }
        }
    }

    @Test
    void "should be able to fit a vol surface"() {
        def volDownloader = env.component(DeribitVolDownloader)
        volDownloader.loadVols('ETH').each { ttExpiry, map ->
            def obs = new WeightedObservedPoints()
            map.each { moneyness, vol ->
                //println "${ttExpiry}, ${moneyness}, ${vol}"
                obs.add(moneyness, vol)
            }
            def fitter = PolynomialCurveFitter.create(3)
            def coeff = fitter.fit(obs.toList())
            //println "${ttExpiry}, ${coeff}"
            def pf = new PolynomialFunction(coeff)
            [-0.25, -0.15, -0.05, 0, 0.05, 0.15, 0.25].each {
                println "${ttExpiry}, $it, ${pf.value(it)}"
            }
        }
    }

}
