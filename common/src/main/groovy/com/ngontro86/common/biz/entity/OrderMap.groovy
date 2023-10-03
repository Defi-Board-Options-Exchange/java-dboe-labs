package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils


public class OrderMap implements Comparable<OrderMap> {
    public String instId, orderReqId, exchangeOrderId, broker, account, portfolio
    public int side
    public long timestamp

    private OrderMap() {
    }

    public static OrderMap build(Map map) {
        final OrderMap om = new OrderMap()
        BeanUtils.copyProperties(om, map,
                [
                        'inst_id'          : 'instId',
                        'order_req_id'     : 'orderReqId',
                        'exchange_order_id': 'exchangeOrderId'
                ]
        )
        return om;
    }

    @Override
    int compareTo(OrderMap arg0) {
        if (arg0 == null) {
            return 1;
        }
        if (this.orderReqId.equals(arg0.orderReqId) && this.exchangeOrderId.equals(arg0.exchangeOrderId)) {
            return 0;
        }
        return 1;
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

    String getPortfolio() {
        return portfolio
    }

    void setPortfolio(String portfolio) {
        this.portfolio = portfolio
    }

    int getSide() {
        return side
    }

    void setSide(int side) {
        this.side = side
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    @Override
    public String toString() {
        return "OrderMap{" +
                "instId='" + instId + '\'' +
                ", orderReqId='" + orderReqId + '\'' +
                ", exchangeOrderId='" + exchangeOrderId + '\'' +
                ", broker='" + broker + '\'' +
                ", account='" + account + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", side=" + side +
                ", timestamp=" + timestamp +
                '}';
    }
}

