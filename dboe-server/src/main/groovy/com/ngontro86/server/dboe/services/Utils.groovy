package com.ngontro86.server.dboe.services

import com.ngontro86.market.pricing.Black76
import com.ngontro86.market.pricing.OptionKind
import com.ngontro86.utils.GlobalTimeUtils

class Utils {

    static double dboeOptionTimeToExpiry(int date, long currentTimeMilliSec) {
        return (GlobalTimeUtils.getTimeUtc("${date}23", 'yyyyMMddHH') - currentTimeMilliSec) / 365 / 24 / 60 / 60 / 1000
    }

    static Map priceOptionWithShocks(boolean isCall,
                                     int expiryDate,
                                     long currentTimeMilliSec,
                                     double spot,
                                     double strike, double condStrike,
                                     double volatility,
                                     double[] spotShocks,
                                     double[] volShocks
    ) {
        double timeToExpiry = dboeOptionTimeToExpiry(expiryDate, currentTimeMilliSec)
        def kind = isCall ? OptionKind.Call : OptionKind.Put
        def ret = [:]
        volShocks.each { volShock ->
            spotShocks.each { spotShock ->
                ret["${volShock}^${spotShock}"] = Black76.priceDboe option: [kind: kind, atm: spot * (1.0 + spotShock), strike: strike, condStrike: condStrike, r: 0.0, t: timeToExpiry, vol: volatility / 100.0 * (1.0 + volShock)]
            }
        }
        return ret
    }

    static double txnFee(double minFee, double feeBps, double tradingNotional) {
        return Math.max(minFee, feeBps / 10000.0 * tradingNotional)
    }

    static String body(String parameterisedBody, String sep, Map params) {
        params.each { k, v ->
            parameterisedBody = parameterisedBody.replaceAll("${sep}${k}${sep}", v.toString())
        }
        return parameterisedBody
    }

    private static double DOUBLE_PRECISION = 0.00001
    static double estAvgPx(double currPos, double pos, double avgPx, double bid, double ask) {
        if (Math.abs(currPos) < DOUBLE_PRECISION) {
            return avgPx
        }
        if (Math.abs(currPos - pos) > DOUBLE_PRECISION) {
            if (pos == 0d) {
                return currPos > 0 ? ask : bid
            }
            return (pos * avgPx + (currPos - pos) * (currPos > pos ? ask : bid)) / currPos
        }
        return avgPx
    }

}
