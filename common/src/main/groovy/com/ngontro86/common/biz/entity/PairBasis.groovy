package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils

class PairBasis {
    String pairName
    double basis
    double fairBasis
    long timestamp

    static PairBasis build(Map<String, Object> map) {
        def one = new PairBasis()
        BeanUtils.copyProperties(one, map)
        return one
    }
}
