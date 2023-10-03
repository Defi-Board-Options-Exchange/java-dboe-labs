package com.ngontro86.market.common.adf

import org.junit.Test


class ADFUtilsTest {
    @Test
    void "should find out diff from array"() {
        assert  ADFUtils.diff([5d, 6d, 7d, 8d] as double[]) == [1d, 1d, 1d] as double[]
    }

    @Test
    void "should find out lag from array"() {
        println ADFUtils.laggedMatrix([5d, 6d, 7d, 8d] as double[], 1)
    }

    @Test
    void "should return array of 1"() {
        assert ADFUtils.ones(3) == [1d, 1d, 1d] as double[]
    }

    @Test
    void "should return sequence array"() {
        assert ADFUtils.sequence(3, 5) == [3, 4, 5] as double[]
    }

}
