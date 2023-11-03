package com.ngontro86.dboe.analytic.rest

import static com.ngontro86.utils.CollectionUtils.join

class QueryUtil {

    static String constructQuery(String query, Collection<String> whereConds) {
        "${query} (${join(whereConds, "'", ',')})"
    }

    private final static String DIGITS = "0123456789ABCDEF"

    static long hexToLong(String hex) {
        hex = hex.toUpperCase()
        long val = 0

        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i)
            val = 16 * val + DIGITS.indexOf(c.toString())
        }
        return val
    }

    private final static String NULL_ADDR = '0x0000000000000000000000000000000000000000'

    static String trimHexAddress(String addr) {
        if (addr == null || addr.length() == NULL_ADDR.length()) {
            return addr
        }
        return addr.replaceFirst("000000000000000000000000", "")
    }
}
