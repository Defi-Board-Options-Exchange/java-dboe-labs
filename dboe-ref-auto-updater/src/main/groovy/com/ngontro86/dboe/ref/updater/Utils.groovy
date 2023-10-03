package com.ngontro86.dboe.ref.updater

import com.ngontro86.utils.GlobalTimeUtils

class Utils {

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

    static String instrId(String und, boolean callPut, int strike, int expiry) {
        "${und.charAt(0)}${strike}${callPut ? 'C' : 'P'}${expiry % 10000}"
    }
}
