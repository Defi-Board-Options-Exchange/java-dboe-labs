package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils


public class Signal implements Comparable<Signal> {
    public String refSignal, instId, orderReqId
    public double refPrice

    public long timestamp

    private Signal() {}

    public static Signal build(Map map) {
        def sig = new Signal()
        BeanUtils.copyProperties(sig, map,
                [
                        'ref_signal'  : 'refSignal',
                        'inst_id'     : 'instId',
                        'order_req_id': 'orderReqId',
                        'ref_price'   : 'refPrice'
                ])
        return sig
    }

    String getRefSignal() {
        return refSignal
    }

    void setRefSignal(String refSignal) {
        this.refSignal = refSignal
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

    double getRefPrice() {
        return refPrice
    }

    void setRefPrice(double refPrice) {
        this.refPrice = refPrice
    }

    long getTimestamp() {
        return timestamp
    }

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp
    }

    @Override
    public int compareTo(Signal arg0) {
        return 1
    }

    @Override
    public String toString() {
        return String.format("RefSignal: %s; instId: %s; orderReqId: %s; refPrice: %.4f", refSignal, instId, orderReqId, refPrice);
    }
}
