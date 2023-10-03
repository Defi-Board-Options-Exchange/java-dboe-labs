package com.ngontro86.market.common.regression

import org.junit.Test


class MultiLinearRegTest {

    @Test
    void "should regress"() {
        def linearReg = new MultiLinearReg("", 2)
        [
                99d : [25d, 25d],
                102d: [35d, 22d],
                105d: [29d, 18d],
                102d: [20d, 19d],
                103d: [20d, 23d],
                107d: [25d, 17d],
                97d : [15d, 25d]
        ].each { y, x ->
            linearReg.addData(x as double[], y)
        }

        linearReg.runRegression()

        def params = linearReg.estimateRegressionParameters()
        println params
        assert params == [114.92158566548801, 0.17070174387247727, -0.7827050997782676]
    }
}
