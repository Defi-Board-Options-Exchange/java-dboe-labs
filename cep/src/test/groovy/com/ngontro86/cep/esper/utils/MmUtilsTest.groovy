package com.ngontro86.cep.esper.utils

import org.junit.Ignore
import org.junit.Test

import static com.ngontro86.cep.esper.utils.MmUtils.*

@Ignore
class MmUtilsTest {

    @Test
    void "should calculate cont spread"() {
        assert MmUtils.contSpread(
                2,
                2,
                [20.0, 10.0, 5.0, 2.0] as Double[],
                [2, 2, 2, 2] as Integer[]
        ) == 20.0

        assert MmUtils.contSpread(
                2,
                2,
                [20.0, 10.0, 5.0, 2.0] as Double[],
                [0, 2, 2, 2] as Integer[]
        ) == 10.0

        assert MmUtils.contSpread(
                2,
                2,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 1, 2, 2] as Integer[]
        ) == 7.5

        assert MmUtils.contSpread(
                2,
                3,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 1, 2, 2] as Integer[]
        ) == 7.5

        assert MmUtils.contSpread(
                2,
                3,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 2, 2] as Integer[]
        ) == 5.0

        assert MmUtils.contSpread(
                2,
                4,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 0, 2] as Integer[]
        ) == 2.0

        assert MmUtils.contSpread(
                2,
                10,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 0, 2] as Integer[]
        ) == 2.0
    }

    @Test
    void "should calculate spread due to market jump"() {
        assert MmUtils.jumpSpread(1, 1.5, 12.0, 25.0, 10000.0) == 0.0
        assert MmUtils.jumpSpread(1, 1.5, 12.0, -25.0, 10000.0) == 25.5
        assert MmUtils.jumpSpread(2, 2.0, 12.0, -35.0, 10000.0) == 0.0
        assert MmUtils.jumpSpread(2, 2.0, 12.0, 45.0, 10000.0) == 78
    }

    @Test
    void "should create order req id"() {
        assert mmQuoteOrderReqId('Nifty MM', 'Global', 'IN1', 1, 5) == 'E-1-Nifty MM-Global-IN1-5'
        assert mmExitOrderReqId('Nifty MM', 'Global', 'IN1', 5) == 'X-Nifty MM-Global-IN1-5'
    }

    @Test
    void "should calculate adjustment spread for BID"() {
        assert spread(
                1,
                2,
                20.0,
                30.0,
                2,
                1.5d,
                -25d,
                10000d,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [2, 2, 2, 2] as Integer[]
        ) == (70.0 + 17.5)

        assert spread(
                1,
                2,
                20.0,
                30.0,
                2,
                1.5,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 2, 2, 2] as Integer[]
        ) == 65.0

        assert spread(
                1,
                2,
                20.0,
                30.0,
                2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 2, 2] as Integer[]
        ) == 55.0

        assert spread(
                1,
                2,
                20.0,
                30.0,
                2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 0, 2] as Integer[]
        ) == 52.0

        assert spread(
                1,
                2,
                20.0,
                30.0,
                -2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [2, 2, 2, 2] as Integer[]
        ) == 40.0

        assert spread(
                1,
                2,
                20.0,
                30.0,
                -2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 2, 2, 2] as Integer[]
        ) == 35.0

        assert spread(
                1,
                2,
                20.0,
                30.0,
                -2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 2, 2] as Integer[]
        ) == 25.0
    }

    @Test
    void "should calculate adjustment spread for ASK"() {
        assert spread(
                2,
                2,
                20.0,
                30.0,
                -2,
                1.5,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [2, 2, 2, 2] as Integer[]
        ) == (70.0 + 17.5)

        assert spread(
                2,
                2,
                20.0,
                30.0,
                -2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 2, 2, 2] as Integer[]
        ) == (65.0 + 30)

        assert spread(
                2,
                2,
                20.0,
                30.0,
                -2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 2, 2] as Integer[]
        ) == (55.0 + 30.0)

        assert spread(
                2,
                2,
                20.0,
                30.0,
                -2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 0, 2] as Integer[]
        ) == (52.0 + 30)

        assert spread(
                2,
                2,
                20.0,
                30.0,
                2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [2, 2, 2, 2] as Integer[]
        ) == 40.0

        assert spread(
                2,
                2,
                20.0,
                30.0,
                2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 2, 2, 2] as Integer[]
        ) == 35.0

        assert spread(
                2,
                2,
                20.0,
                30.0,
                2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 2, 2] as Integer[]
        ) == 25.0

        assert spread(
                2,
                2,
                20.0,
                30.0,
                2,
                2.0,
                25.0,
                10000.0,
                [20.0, 15.0, 5.0, 2.0] as Double[],
                [0, 0, 0, 2] as Integer[]
        ) == 20.0
    }

    @Test
    void "should reduce spread according time for BID"() {
        assert MmUtils.spread2(
                1,
                2,
                0,
                25,
                24,
                72,
                10,
                100,
                200
        ) == 52

        assert MmUtils.spread2(
                1,
                2,
                0,
                25,
                24,
                72,
                10,
                100,
                500
        ) == 24

        assert MmUtils.spread2(
                1,
                2,
                -1,
                25,
                24,
                72,
                10,
                100,
                200
        ) == 52

        assert MmUtils.spread2(
                1,
                1,
                1,
                25,
                24,
                72,
                10,
                100,
                200
        ) == 77
    }

    @Test
    void "should reduce spread according time for ASK"() {
        assert MmUtils.spread2(
                2,
                2,
                0,
                25,
                24,
                72,
                10,
                100,
                200
        ) == 52

        assert MmUtils.spread2(
                2,
                2,
                0,
                25,
                24,
                72,
                10,
                100,
                500
        ) == 24

        assert MmUtils.spread2(
                2,
                1,
                1,
                25,
                24,
                72,
                10,
                100,
                200
        ) == 52

        assert MmUtils.spread2(
                2,
                1,
                -1,
                25,
                24,
                72,
                10,
                100,
                200
        ) == 77
    }

}
