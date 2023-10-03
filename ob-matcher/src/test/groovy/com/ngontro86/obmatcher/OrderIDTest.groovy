package com.ngontro86.obmatcher

import org.junit.Test

class OrderIDTest {

    @Test
    void "should create OrderID from map"() {
        def orderId = OrderID.fromMap([
                'wallet_id'    : '0x212',
                'user_order_id': 'o1',
                'ex_order_id'  : 'ex1',
                'order_hash'   : 'h1',
                'txn_sig'      : '0x11',
        ])

        assert orderId.walletAddress == '0x212'
        assert orderId.userOrderId == 'o1'
        assert orderId.exOrderId == 'ex1'
        assert orderId.orderHash == 'h1'
        assert orderId.txnSig == '0x11'
    }

    @Test
    void "should test equality and return hashcode"() {
        def oi1 = new OrderID(walletAddress: '0x1', exOrderId: 'ex1', userOrderId: 'o1', orderHash: 'h1', txnSig: 'txn1')
        def oi2 = new OrderID(walletAddress: '0x2', exOrderId: 'ex1', userOrderId: 'o1', orderHash: 'h1', txnSig: 'txn2')
        def oi3 = new OrderID(walletAddress: '0x2', exOrderId: 'ex1', userOrderId: 'o1', orderHash: 'h1', txnSig: 'txn2')

        assert !oi1.equals(null)
        assert !oi1.equals(oi2)
        assert oi3.equals(oi2)
    }

}
