package com.ngontro86.cep.esper.utils


import com.ngontro86.utils.GlobalTimeUtils
import org.junit.Test

class OptionUtilsTest {

    @Test
    void "should calculate time diff"() {
        long today = GlobalTimeUtils.getTimeUtc('20230601', 'GMT', 'yyyyMMdd')
        assert OptionUtils.timeDiffInYear(20230609, 150000, today) == 0.02363013698630137
    }

    @Test
    void "should find out time utc from expiry and ltt"() {
        println OptionUtils.getTimeUtc(20230609, 150000)
    }

}
