package com.ngontro86.common.biz.entity

import com.ngontro86.common.biz.entity.OrderReq
import org.junit.Test


class OrderReqTest {

    @Test
    void "should build order 1"() {
        def order = OrderReq.build(
                [
                        'inst_id'          : 'I1',
                        'order_req_id'     : 'o1',
                        'exchange_order_id': '1',
                        'qty'              : 1,
                        'price'            : 10d
                ]
        )
        assert order.instId == 'I1'
        assert order.orderReqId == 'o1'
        assert order.qty == 1
        assert order.price == 10d
    }

}
