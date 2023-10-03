package com.ngontro86.common.biz.entity

import com.ngontro86.common.biz.entity.Price
import org.junit.Test


class PriceTest {

    @Test
    void "should build Price 1"() {
        def price = Price.build(
                [
                        'inst_id'    : 'I1',
                        'last_price' : 100d,
                        'open_price' : 99d,
                        'close_price': 99d,
                        'micro_price': 101d,
                        'bid'        : 100d,
                        'ask'        : 102d,
                        'bid_size'   : 5,
                        'ask_size'   : 7,
                        'last_size'  : 1,
                        'timestamp'  : 10l
                ]
        )

        assert price.instId == 'I1'
        assert price.bid == 100d
        assert price.ask == 102d
        assert price.bidSize == 5
        assert price.askSize == 7
        assert price.microPrice == 101d

    }

}
