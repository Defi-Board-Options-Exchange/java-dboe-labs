package com.ngontro86.algo.research


class OneTrade {

    public int date
    public long timestamp
    public SIDE side
    public int qty
    public double price
    // average spread and fixed commission
    // 50 * 0.5 * (1d + 1.8d) + (30d * 1d + 1.8d * 20d)
    public double comm
    public String reason
}
