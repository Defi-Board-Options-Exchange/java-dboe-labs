package com.ngontro86.common.biz.entity

import org.junit.Test


class CloseoutOrderReqTest {

    @Test
    void "should build object 1"() {
        def order = CloseoutOrderReq.build(
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

        assert order.orderReq['inst_id'] == 'I1'
        assert order.orderReq['order_req_id'] == 'o1'
        assert order.orderReq['qty'] == 1
        assert order.orderReq['price'] == 10d

        order.updateExchangeOrderId('10')
        assert order.exchangeOrderId == '10'
        assert order.orderReq['exchange_order_id'] == '10'

        order.setOrderStatus(OrderStatus.ACKED)
        assert order.orderStatus == OrderStatus.ACKED

    }

}
