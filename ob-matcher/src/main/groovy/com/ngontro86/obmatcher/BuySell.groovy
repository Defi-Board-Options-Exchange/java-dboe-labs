package com.ngontro86.obmatcher

enum BuySell {
    BUY,
    SELL

    static BuySell buySell(int bs) {
        return bs == 1 ? BUY : SELL
    }

    int toInt() {
        return this == BUY ? 1 : 2
    }

}