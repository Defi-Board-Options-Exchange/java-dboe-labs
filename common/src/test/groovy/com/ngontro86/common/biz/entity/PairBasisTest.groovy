package com.ngontro86.common.biz.entity

import org.junit.Test


class PairBasisTest {

    @Test
    void "should construct bean"() {
        def pairBasis = PairBasis.build(
                [
                        'pairName' : 'X',
                        'basis'    : 10d,
                        'fairBasis': 7.5d,
                        'timestamp': 10l
                ]
        )
        assert pairBasis.pairName == 'X'
        assert pairBasis.basis == 10d
        assert pairBasis.fairBasis == 7.5d
        assert pairBasis.timestamp == 10l
    }
}
