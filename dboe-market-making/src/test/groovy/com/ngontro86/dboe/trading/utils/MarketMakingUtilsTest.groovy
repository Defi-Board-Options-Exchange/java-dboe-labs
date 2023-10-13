package com.ngontro86.dboe.trading.utils

import org.junit.Test

class MarketMakingUtilsTest {

    @Test
    void "should avoid the over precision when we round up or down the quantity"() {
        100.times {
            println MarketMakingUtils.round((6.0573829800600825 + it * 0.052323))
        }
    }

    @Test
    void "should work out best time out in minute"() {
        println MarketMakingUtils.bestOrderTimeOutInMin(1697122800000, 1697090023000, 1)
        println MarketMakingUtils.bestOrderTimeOutInMin(1697122800000, 1697090023000, 2)
        println MarketMakingUtils.bestOrderTimeOutInMin(1697122800000, 1697090023000, 3)
        println MarketMakingUtils.bestOrderTimeOutInMin(1697122800000, 1697090023000, 4)
        println MarketMakingUtils.bestOrderTimeOutInMin(1697122800000, 1697090023000, 5)
        println MarketMakingUtils.bestOrderTimeOutInMin(1697122800000, 1697090023000, 6)
        println MarketMakingUtils.bestOrderTimeOutInMin(1697122800000, 1697090023000, 7)
    }

}
