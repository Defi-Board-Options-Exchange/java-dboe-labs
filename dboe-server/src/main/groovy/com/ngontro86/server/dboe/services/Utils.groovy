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

}
