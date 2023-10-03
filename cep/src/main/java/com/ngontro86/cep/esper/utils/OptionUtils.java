package com.ngontro86.cep.esper.utils;

import com.ngontro86.market.pricing.Black76;
import com.ngontro86.utils.GlobalTimeUtils;

public class OptionUtils {

    private static long YEAR_MS = 365 * 24 * 3600 * 1000L;

    public static double timeDiffInYear(int yyyyMMdd, int ltt, long todayUtcMs) {
        return (getTimeUtc(yyyyMMdd, ltt) - todayUtcMs) * 1.0 / YEAR_MS;
    }

    protected static long getTimeUtc(int yyyyMMdd, int ltt) {
        return GlobalTimeUtils.getTimeUtc(String.format("%08d %06d", yyyyMMdd, ltt), "GMT", "yyyyMMdd HHmmss");
    }

    public static double iv(double px, String kind, double atm, double strike, double condStrike, double timeToExpiry) {
        return Black76.iv(px, kind, atm, strike, condStrike, timeToExpiry);
    }
}
