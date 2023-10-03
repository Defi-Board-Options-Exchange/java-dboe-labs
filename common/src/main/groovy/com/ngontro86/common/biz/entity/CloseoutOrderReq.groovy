package com.ngontro86.common.biz.entity

import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.util.BeanUtils


class CloseoutOrderReq extends AbstractOrder implements Comparable<CloseoutOrderReq> {

    private ObjMap orderReq
    private OrderStatus orderStatus

    private CloseoutOrderReq() {}

    static CloseoutOrderReq build(Map map) {
        final CloseoutOrderReq req = new CloseoutOrderReq()
        BeanUtils.copyProperties(req, map,
                [
                        'inst_id'          : 'instId',
                        'order_req_id'     : 'orderReqId',
                        'exchange_order_id': 'exchangeOrderId'
                ]
        )
        req.orderReq = new ObjMap('OrderReqEvent', map)
        return req
    }

    ObjMap getOrderReq() {
        return orderReq
    }

    @Override
    int compareTo(CloseoutOrderReq o) {
        return 1
    }

    void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus
    }

    OrderStatus getOrderStatus() {
        return orderStatus
    }

    void updateExchangeOrderId(String exchangeOrderId) {
        this.orderReq.put("exchange_order_id", exchangeOrderId)
        super.setExchangeOrderId(exchangeOrderId)
    }



}
