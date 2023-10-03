package com.ngontro86.obmatcher


import org.junit.Test

import static com.ngontro86.obmatcher.BuySell.BUY
import static com.ngontro86.obmatcher.BuySell.SELL
import static com.ngontro86.obmatcher.OrderStatus.NEW
import static com.ngontro86.obmatcher.OrderType.LMT
import static com.ngontro86.obmatcher.OrderType.MKT
import static com.ngontro86.utils.ResourcesUtils.lines

class OrderBookTest {

    @Test
    void "should create an orderbook with bid in good order"() {
        def ob = new OrderBook('Option1')
        ob.bids.put(100.0 as Double, new PriceLevel())
        ob.bids.put(99.0 as Double, new PriceLevel())

        ob.bids.get(99.0 as Double).add(new Order(
                orderID: new OrderID(walletAddress: '0x1', exOrderId: 'ex2', userOrderId: 'o2', orderHash: 'h1', txnSig: 'hash2'),
                buySell: BUY,
                orderType: LMT,
                status: NEW,
                qty: 1.0,
                px: 99.0,
                timestamp: 10001l
        ))

        ob.bids.get(100.0 as Double).add(new Order(
                orderID: new OrderID(walletAddress: '0x1', exOrderId: 'ex1', userOrderId: 'o1', orderHash: 'h2', txnSig: 'hash1'),
                buySell: BUY,
                orderType: LMT,
                status: NEW,
                qty: 1.0,
                px: 100.0,
                timestamp: 10001l
        ))
        assert ob.bids.size() == 2
        assert ob.bids.firstEntry().key == 100.0
        ob.bids.pollFirstEntry()
        assert ob.bids.size() == 1
        assert ob.bids.firstEntry().key == 99.0
        ob.bids.pollFirstEntry()
        assert ob.bids.isEmpty()
    }

    @Test
    void "should create an orderbook with ask in good order"() {
        def ob = new OrderBook('Option1')
        ob.asks.put(100.0 as Double, new PriceLevel())
        ob.asks.put(99.0 as Double, new PriceLevel())

        ob.asks.get(99.0 as Double).add(new Order(
                orderID: new OrderID(walletAddress: '0x1', exOrderId: 'ex2', userOrderId: 'o2', orderHash: 'h1', txnSig: 'hash2'),
                buySell: SELL,
                orderType: LMT,
                status: NEW,
                qty: 1.0,
                px: 99.0,
                timestamp: 10001l
        ))

        ob.asks.get(100.0 as Double).add(new Order(
                orderID: new OrderID(walletAddress: '0x1', exOrderId: 'ex1', userOrderId: 'o1', orderHash: 'h2', txnSig: 'hash1'),
                buySell: SELL,
                orderType: LMT,
                status: NEW,
                qty: 1.0,
                px: 100.0,
                timestamp: 10001l
        ))

        assert ob.asks.size() == 2
        assert ob.asks.firstEntry().key == 99.0
        ob.asks.pollFirstEntry()
        assert ob.asks.size() == 1
        assert ob.asks.firstEntry().key == 100.0
        ob.asks.pollFirstEntry()
        assert ob.asks.isEmpty()
    }

    @Test
    void "should create a passive orderbook"() {
        def ob = new OrderBook('Option1')

        ob.matchThenAdd(new Order(
                orderID: new OrderID(walletAddress: '0x1', exOrderId: 'ex1', userOrderId: 'o1', txnSig: 'hash1'),
                buySell: BUY,
                orderType: LMT,
                status: NEW,
                qty: 1.0,
                px: 4400.0,
                timestamp: 10001l
        ))

        ob.matchThenAdd(new Order(
                orderID: new OrderID(walletAddress: '0x2', exOrderId: 'ex2', userOrderId: 'o2', txnSig: 'hash2'),
                buySell: SELL,
                orderType: LMT,
                status: NEW,
                qty: 1.0,
                px: 4450.0,
                timestamp: 10001l
        ))
        ob.matchThenAdd(new Order(
                orderID: new OrderID(walletAddress: '0x3', exOrderId: 'ex3', userOrderId: 'o3', txnSig: 'hash3'),
                buySell: SELL,
                orderType: LMT,
                status: NEW,
                qty: 2.0,
                px: 4450.0,
                timestamp: 9999l
        ))
        ob.matchThenAdd(new Order(
                orderID: new OrderID(walletAddress: '0x4', exOrderId: 'ex4', userOrderId: 'o4', txnSig: 'hash4'),
                buySell: SELL,
                orderType: LMT,
                status: NEW,
                qty: 5.0,
                px: 4455.0,
                timestamp: 10001l
        ))

        assert ob.bids.size() == 1
        assert ob.asks.size() == 2

        def matches = ob.matchThenAdd(new Order(
                orderID: new OrderID(walletAddress: '0x5', exOrderId: 'ex5', userOrderId: 'o5', txnSig: 'hash5'),
                buySell: BUY,
                orderType: LMT,
                status: NEW,
                qty: 5.0,
                px: 4600.0,
                timestamp: 10001l
        ))

        assert matches.collect {
            [
                    'bidUserId': it.bidOrderId.userOrderId,
                    'askUserId': it.askOrderId.userOrderId,
                    'qty'      : it.qty,
                    'px'       : it.px
            ]
        } == [
                [
                        'bidUserId': 'o5',
                        'askUserId': 'o3',
                        'qty'      : 2.0,
                        'px'       : 4450.0
                ],
                [
                        'bidUserId': 'o5',
                        'askUserId': 'o2',
                        'qty'      : 1.0,
                        'px'       : 4450.0
                ],
                [
                        'bidUserId': 'o5',
                        'askUserId': 'o4',
                        'qty'      : 2.0,
                        'px'       : 4455.0
                ]
        ]

        assert ob.bids.size() == 1
        assert ob.asks.size() == 1

        matches = ob.matchThenAdd(new Order(
                orderID: new OrderID(walletAddress: '0x5', exOrderId: 'ex6', userOrderId: 'o6', txnSig: 'hash6'),
                buySell: BUY,
                orderType: MKT,
                status: NEW,
                notional: 500,
                px: Double.NaN,
                timestamp: 10001l
        ))

        matches.each { println "${it.bidOrderId.userOrderId}, ${it.askOrderId.userOrderId}, ${it.px}, ${it.qty}" }

        assert matches.collect {
            [
                    'bidUserId': it.bidOrderId.userOrderId,
                    'askUserId': it.askOrderId.userOrderId,
                    'qty'      : it.qty,
                    'px'       : it.px
            ]
        } == [
                [
                        'bidUserId': 'o6',
                        'askUserId': 'o4',
                        'qty'      : 0.1122334455667789,
                        'px'       : 4455
                ]
        ]

        ob.bids.each {
            println "Bid, px: ${it.key}, ${it.value.orders.collect { it.qty }.sum()}"
        }
        ob.asks.each {
            println "Bid, px: ${it.key}, ${it.value.orders.collect { it.qty }.sum()}"
        }

        matches = ob.matchThenAdd(new Order(
                orderID: new OrderID(walletAddress: '0x7', exOrderId: 'ex7', userOrderId: 'o7', txnSig: 'hash7'),
                buySell: BUY,
                orderType: MKT,
                status: NEW,
                notional: 500000,
                px: Double.NaN,
                timestamp: 10001l
        ))

        assert matches.collect {
            [
                    'bidUserId': it.bidOrderId.userOrderId,
                    'askUserId': it.askOrderId.userOrderId,
                    'qty'      : it.qty,
                    'px'       : it.px
            ]
        } == [
                [
                        'bidUserId': 'o7',
                        'askUserId': 'o4',
                        'qty'      : 2.887766554433221,
                        'px'       : 4455
                ]
        ]

        matches.each {
            println "${it.instrId}, ${it.qty}, ${it.px}, ${it.filledSide}, ${it.aggressiveSide}..."
        }

    }


    @Test
    void "should process MKT order against a deep OB"() {
        def ob = new OrderBook('Option1')
        def matches = [] as Collection<Match>
        constructOrder("ob1.csv").each { order ->
            println "Got order: ${order.orderType}, ${order.buySell}, ${order.notional}, ${order.qty},${order.px}"
            matches.addAll(ob.matchThenAdd(order))
        }
        matches.each {
            println "${it.filledSide},${it.aggressiveSide},${it.bidOrderId.exOrderId},${it.askOrderId.exOrderId},${it.qty},${it.px}"
        }
    }

    @Test
    void "should process MKT order against an illiquid OB"() {
        def ob = new OrderBook('Option1')
        def matches = [] as Collection<Match>
        constructOrder("ob2.csv").each { order ->
            println "Got order: ${order.orderType}, ${order.buySell}, ${order.notional}, ${order.qty},${order.px}"
            matches.addAll(ob.matchThenAdd(order))
        }
        matches.each {
            println "${it.filledSide},${it.aggressiveSide},${it.bidOrderId.exOrderId},${it.askOrderId.exOrderId},${it.qty},${it.px}"
        }
    }

    @Test
    void "should process bid sweeping order"() {
        def ob = new OrderBook('Option1')
        def matches = [] as Collection<Match>
        constructOrder("bid-sweeping-ob.csv").each { order ->
            println order
            matches.addAll(ob.matchThenAdd(order))
        }
        assert matches.size() == 2
    }

    @Test
    void "should process cxl order with px"() {
        def ob = new OrderBook('Option1')
        constructOrder("cxlorderswithpx-ob.csv").each { order ->
            ob.matchThenAdd(order)
        }
        assert ob.asks.size() == 2
    }

    @Test
    void "should process cxl order without px"() {
        def ob = new OrderBook('Option1')
        constructOrder("cxlorderswithoutpx-ob.csv").each { order ->
            ob.matchThenAdd(order)
        }
        assert ob.asks.size() == 2
    }

    @Test
    void "should read csv file"() {
        assert constructOrder("ob1.csv").size() == 4
    }

    Collection<Order> constructOrder(String csvFile) {
        def orders = []
        boolean firstLine = true
        def headers = [] as ArrayList
        lines(csvFile).each {
            if (firstLine) {
                headers.addAll(it.split(",").collect { it.trim() })
            } else {
                def lineTok = it.split(",").collect { it.trim() }
                def orderMap = [:]
                lineTok.eachWithIndex { val, idx ->
                    orderMap.put(headers[idx],
                            'notional,amount,price'.split(',').contains(headers[idx]) ? ("NaN" == val ? Double.NaN : Double.valueOf(val)) :
                                    (headers[idx] == 'in_timestamp' ? Long.valueOf(val) :
                                            (headers[idx] == 'buy_sell' ? Integer.valueOf(val) : val)))
                }
                orders.add(Order.fromMap(orderMap))
            }
            firstLine = false
        }
        return orders
    }

}
