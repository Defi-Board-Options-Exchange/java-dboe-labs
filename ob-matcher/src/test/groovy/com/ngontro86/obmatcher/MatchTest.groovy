package com.ngontro86.obmatcher

import org.junit.Test

import static com.ngontro86.obmatcher.BuySell.BUY
import static com.ngontro86.obmatcher.BuySell.SELL
import static com.ngontro86.obmatcher.OrderStatus.NEW
import static com.ngontro86.obmatcher.OrderType.LMT

class MatchTest {

    @Test
    void "should create a match" () {
        def match = Match.match(
                'Op1',
                new Order(
                        orderID: new OrderID(walletName: 'MetaMask', walletAddress: '0x1', exOrderId: 'ex1', userOrderId: 'o1', txnSig: 'hash1'),
                        buySell: BUY,
                        orderType: LMT,
                        status: NEW,
                        qty: 5.0,
                        px: 4455.0,
                        timestamp: 10001l
                ),
                new Order(
                        orderID: new OrderID(walletName: 'MetaMask', walletAddress: '0x2', exOrderId: 'ex2', userOrderId: 'o2', txnSig: 'hash2'),
                        buySell: SELL,
                        orderType: LMT,
                        status: NEW,
                        qty: 2.0,
                        px: 4454.0,
                        timestamp: 10001l
                )
        )

        assert [
                'bidUserId': match.bidOrderId.userOrderId,
                'askUserId': match.askOrderId.userOrderId,
                'qty'      : match.qty,
                'px'       : match.px
        ] == [
                'bidUserId': 'o1',
                'askUserId': 'o2',
                'qty'      : 2.0,
                'px'       : 4454
        ]
    }

}
