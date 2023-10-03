package com.ngontro86.common.biz.entity

import org.junit.Test


class PriceMATest {

    @Test
    void "should build price MA 1"() {
        def priceMa = PriceMA.build(
                [
                        'inst_id'    : 'I1',
                        'imbavg'     : 0.6d,
                        'micro_price': 109.67d,
                        'mpavg'      : '108.1d',
                        'bid'        : 109d,
                        'ask'        : 110d,
                        'imbalance'  : 0.45d,
                ]
        )
        assert priceMa.instId == 'I1'
        assert priceMa.imbAvg == 0.6d
        assert priceMa.imbalance == 0.45d
        assert priceMa.microPrice == 109.67d
        assert priceMa.mpAvg == 108.1d
        assert priceMa.bid == 109d
        assert priceMa.ask == 110d
    }

}
