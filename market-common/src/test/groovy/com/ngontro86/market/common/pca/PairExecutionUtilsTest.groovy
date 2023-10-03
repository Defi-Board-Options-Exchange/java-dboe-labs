package com.ngontro86.market.common.pca

import com.ngontro86.market.common.NormalizeMethod
import org.junit.Test

import static com.ngontro86.market.common.pca.PairExecutionUtils.executeInPairConcept


class PairExecutionUtilsTest {

    @Test
    void "should find trade size for each leg"() {
        assert executeInPairConcept(
                NormalizeMethod.Z_SCORE,
                [0.707, -0.707] as double[],
                [10105.76, 23457.6] as double[],
                [105.76, 257.6] as double[],
                [3, 2] as int[],
                [50d, 50d] as double[]
        ) == [3.0, -1.2316770186335402, 71254.6583850933]
    }

    @Test
    void "should find GCD"() {
        assert PairExecutionUtils.gcd(15, 12) == 3
    }

    @Test
    void "should find LCM"() {
        assert PairExecutionUtils.lcm(15, 12) == 60
    }

}
