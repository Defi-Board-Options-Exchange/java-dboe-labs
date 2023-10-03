package com.ngontro86.common.util

import org.junit.Test

import static com.ngontro86.common.util.InstIdUtils.BBG_MONTHS
import static com.ngontro86.common.util.InstIdUtils.BBG_MONTH_MAP


class InstIdUtilsTest {

    @Test
    void "should have correct bbg and month mapping"() {
        assert BBG_MONTH_MAP[1] == 'F'
        assert BBG_MONTH_MAP[12] == 'Z'
    }

    @Test
    void "should get month"() {
        assert InstIdUtils.getMonth(201703) == 3
    }

    @Test
    void "should have correct bbg code"() {
        assert InstIdUtils.getBbgTicker('HSI', 201703i) == 'HSIH17'
    }

    @Test
    void "should have correct bbg code 2"() {
        assert InstIdUtils.getBbgTicker('HSI', 20170315) == 'HSIH17'
    }

}
