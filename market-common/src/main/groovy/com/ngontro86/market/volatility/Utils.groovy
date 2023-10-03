package com.ngontro86.market.volatility

class Utils {

    static long[] twoNearest(Set<Long> sets, long pivot) {
        def min = sets.min(), max = sets.max()
        if (pivot <= min) {
            return [min, min] as long[]
        }
        if (pivot >= max) {
            return [max, max] as long[]
        }
        return [sets.findAll { it < pivot }.max(), sets.findAll { it > pivot }.min()]
    }
}
