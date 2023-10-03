package com.ngontro86.utils

import java.text.SimpleDateFormat

import static com.ngontro86.utils.Utils.toLong

class GlobalTimeUtils {
    /**
     *
     * @param day
     * @param startingHHmmss
     * @param endingHHmmss
     * @param minute
     * @return Collection of yyyyMMddHHmmss series
     */
    static Collection<Long> samplingTimes(int dayYYYYMMdd, int startingHHmmss, int endingHHmmss, int minute) {
        def formatter = new SimpleDateFormat('yyyyMMddHHmmss')

        def startingD = formatter.parse("${dayYYYYMMdd * 1000000L + startingHHmmss}")
        int numSeries = (formatter.parse("${dayYYYYMMdd * 1000000L + endingHHmmss}").time - startingD.time) / minute / 60000L

        def ret = [Long.valueOf(formatter.format(startingD))] as Collection<Long>
        numSeries.times {
            startingD.setTime(startingD.time + minute * 60000L)
            ret << Long.valueOf(formatter.format(startingD))
        }
        return ret
    }

    static Collection<Integer> dates(int startingDate, int endingDate, int duration) {
        def formatter = new SimpleDateFormat('yyyyMMdd')
        def d1 = formatter.parse("$startingDate")
        def d2 = formatter.parse("$endingDate")
        final long dur = duration * 24L * 3600L * 1000L
        def allDates = []
        while (!d1.after(d2)) {
            allDates << (int)getTimeFormat(d1.time, 'yyyyMMdd')
            d1.setTime(d1.time + dur)
        }
        return allDates
    }


    static long getTimeFormat(long timeUTC, String pattern) {
        return toLong(new SimpleDateFormat(pattern).format(new Date(timeUTC)), 0)
    }

    static long getTimeUtc(String timeInDifferentFormat, String pattern) {
        return new SimpleDateFormat(pattern).parse("${timeInDifferentFormat}").time
    }

    static long getTimeUtc(String timeInDifferentFormat, String timezone, String pattern) {
        def sdf = new SimpleDateFormat(pattern)
        sdf.setTimeZone(TimeZone.getTimeZone(timezone))
        return sdf.parse("${timeInDifferentFormat}").time
    }

    static int getMonthYYYYMM(long timeMilliSec) {
        return (int) getTimeFormat(timeMilliSec, "yyyyMM")
    }

    static int millisDiff(int startingHHmmss, int endingHHmmss) {
        return ((endingHHmmss/10000) - (startingHHmmss/10000)) * 3600000 +
                ((endingHHmmss % 10000)/100 - (startingHHmmss % 10000)/100) * 60000 +
                ((endingHHmmss % 100) - (startingHHmmss % 100)) * 1000
    }

}
