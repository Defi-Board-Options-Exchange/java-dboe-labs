package com.ngontro86.common.biz.entity

import com.ngontro86.common.util.BeanUtils


class VOrderReq extends AbstractOrder implements Comparable<VOrderReq> {

    private long verifiedTimestamp

    private VOrderReq() {
    }

    static VOrderReq build(Map map) {
        final VOrderReq req = new VOrderReq()
        BeanUtils.copyProperties(req, map,
                [
                        'inst_id'           : 'instId',
                        'order_req_id'      : 'orderReqId',
                        'exchange_order_id' : 'exchangeOrderId',
                        'verified_timestamp': 'verifiedTimestamp'
                ]
        )
        return req
    }

    long getVerifiedTimestamp() {
        return verifiedTimestamp
    }

    void setVerifiedTimestamp(long verifiedTimestamp) {
        this.verifiedTimestamp = verifiedTimestamp
    }

    @Override
    int compareTo(VOrderReq arg0) {
        return 1
    }

    @Override
    String toString() {
        return "VOrderReq{" +
                "verifiedTimestamp=" + verifiedTimestamp +
                "," + super.toString() +
                '}'
    }
}
