package com.ngontro86.obmatcher

class PriceLevel {

    BuySell buySell
    Double px

    PriorityQueue<Order> orders

    Comparator<? super Order> timeAscendingComparator = new Comparator<Order>() {
        @Override
        int compare(Order o1, Order o2) {
            return o1.timestamp.compareTo(o2.timestamp)
        }
    }

    PriceLevel() {
        orders = new PriorityQueue<Order>(50, timeAscendingComparator)
    }

    void remove(Order o) {
        orders.remove(o)
    }

    void remove(OrderID orderID) {
        orders.remove(new Order(orderID: orderID))
    }

    void add(Order o) {
        if (buySell == null) {
            buySell = o.buySell
        }
        if(px == null) {
            px = o.px
        }
        if (buySell != o.buySell) {
            throw new IllegalStateException("Adding buy and sell into the same price level")
        }
        if(px != o.px) {
            throw new IllegalStateException("Adding orders with different px into the same price level")
        }
        orders.offer(o)
    }
}
