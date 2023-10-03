package com.ngontro86.dboe.trading.utils

import org.junit.Test

class MarketMakingUtilsTest {

    @Test
    void "should avoid the over precision when we round up or down the quantity"() {
        100.times {
            println MarketMakingUtils.round((6.0573829800600825 + it * 0.052323))
        }
    }

}
