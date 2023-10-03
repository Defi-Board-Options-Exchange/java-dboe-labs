package com.ngontro86.market.common

import org.junit.Test

import static com.ngontro86.market.common.MidPriceUtils.microPrice
import static com.ngontro86.market.common.MidPriceUtils.priceJump
import static com.ngontro86.utils.EqualUtils.equals

class MidPriceUtilsTest {

    @Test
    void "should return NaN for invalid input"() {
        assert microPrice(null, 10.0, 11.0, 1, 2, 3) == Double.NaN
    }

    @Test
    void "should calculate midpoint"() {
        def mp =
                microPrice(
                        99.0,
                        99.5,
                        100.0,
                        1000,
                        5,
                        100
                )
        println mp
        assert equals(mp, 99.75974692664795, 0.0000001)

        assert microPrice(
                99.0,
                99.5,
                100.0,
                1000,
                5,
                0
        ) == 100.0

        assert microPrice(
                99.0,
                99.5,
                100.0,
                0,
                5,
                10
        ) == 99.0

    }

    @Test
    void "should calculate price jump"() {
        assert priceJump(1.27555, 1.27550, 2000, 1000) == 0.3919954529027971

        assert priceJump(1.27555, 0d, 2000, 1000) == 0d

        assert priceJump(1.27555, 1.27550, 1000, 1000) == 0d
    }

}
