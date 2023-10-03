package com.ngontro86.common.biz.entity


enum OrderStatus {
    SUBMITTED,
    ACKED,
    PARTIAL_FILLED,
    FILLED,
    CANCELLED

    static OrderStatus status(String tradeStatus) {
        if (tradeStatus == 'Filled') {
            return FILLED
        }
        if (tradeStatus == 'Partialfill') {
            return PARTIAL_FILLED
        }
        if (tradeStatus == 'Cancelled') {
            return CANCELLED
        }
        return ACKED
    }

}