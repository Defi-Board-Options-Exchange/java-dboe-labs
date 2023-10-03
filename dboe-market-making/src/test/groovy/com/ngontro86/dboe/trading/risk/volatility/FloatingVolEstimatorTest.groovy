package com.ngontro86.dboe.trading.risk.volatility

import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.component.testing.ComponentEnv
import com.ngontro86.market.volatility.downloader.VolDownloader
import com.ngontro86.market.volatility.FloatingVolEstimator
import org.junit.Before
import org.junit.Test

class FloatingVolEstimatorTest {

    ComponentEnv env

    @Before
    void init() {
        env = ComponentEnv.env([FloatingVolEstimator, TestVolDownloader, LoggerPostProcessor])
    }

    @Test
    void "should estimate vol"() {
        def floatingVolEstimator = env.component(FloatingVolEstimator)
        floatingVolEstimator.setUnderlyings(['ETH'] as Set)
        floatingVolEstimator.loadAndFitVolSurface()
        assert floatingVolEstimator.impliedVol('ETH', 1689778800000, 0d) == 55.49085714285714
    }


    @DBOEComponent
    static class TestVolDownloader implements VolDownloader {
        @Override
        Map<Long, Map<Double, Double>> loadVols(String underlying) {
            return [
                    1689519600000L: [
                            (-0.25): 134.6d,
                            (-0.15): 80.4d,
                            (-0.05): 53.3d,
                            (0d)   : 51.3d,
                            (0.05) : 57.7d,
                            (0.15) : 97.8d,
                            (0.25) : 178.09d
                    ],
                    1689951600000L: [
                            (-0.25): 83.4d,
                            (-0.15): 71.7d,
                            (-0.05): 62d,
                            (0d)   : 58.3d,
                            (0.05) : 55.5d,
                            (0.15) : 53.4d,
                            (0.25) : 56.8d
                    ]
            ]
        }
    }
}
