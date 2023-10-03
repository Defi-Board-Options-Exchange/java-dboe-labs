package com.ngontro86.server.utils

import org.junit.Ignore
import org.junit.Test


class SwaggerUtilsTest {

    @Test
    void "should print out string 1"() {
        assert SwaggerUtils.toString(['x' : 1d,'y' : 10d]) == 'x:1.0,y:10.0,'
    }

    @Test
    @Ignore
    void "should print out string 2"() {
        assert SwaggerUtils.toString([['x' : 1d,'y' : 10d],['x' : 1d,'y' : 10d]]) == 'x:1.0,y:10.0,' +
                '\nx:1.0,y:10.0,'
    }

}
