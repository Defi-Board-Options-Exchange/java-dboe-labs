package com.ngontro86.utils

import org.junit.Test

import static com.ngontro86.utils.PairUtils.*

class PairUtilsTest {

    @Test
    void "should find out lots at each leg 1"() {
        assert optimalLots(1.5d, [50, 10]) == [2, -2]
        assert optimalLots(1.25d, [50, 10]) == [1, 1]
        assert optimalLots(1.32d, [50, 10]) == [1, 2]
        assert optimalLots(1.3d, [50, 10]) == [1, 2]
        assert optimalLots(1.28d, [50, 10]) == [1, 1]
        assert optimalLots(1.82d, [50, 10]) == [2, -1]

        assert optimalLots(1.24d, [50, 25]) == [1, 0]
        assert optimalLots(0.7d, [50, 25]) == [1, -1]
        assert optimalLots(0.75d, [50, 25]) == [1, 0]
        assert optimalLots(0.7d, [50, 10]) == [1, -2]
        assert optimalLots(2, [50]) == [2]
        assert optimalLots(1, [50]) == [1]
        assert optimalLots(-3, [50]) == [-3]
    }

    @Test
    void "should find out lots at each leg 2"() {
        assert optimalLots(-1.24d, [50, 25]) == [-1, 0]

        assert optimalLots(-1.26d, [50, 25]) == [-1, -1]

        assert optimalLots(-0.82d, [50, 10]) == [-1, 1]
        assert optimalLots(-1.82d, [50, 10]) == [-2, 1]
    }

    @Test
    void "should find out each leg 3"() {
        assert optimalLots(1.5d, [50]) == [2d]

        assert optimalLots(1.45d, [50]) == [1d]

    }

    @Test
    void "should throw exception"() {
        try {
            optimalLots(1.5d, []) == [2d]
            assert false
        } catch (Exception e) {
            assert true
        }
    }

    @Test
    void "should form order id"() {
        assert PairUtils.getOrderReqId("HangSeng_1", true, 1) == 'HangSeng_1-Entry-1'
        assert PairUtils.getOrderReqId("HangSeng_1", false, 2) == 'HangSeng_1-Exit-2'
    }

    @Test
    void "should know entry or exit"() {
        assert isEntry('Hang Seng Pair-Entry-1')
        assert !isEntry('Hang Seng Pair-Exit-1')
    }

    @Test
    void "should know which leg"() {
        assert legId('Hang Seng Pair-Entry-1') == 1
    }

    @Test
    void "should know pair req id"() {
        assert pairOrderId('Hang Seng Pair-Entry-1') == 'Hang Seng Pair'
    }
}
