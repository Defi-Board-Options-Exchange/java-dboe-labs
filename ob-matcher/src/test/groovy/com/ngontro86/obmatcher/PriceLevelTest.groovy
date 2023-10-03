package com.ngontro86.obmatcher

import org.junit.Test

class PriceLevelTest {

    @Test
    void "should order the price according to time"() {
        def priceLevel = new PriceLevel()
        priceLevel.add(new Order(
                buySell: BuySell.BUY,
                orderType: OrderType.LMT,
                status: OrderStatus.NEW,
                qty: 1.0,
                px: 4400.0,
                timestamp: 10001l
        ))

        priceLevel.add(new Order(
                buySell: BuySell.BUY,
                orderType: OrderType.LMT,
                status: OrderStatus.NEW,
                qty: 0.5,
                px: 4400.0,
                timestamp: 9999l
        ))

        assert priceLevel.orders.peek().timestamp == 9999l
        assert priceLevel.orders.collect {it.qty}.sum() == 1.5d

        priceLevel.add(new Order(
                buySell: BuySell.BUY,
                orderType: OrderType.LMT,
                status: OrderStatus.NEW,
                qty: 0.5,
                px: 4400.0,
                timestamp: 9998l
        ))

        assert priceLevel.orders.collect {it.qty}.sum()  == 2.0d
        priceLevel.orders.poll()

        assert priceLevel.orders.peek().timestamp == 9999l
        assert priceLevel.orders.collect {it.qty}.sum()  == 1.5d
    }

    @Test
    void "should throw exception when adding Buy and Sell together"() {
        try {
            def priceLevel = new PriceLevel()
            priceLevel.add(new Order(
                    buySell: BuySell.BUY,
                    orderType: OrderType.LMT,
                    status: OrderStatus.NEW,
                    qty: 1.0,
                    px: 4400.0,
                    timestamp: 10001l
            ))

            priceLevel.add(new Order(
                    buySell: BuySell.SELL,
                    orderType: OrderType.LMT,
                    status: OrderStatus.NEW,
                    qty: 0.5,
                    px: 4400.0,
                    timestamp: 9999l
            ))
            assert false
        } catch (IllegalStateException ise) {
            assert true
        }
    }

    @Test
    void "should throw exception when adding orders with different px together"() {
        try {
            def priceLevel = new PriceLevel()
            priceLevel.add(new Order(
                    buySell: BuySell.BUY,
                    orderType: OrderType.LMT,
                    status: OrderStatus.NEW,
                    qty: 1.0,
                    px: 4400.0,
                    timestamp: 10001l
            ))

            priceLevel.add(new Order(
                    buySell: BuySell.BUY,
                    orderType: OrderType.LMT,
                    status: OrderStatus.NEW,
                    qty: 0.5,
                    px: 4500.0,
                    timestamp: 9999l
            ))
            assert false
        } catch (IllegalStateException ise) {
            assert true
        }
    }

    @Test
    void "should be able to have a price level with more than 100 quotes"() {
        def priceLevel = new PriceLevel()
        100.times {
            priceLevel.add(new Order(
                    buySell: BuySell.BUY,
                    orderType: OrderType.LMT,
                    status: OrderStatus.NEW,
                    qty: Math.random(),
                    px: 4400.0,
                    timestamp: 10001l
            ))
        }
        assert priceLevel.orders.size() == 100
    }

    @Test
    void "should be able to remove Order"() {
        def priceLevel = new PriceLevel()
        priceLevel.add(new Order(
                orderID: new OrderID(walletAddress: '0x1', exOrderId: 'ex1', userOrderId: 'o1'),
                buySell: BuySell.BUY,
                orderType: OrderType.LMT,
                status: OrderStatus.NEW,
                qty: Math.random(),
                px: 4400.0,
                timestamp: 10001l
        ))
        priceLevel.remove(new Order(orderID: new OrderID(exOrderId: 'ex1', userOrderId: 'o1')))
        assert priceLevel.orders.isEmpty()

        priceLevel.add(new Order(
                orderID: new OrderID(walletAddress: '0x1', exOrderId: 'ex1', userOrderId: 'o1'),
                buySell: BuySell.BUY,
                orderType: OrderType.LMT,
                status: OrderStatus.NEW,
                qty: Math.random(),
                px: 4400.0,
                timestamp: 10001l
        ))

        priceLevel.remove(new OrderID(exOrderId: 'ex1', userOrderId: 'o1'))
        assert priceLevel.orders.isEmpty()
    }
}
