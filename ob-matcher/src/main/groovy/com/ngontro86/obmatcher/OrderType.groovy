package com.ngontro86.obmatcher

enum OrderType {
    LMT,
    MKT

    static OrderType orderType(String ot) {
        if ("LMT".equals(ot)) {
            return LMT
        }
        if ("MKT".equals(ot)) {
            return MKT
        }
        throw new IllegalArgumentException("Unknown Order Type: ${ot}")
    }
}