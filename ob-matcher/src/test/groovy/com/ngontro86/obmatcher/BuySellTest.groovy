package com.ngontro86.obmatcher

import org.junit.Test

import static com.ngontro86.obmatcher.BuySell.BUY
import static com.ngontro86.obmatcher.BuySell.SELL
import static com.ngontro86.obmatcher.BuySell.buySell

class BuySellTest {

    @Test
    void "should return BuySell enum"() {
        assert buySell(1) == BUY
        assert buySell(2) == SELL
        assert buySell(3) == SELL
    }

    @Test
    void "should return correct integer from BuySell"() {
        assert buySell(1).toInt() == 1
        assert buySell(2).toInt() == 2
    }

}
