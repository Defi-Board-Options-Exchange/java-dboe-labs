package com.ngontro86.market.common

import org.junit.Test

import static com.ngontro86.market.common.ImbalanceUtils.*
import static com.ngontro86.utils.EqualUtils.equals

class ImbalanceUtilsTest {

    @Test
    void "should calculate aggregated imbalance"() {
        assert ImbalanceUtils.imbalance(100, 200) == -0.3333333333333333
        assert ImbalanceUtils.imbalance(0, 0) == 0
    }


    @Test
    void "should get delta size"() {
        assert getDeltaAskSize(7, 3, 101.0, 103.0) == 7
        assert getDeltaAskSize(2, 3, 103.0, 103.0) == -1
        assert getDeltaAskSize(2, 3, 104.0, 103.0) == 0

        assert getDeltaBidSize(2, 3, 99.0, 100.0) == 0
        assert getDeltaBidSize(2, 3, 100.0, 100.0) == -1
        assert getDeltaBidSize(2, 3, 101.0, 100.0) == 2
    }

    @Test
    void "should calculate imbalance"() {
        def imb = imbalance(
                5, 2,
                99.0, 100.0,
                7, 3,
                101.0, 103.0
        )
        println imb
        assert equals(imb, -1.0, 0.0001)
    }

    @Test
    void "should calculate imbalance volume"() {
        def imb = imbalanceVol(
                5, 2,
                99.0, 100.0,
                7, 3,
                101.0, 103.0
        )
        println imb
        assert equals(imb, -7.0, 0.0001)
    }

    @Test
    void "should calculate imbalance with time discount"() {
        def imb = imbalance(
                5, 2,
                99.0, 100.0,
                7, 3,
                101.0, 103.0,
                250
        )
        assert equals(imb, -2.0, 0.0001)

        assert imbalanceTimeDiscount(
                imbalance(
                        5, 2,
                        99.0, 100.0,
                        7, 3,
                        101.0, 103.0
                ),
                250) == -2.0

    }

}
