package com.ngontro86.utils

import org.junit.Test


class GlobalTimeUtilsTest {

    @Test
    void "should find out current month"() {
        assert GlobalTimeUtils.getMonthYYYYMM(1495856980070) == 201705
    }

    @Test
    void "should get date range"() {
        assert GlobalTimeUtils.dates(20170501, 20170505, 1) == [20170501, 20170502, 20170503, 20170504, 20170505]
    }

    @Test
    void "should find out time utc given different time format"() {
        assert GlobalTimeUtils.getTimeUtc('20170508215700', 'yyyyMMddHHmmss') == 1494251820000
        assert GlobalTimeUtils.getTimeFormat(1494251820000, 'yyyyMMddHHmmss') == 20170508215700
    }

    @Test
    void "should find out series"() {
        assert GlobalTimeUtils.samplingTimes(20170527, 113000, 113500, 2) == [20170527113000l, 20170527113200l, 20170527113400l]
    }

    @Test
    void "should find out millisec in between"() {
        assert GlobalTimeUtils.millisDiff(83000, 93000) == 3600000
        assert GlobalTimeUtils.millisDiff(93000, 83000) == -3600000

        assert GlobalTimeUtils.millisDiff(93000, 104500) == 5040000
    }

    @Test
    void "should find out time in different format"() {
        assert GlobalTimeUtils.getTimeUtc('20230601 150000', 'GMT', 'yyyyMMdd HHmmss') == 1685631600000
    }

}
