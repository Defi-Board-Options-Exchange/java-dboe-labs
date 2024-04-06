package com.ngontro86.server.dboe.services.analytic

import com.ngontro86.market.web3j.GreekRisk
import com.ngontro86.restful.common.json.JsonUtils
import org.junit.Test

class PortfolioRiskTest {

    @Test
    void "should display the Total property"() {
        def portfolioRisk = new PortfolioRisk(
                vals: ['ETH': 520d, 'BTC': 127d],
                greeks: ['ETH': new GreekRisk(delta: 0.1, vega: 0.1, gamma: 0.05, theta: 0.5), 'BTC': new GreekRisk(delta: 0.1, vega: 0.1, gamma: 0.05, theta: 0.5)],
                optionPos: ['ETH': ['E3200C412': -0.14d, 'E3400C412': 0.4d], 'BTC': ['E64000C412': 0.3d, 'E64000P412': -0.2d]]
        )
        println JsonUtils.toJson(portfolioRisk)
    }

}
