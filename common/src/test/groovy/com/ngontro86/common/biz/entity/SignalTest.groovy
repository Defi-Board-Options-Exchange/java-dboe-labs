package com.ngontro86.common.biz.entity

import com.ngontro86.common.biz.entity.Signal
import org.junit.Test


class SignalTest {

    @Test
    void "should build signal 1"() {
        def sig = Signal.build(
                [
                        'ref_signal'  : 'PairBasis',
                        'inst_id'     : 'I1',
                        'order_req_id': 'o1',
                        'ref_price'   : 100d,
                        'timestamp'   : 10l
                ]
        )
        assert sig.refSignal == 'PairBasis'
        assert sig.instId == 'I1'
        assert sig.orderReqId == 'o1'
        assert sig.refPrice == 100d
        assert sig.timestamp == 10l
    }
}
