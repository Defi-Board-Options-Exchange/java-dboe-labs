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
    static double round(double q) {
        return Double.valueOf(fmt.format(q))
    }
}
