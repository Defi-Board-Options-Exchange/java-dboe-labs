package com.ngontro86.cep.esper.utils

import org.junit.Test


class CpmcUtilsTest {

    @Test
    void "should return date"() {
        assert CpmcUtils.date(1572059251468) == 20191026
    }

    @Test
    void "should return time utc given a specific time"() {
        assert CpmcUtils.toUtcTime(1572059251468, 111001) == 1572059401000
    }

    @Test
    void "should return time utc given a specific time 2"() {
        assert CpmcUtils.utcTime(20191026, 111001) == 1572059401000
        assert CpmcUtils.utcTime(20191026, 0) == 1572019200000
    }
}
