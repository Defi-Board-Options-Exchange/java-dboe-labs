package com.ngontro86.market.common.regression

import com.ngontro86.restful.common.json.JsonUtils
import com.ngontro86.utils.ResourcesUtils
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

    @Test
    void "should regress over a vol curve"() {
        def vols = JsonUtils.fromJson(ResourcesUtils.content('impliedVol.txt'), Collection) as Collection<Map>
        vols.groupBy { it['expiry'] }.each { expiry, data ->
            def linearReg = new MultiLinearReg("", 2)
            data.each { row ->
                linearReg.addData([row['moneyness'], row['moneyness'] * row['moneyness']] as double[], row['vol'])
            }
            linearReg.runRegression()
            def params = linearReg.estimateRegressionParameters()
            data.each { row ->
                def xData = [1.0, row['moneyness'], row['moneyness'] * row['moneyness']]
                println "${expiry}, ${row['moneyness']}, ${row['vol']}, ${[xData, params].transpose().collect { it[0] * it[1] }.sum()}"
            }
        }
    }
}
