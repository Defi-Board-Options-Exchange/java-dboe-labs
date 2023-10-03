package com.ngontro86.obmatcher

import static com.ngontro86.obmatcher.BuySell.BUY
import static com.ngontro86.obmatcher.OrderType.LMT
import static com.ngontro86.obmatcher.OrderType.MKT

class OrderBook {

    String instrId

    TreeMap<Double, PriceLevel> bids
    TreeMap<Double, PriceLevel> asks

    int numOfOrder = 0, numOfCancel = 0

    private final Comparator<Double> priceAscSorter = new Comparator<Double>() {
        @Override
        int compare(Double o1, Double o2) {
            return Objects.compare(o1, o2, Comparator.naturalOrder())
        }
    }

    OrderBook(String instrId) {
        this.instrId = instrId
        bids = new TreeMap<>(priceAscSorter.reversed())
        asks = new TreeMap<>(priceAscSorter)
    }

    private boolean isAggressive(Order order, double bbo) {
        return order.orderType == MKT || (order.buySell == BUY ? (order.px >= bbo) : (order.px <= bbo))
    }

    synchronized Collection<Match> matchThenAdd(Order order) {
        def matches = [] as Collection<Match>
        if (removeCxlOrder(order)) {
            numOfCancel++
            return matches
        }

        def otherOb = order.buySell == BUY ? asks : bids
        if (!otherOb.isEmpty()) {
            matches = match(order, otherOb)
        }
        if (order.orderType == LMT && !order.done()) {
            println "Added order: ${instrId}, ${order}"
            add(order)
        }
        numOfOrder++
        return matches
    }

    private void add(Order order) {
        def ob = order.buySell == BUY ? bids : asks
        if (order.px != Double.NaN && order.qty > 0) {
            ob.putIfAbsent(order.px, new PriceLevel())
            ob.get(order.px).add(order)
        }
    }

    private boolean priceLevelComplete(PriceLevel priceLevel) {
        return priceLevel == null || priceLevel.orders.isEmpty()
    }

    private boolean furtherMatch(Order order, PriceLevel priceLevel) {
        if (priceLevelComplete(priceLevel)) {
            return false
        }
        if (order.done()) {
            return false
        }
        return isAggressive(order, priceLevel.orders.peek().px)
    }

    private Collection<Match> match(Order order, TreeMap<Double, PriceLevel> ob) {
        def matches = [] as Collection<Match>

        def priceLevel = ob.isEmpty() ? null : ob.firstEntry().value
        while (!ob.isEmpty() && furtherMatch(order, priceLevel)) {
            def passiveOrder = priceLevel.orders.peek()
            if (passiveOrder.qty > 0) {
                def match = Match.match(instrId, order, passiveOrder)

                if (match.filledSide == order.buySell) {
                    if (order.orderType == MKT) {
                        order.notional = 0d
                    } else {
                        order.qty = 0d
                    }
                    passiveOrder.qty -= match.qty
                }

                if (match.filledSide == passiveOrder.buySell) {
                    passiveOrder.qty = 0d
                    if (order.orderType == MKT) {
                        order.notional -= match.qty * match.px
                    } else {
                        order.qty -= match.qty
                    }
                }

                matches << match
            }

            if (passiveOrder.done()) {
                priceLevel.orders.poll()
            }
            if (priceLevel.orders.isEmpty()) {
                ob.pollFirstEntry()
                priceLevel = ob.isEmpty() ? null : ob.firstEntry().value
            }
        }
        return matches
    }

    private boolean removeCxlOrder(Order order) {
        if (order.orderType == LMT && order.qty == 0) {
            def obSide = order.buySell == BUY ? bids : asks
            if (order.px != Double.NaN) {
                if (obSide.containsKey(order.px)) {
                    obSide.get(order.px).remove(order)
                    if (obSide.get(order.px).orders.isEmpty()) {
                        obSide.remove(order.px)
                    }
                }
            } else {
                obSide.each { px, level ->
                    level.remove(order)
                }
                def it = obSide.entrySet().iterator()
                while (it.hasNext()) {
                    if (it.next().value.orders.isEmpty()) {
                        it.remove()
                    }
                }
            }
            return true
        }

        return false
    }
}
