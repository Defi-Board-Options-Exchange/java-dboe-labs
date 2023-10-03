package com.ngontro86.obmatcher

import static com.ngontro86.obmatcher.OrderType.MKT

class Match {
    String instrId

    OrderID bidOrderId
    OrderID askOrderId

    double qty
    double px
    BuySell aggressiveSide
    BuySell filledSide

    Map toMap() {
        return [
                'instr_id'       : instrId,
                'bid_wallet_id'  : bidOrderId.walletAddress,
                'ask_wallet_id'  : askOrderId.walletAddress,
                'bid_ex_order_id': bidOrderId.exOrderId,
                'ask_ex_order_id': askOrderId.exOrderId,
                'matched_price'  : px,
                'matched_amount' : qty,
                'side_filled'    : filledSide.toInt(),
                'aggressor'      : aggressiveSide.toInt()
        ]
    }

    @Override
    String toString() {
        return "Match:${instrId}, Bid: ${bidOrderId}, Ask: ${askOrderId}, qty: ${qty}, px: ${px}, filled side: ${filledSide}"
    }

    static Match match(String instrId, Order aggressiveOrder, Order passiveOrder) {
        def filledQty = aggressiveOrder.orderType == MKT ? Math.min(aggressiveOrder.notional / passiveOrder.px, passiveOrder.qty) : Math.min(aggressiveOrder.qty, passiveOrder.qty)
        def filledSide = filledQty == passiveOrder.qty ? passiveOrder.buySell : aggressiveOrder.buySell
        return new Match(
                instrId: instrId,
                px: passiveOrder.px,
                qty: filledQty,
                aggressiveSide: aggressiveOrder.buySell,
                filledSide: filledSide,
                bidOrderId: aggressiveOrder.buySell == BuySell.BUY ? aggressiveOrder.orderID : passiveOrder.orderID,
                askOrderId: aggressiveOrder.buySell == BuySell.BUY ? passiveOrder.orderID : aggressiveOrder.orderID
        )
    }

}
