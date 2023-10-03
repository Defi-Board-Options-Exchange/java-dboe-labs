package com.ngontro86.utils

import org.junit.Test

import static com.ngontro86.utils.RoundUtils.roundToNearestStep

class RoundUtilsTest {
    @Test
    void "should round to nearest step"() {
        assert roundToNearestStep(100.1, 0.5) == 100
    }
}
