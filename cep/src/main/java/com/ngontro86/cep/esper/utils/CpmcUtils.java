package com.ngontro86.cep.esper.utils;

import com.ngontro86.utils.GlobalTimeUtils;

public class CpmcUtils {

    public static int millisDiff(int startingHHmmss, int endingHHmmss) {
        return GlobalTimeUtils.millisDiff(startingHHmmss, endingHHmmss);
    }

    public static int date(long timeUtc) {
        return (int)GlobalTimeUtils.getTimeFormat(timeUtc, "yyyyMMdd");
    }

    public static long toUtcTime(long asOfNowUtc, int startingHHmmssTime) {
        return utcTime(date(asOfNowUtc), startingHHmmssTime);
    }

    public static long utcTime(int asOfDate, int startingHHmmssTime) {
        return GlobalTimeUtils.getTimeUtc(String.format("%d", 1000000L * asOfDate + startingHHmmssTime), "yyyyMMddHHmmss");
    }
}
