package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils
import org.apache.commons.lang3.builder.CompareToBuilder
import org.apache.commons.lang3.builder.ToStringBuilder


class Trade implements Comparable<Trade> {
    public String instId, orderReqId, exchangeOrderId, broker, portfolio, account
    public double size
    public double avgPrice
    public String status
    public long timestamp

    private Trade() {}

    static Trade build(Map map) {
        final def trade = new Trade()
        BeanUtils.copyProperties(trade, map,
                [
                        'inst_id'          : 'instId',
                        'order_req_id'     : 'orderReqId',
                        'exchange_order_id': 'exchangeOrderId',
                        'avg_price'        : 'avgPrice'
                ]
        )
        return trade
    }

    String getBroker() {
        return broker
    }

    void setBroker(String broker) {
        this.broker = broker
    }

    String getAccount() {
        return account
    }

    void setAccount(String account) {
        this.account = account
    }

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

    String getPortfolio() {
        return portfolio
    }

    void setPortfolio(String portfolio) {
        this.portfolio = portfolio
    }

    double getSize() {
        return size
    }

    void setSize(double size) {
        this.size = size
    }

    double getAvgPrice() {
        return avgPrice
    }

    void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice
    }

    String getStatus() {
        return status
    }

    void setStatus(String status) {
        this.status = status
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    @Override
    int compareTo(Trade arg0) {
        return new CompareToBuilder()
                .append(this.instId, arg0.instId)
                .append(this.broker, arg0.broker)
                .append(this.account, arg0.account)
                .append(this.portfolio, arg0.portfolio)
                .append(this.orderReqId, arg0.orderReqId)
                .append(this.exchangeOrderId, arg0.exchangeOrderId)
                .append(this.size, arg0.size)
                .append(this.avgPrice, arg0.avgPrice)
                .append(this.status, arg0.status)
                .toComparison()
    }

    @Override
    String toString() {
        return new ToStringBuilder(this)
                .append("instId", instId)
                .append("orderReqId", orderReqId)
                .append("exchangeOrderId", exchangeOrderId)
                .append("broker", broker)
                .append("portfolio", portfolio)
                .append("account", account)
                .append("size", size)
                .append("avgPrice", avgPrice)
                .append("status", status)
                .append("timestamp", timestamp)
                .toString();
    }
}
