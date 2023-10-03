package com.ngontro86.common.util

import org.junit.Test


class IOUtilsTest {

    @Test
    void "should get date time"() {
        assert IOUtils.getDateFromTime(20170614, 230000).time == 1497452400000
    }

}
