package com.ngontro86.common.biz.entity

class SimpleOrder {
    private String instId, orderReqId, exchangeOrderId
    private double qty
    private double price
    private long timestamp

    String getInstId() {
        return instId
    }

    void setInstId(String instId) {
        this.instId = instId
    }

    String getOrderReqId() {
        return orderReqId
    }

    void setOrderReqId(String orderReqId) {
        this.orderReqId = orderReqId
    }

    String getExchangeOrderId() {
        return exchangeOrderId
    }

    void setExchangeOrderId(String exchangeOrderId) {
        this.exchangeOrderId = exchangeOrderId
    }


    double getQty() {
        return qty
    }

    void setQty(double qty) {
        this.qty = qty
    }

    double getPrice() {
        return price
    }

    void setPrice(double price) {
        this.price = price
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }
}
