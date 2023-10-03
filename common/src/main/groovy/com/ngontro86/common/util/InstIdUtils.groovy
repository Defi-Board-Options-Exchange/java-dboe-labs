package com.ngontro86.common.util


class InstIdUtils {

    static Collection BBG_MONTHS = ['F', 'G', 'H', 'J', 'K', 'M', 'N', 'Q', 'U', 'V', 'X', 'Z']

    static Map BBG_MONTH_MAP = [:]

    static {
        BBG_MONTHS.eachWithIndex { bbgMonthCode, idx -> BBG_MONTH_MAP[idx + 1] = bbgMonthCode }
    }

    static int getMonth(int yyyyMM) {
        return yyyyMM % 100
    }

    static String getBbgTicker(String underlying, int yyyyMM) {
        if(yyyyMM > 205001) {
            yyyyMM = (int)(yyyyMM / 100)
        }
        int month = getMonth(yyyyMM)
        int yearLastTwoDigit = (int)((yyyyMM/100)) % 100
        return "${underlying}${BBG_MONTH_MAP[month]}${yearLastTwoDigit}"
    }
}
