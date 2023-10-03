package com.ngontro86.market.common;

import com.ngontro86.utils.Utils;

import static java.lang.Double.NaN;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

public class MidPriceUtils {

    public static double microPrice(Double bid, Double lastPrice, Double ask, Double bidSize, Double lastSize, Double askSize) {

        if (bid == null || lastPrice == null || ask == null || bidSize == null || lastSize == null || askSize == null) {
            return NaN;
        }

        final double sqrtBidSize = sqrt(bidSize), sqrtAskSize = sqrt(askSize);
        final double totalWeight = sqrtBidSize + sqrtAskSize;
        return (bid * sqrtAskSize + ask * sqrtBidSize) / totalWeight;
    }

    public static double priceJump(double currentPrice, double prevPrice, long timestamp, long prevTimestamp) {
        if (prevPrice == 0d || timestamp <= prevTimestamp) {
            return 0d;
        }
        return log(currentPrice / prevPrice) * Utils.BPS / sqrt((timestamp - prevTimestamp) / 1000d);
    }

}
