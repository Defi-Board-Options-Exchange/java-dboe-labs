package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils


class Price implements Comparable<Price> {
    public String instId
    public double bid, lastPrice, ask, openPrice, closePrice, microPrice
    public double bidSize, askSize, lastSize
    public long timestamp

    private Price() {}

    static Price build(Map map) {
        final def price = new Price()
        BeanUtils.copyProperties(price, map,
                [
                        'inst_id'    : 'instId',
                        'last_price' : 'lastPrice',
                        'open_price' : 'openPrice',
                        'close_price': 'closePrice',
                        'micro_price': 'microPrice',
                        'bid_size'   : 'bidSize',
                        'ask_size'   : 'askSize',
                        'last_size'  : 'lastSize'
                ]
        )
        return price
    }

    double getBidSize() {
        return bidSize
    }

    void setBidSize(double bidSize) {
        this.bidSize = bidSize
    }

    double getAskSize() {
        return askSize
    }

    void setAskSize(double askSize) {
        this.askSize = askSize
    }

    double getLastSize() {
        return lastSize
    }

    void setLastSize(double lastSize) {
        this.lastSize = lastSize
    }

    String getInstId() {
        return instId
    }

    void setInstId(String instId) {
        this.instId = instId
    }

    double getBid() {
        return bid
    }

    void setBid(double bid) {
        this.bid = bid
    }

    double getLastPrice() {
        return lastPrice
    }

    void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice
    }

    double getAsk() {
        return ask
    }

    void setAsk(double ask) {
        this.ask = ask
    }

    double getOpenPrice() {
        return openPrice
    }

    void setOpenPrice(double openPrice) {
        this.openPrice = openPrice
    }

    double getClosePrice() {
        return closePrice
    }

    void setClosePrice(double closePrice) {
        this.closePrice = closePrice
    }

    double getMicroPrice() {
        return microPrice
    }

    void setMicroPrice(double microPrice) {
        this.microPrice = microPrice
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    @Override
    int compareTo(Price arg0) {
        return 1;
    }

    @Override
    String toString() {
        return String.format("inst_id: %s; bid: %.6f; ask: %.6f; mp: %.6f", instId, bid, ask, microPrice);
    }
}

