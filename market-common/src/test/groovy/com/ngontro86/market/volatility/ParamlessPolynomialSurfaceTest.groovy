package com.ngontro86.market.volatility

import org.junit.Test

class ParamlessPolynomialSurfaceTest {


    @Test
    void "should est vol"() {
        def surface = new ParamlessPolynomialSurface()

        surface.addDataThenFit(1689519600000,
                [
                        (-0.25): 134.6d,
                        (-0.15): 80.4d,
                        (-0.05): 53.3d,
                        (0d)   : 51.3d,
                        (0.05) : 57.7d,
                        (0.15) : 97.8d,
                        (0.25) : 178.09d
                ])

        surface.addDataThenFit(1689951600000,
                [
                        (-0.25): 83.4d,
                        (-0.15): 71.7d,
                        (-0.05): 62d,
                        (0d)   : 58.3d,
                        (0.05) : 55.5d,
                        (0.15) : 53.4d,
                        (0.25) : 56.8d
                ])

        assert [-0.25, 0, 0.25].collectEntries { moneyness ->
            [(moneyness): surface.estVol(1689778800000, moneyness)]
        } == [
                (-0.25): 103.8805396825397,
                (0)    : 55.49085714285714,
                (0.25) : 105.31546031746032,
        ]
    }
}
