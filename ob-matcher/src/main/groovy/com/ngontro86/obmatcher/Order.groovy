package com.ngontro86.obmatcher

class Order {
    OrderID orderID
    BuySell buySell
    OrderType orderType
    OrderStatus status
    double qty

    double notional

    double px
    long timestamp

    static Order fromMap(Map map) {
        return new Order(
                orderID: OrderID.fromMap(map),
                buySell: BuySell.buySell(map['buy_sell']),
                orderType: OrderType.orderType(map['order_type']),
                status: OrderStatus.NEW,
                qty: map['amount'],
                px: map['price'],
                notional: map['notional'],
                timestamp: map['in_timestamp']
        )
    }


    boolean done() {
        return orderType == OrderType.LMT ? qty == 0d : notional == 0d
    }

    @Override
    boolean equals(Object o) {
        if (o instanceof Order) {
            def order = (Order) o
            return orderID.equals(order.orderID)
        }
        return false
    }

    @Override
    int hashCode() {
        return orderID.hashCode()
    }

    @Override
    String toString() {
        return "Order: orderId: ${orderID}, buy_sell:${buySell}, qty: ${qty}, px: ${px}".toString()
    }

}
