package com.ngontro86.utils

import org.junit.Test

import static com.ngontro86.utils.Utils.getSlidingHSLExitPrice


class UtilsTest {

    @Test
    void "should have valid hsl"() {
        def hsl = getSlidingHSLExitPrice(false, 1.36055, 1.36075, 2.5)
        assert hsl == 1.3608901375
        println hsl
    }

    @Test
    void "should create instId scenario 1"() {
        assert Utils.getInstID('HSI', 'HKFE', 20170530, 50, 'HKD') == 'FHSIHKFE50HKD201705'
    }

}
