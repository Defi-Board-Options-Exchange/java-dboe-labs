package com.ngontro86.dboe.trading.utils

import java.text.DecimalFormat

class MarketMakingUtils {

    static Set<String> umSymbols(Set<String> underlyings) {
        return underlyings.collect { umSymbol(it) }
    }

    static String umSymbol(String underlying) {
        return "${underlying}USDT"
    }

    static String underlying(String symbol) {
        return symbol.replace("USDT", "")
    }

    static Set<String> underlyings(Set<String> symbols) {
        return symbols.collect { underlying(it) }
    }

    private static DecimalFormat fmt = new DecimalFormat('0.00')

    static double round(double q, String format) {
        return Double.valueOf(new DecimalFormat(format).format(q))
    }

    static double round(double q) {
        return Double.valueOf(fmt.format(q))
    }

    static int bestOrderTimeOutInMin(long expiryUtc, long currentTime, int pxLevel) {
        def timeToExpiryInMin = (expiryUtc - currentTime) / 60000
        int buffTime = 0
        switch (pxLevel) {
            case 1:
            case 2:
            case 3:
                buffTime = 3 * 60
                break
            case 4:
                buffTime = 2 * 60
                break
            case 5:
                buffTime = 1 * 60
                break
            default:
                buffTime = 0
                break
        }
        return timeToExpiryInMin - buffTime
    }

    static Collection pickNSmallest(Collection sets, int n) {
        def ret = []
        if (n >= sets.size()) {
            ret.addAll(sets)
            return ret
        }

        def priorityQueue = new PriorityQueue(sets)
        n.times {
            ret.add(priorityQueue.poll())
        }
        return ret
    }
}
