package com.ngontro86.dboe.ref.updater

import com.ngontro86.utils.GlobalTimeUtils

import java.text.DecimalFormat

class Utils {

    static long getTimeUtcWithBlock(long nowUtc, long pivotUtc, long block) {
        return pivotUtc + (long)(Math.floor((nowUtc - pivotUtc) / block) * block)
    }

    static long getTimeUtc(int yyyyMMdd, int ltt) {
        return GlobalTimeUtils.getTimeUtc(String.format("%08d %06d", yyyyMMdd, ltt), "GMT", "yyyyMMdd HHmmss")
    }

    static Collection<Integer> listStrikes(boolean callPut, double spot, double strikeInterval, int noItm, int noOtm, int scaleUp) {
        def strikes = [] as SortedSet<Integer>
        int strikeIntScaleup = scaleUp * strikeInterval
        int spotPivot = (int) (strikeIntScaleup * (callPut ? Math.floor(spot / strikeInterval) : Math.ceil(spot / strikeInterval)))

        (noItm).times { time ->
            strikes << (spotPivot + (callPut ? -1 : 1) * (noItm - time) * strikeIntScaleup)
        }

        (noOtm).times { time ->
            strikes << (spotPivot + (callPut ? 1 : -1) * (noOtm - time) * strikeIntScaleup)
        }
        strikes << spotPivot
        return strikes
    }

    static String instrId(String prefix, String strikeFmt, boolean callPut, double strike, int expiry) {
        "${prefix}${new DecimalFormat(strikeFmt).format(strike)}${callPut ? 'C' : 'P'}${expiry % 10000}"
    }

    static Map[] atmCallPut(double spot, Collection options) {
        def calls = options.findAll { it['kind'] == 'Call' && it['strike'] >= spot }.sort { it['strike'] }
        def puts = options.findAll { it['kind'] == 'Put' && it['strike'] <= spot }.sort { -it['strike'] }
        return [calls.isEmpty() ? null : calls.first(), puts.isEmpty() ? null : puts.first()]
    }
}
