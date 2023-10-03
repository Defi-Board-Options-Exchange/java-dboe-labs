package com.ngontro86.obmatcher

import org.junit.Test

class OrderTest {
    @Test
    void "should create Order object"() {
        def order = Order.fromMap(
                [
                        'wallet_id'    : '0x123',
                        'user_order_id': 'U1',
                        'ex_order_id'  : 'Ex1',
                        'txn_sig'      : '0x123',
                        'buy_sell'     : 1,
                        'order_type'   : 'LMT',
                        'amount'       : 0.25,
                        'price'        : 10.25,
                        'in_timestamp' : 1l,
                ]
        )

        assert order.orderID == new OrderID(userOrderId: 'U1', exOrderId: 'Ex1')
        assert order.qty == 0.25
        assert order.px == 10.25
    }
}
