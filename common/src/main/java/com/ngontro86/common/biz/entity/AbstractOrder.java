package com.ngontro86.common.biz.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AbstractOrder {
    private String instId, orderReqId, exchangeOrderId, broker, account, portfolio;
    private double qty;
    private double price;
    private long timestamp;

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getOrderReqId() {
        return orderReqId;
    }

    public void setOrderReqId(String orderReqId) {
        this.orderReqId = orderReqId;
    }

    public String getExchangeOrderId() {
        return exchangeOrderId;
    }

    public void setExchangeOrderId(String exchangeOrderId) {
        this.exchangeOrderId = exchangeOrderId;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("instId", instId)
                .append("orderReqId", orderReqId)
                .append("exchangeOrderId", exchangeOrderId)
                .append("broker", broker)
                .append("account", account)
                .append("portfolio", portfolio)
                .append("qty", qty)
                .append("price", price)
                .append("timestamp", timestamp)
                .toString();
    }
}
