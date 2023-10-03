package com.ngontro86.utils

import org.junit.Test


class MapEqualUtilsTest {

    @Test
    void "should compare two maps"() {
        assert MapEqualUtils.mapContains(['x': 1, 'y': 2], ['x': 1])
    }

}
