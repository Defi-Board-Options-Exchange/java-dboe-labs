package com.ngontro86.common.biz.entity

import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.util.BeanUtils

class OrderReq extends AbstractOrder implements Comparable<OrderReq> {

    public ObjMap verifiedOrder, rejectedOrder

    static OrderReq build(Map map) {
        final OrderReq req = new OrderReq()
        BeanUtils.copyProperties(req, map,
                [
                        'inst_id'          : 'instId',
                        'order_req_id'     : 'orderReqId',
                        'exchange_order_id': 'exchangeOrderId'
                ]
        )
        req.verifiedOrder = getOrder(map, true)
        req.rejectedOrder = getOrder(map, false)
        return req
    }

    static ObjMap getOrder(Map<String, ? extends Object> map, boolean verified) {
        return new ObjMap(verified ? 'OrderReqVerifiedEvent' : 'OrderReqRejectedEvent', map)
    }

    @Override
    int compareTo(OrderReq o) {
        return 1
    }
}

