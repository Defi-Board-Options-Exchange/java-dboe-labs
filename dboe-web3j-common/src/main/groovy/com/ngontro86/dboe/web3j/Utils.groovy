package com.ngontro86.dboe.web3j

import com.ngontro86.utils.GlobalTimeUtils

class Utils {

    static byte[] padding(int fixedLen, byte[] sources) {
        def ret = new byte[fixedLen]
        System.arraycopy(sources, 0, ret, 0, sources.length)
        return ret
    }

    private static long YEAR_MS = 365 * 24 * 3600 * 1000L

    static double timeDiffInYear(int yyyyMMdd, int ltt, long todayUtcMs) {
        return (getTimeUtc(yyyyMMdd, ltt) - todayUtcMs) * 1.0 / YEAR_MS
    }

    static double timeDiffInYear(long expiryUtcMs, long todayUtcMs) {
        return (expiryUtcMs - todayUtcMs) * 1.0 / YEAR_MS
    }

    static long getTimeUtc(int yyyyMMdd, int ltt) {
        return GlobalTimeUtils.getTimeUtc(String.format("%08d %06d", yyyyMMdd, ltt), "GMT", "yyyyMMdd HHmmss")
    }

    static DBOEOptionFactory.DBOEOptionSpecs optionSpecs(Map specs) {
        return new DBOEOptionFactory.DBOEOptionSpecs(
                specs['expiry'] as BigInteger,
                specs['ltt'] as BigInteger,
                specs['lttUtc'] as BigInteger,
                specs['strike'] as BigInteger,
                specs['cond_strike'] as BigInteger,
                padding(32, specs['underlying'] as byte[]),
                padding(32, specs['currency'] as byte[]),
                specs['price_scale'] as BigInteger,
                specs['isLong'] as Boolean,
                specs['isCall'] as Boolean
        )
    }

}
