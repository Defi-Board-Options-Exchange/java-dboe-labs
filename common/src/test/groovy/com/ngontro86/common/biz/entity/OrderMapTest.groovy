package com.ngontro86.common.biz.entity

import com.ngontro86.common.biz.entity.OrderMap
import org.junit.Test


class OrderMapTest {

    @Test
    void "should build Order Map using BeanUtils 1"() {
        def om = OrderMap.build(
                [
                        'inst_id'          : 'I1',
                        'order_req_id'     : 'OR1',
                        'exchange_order_id': '100',
                        'broker'           : 'IB',
                        'account'          : 'acc1',
                        'portfolio'        : 'Global',
                        'side'             : 1,
                        'timestamp'        : 10l
                ]
        )
        assert om.instId == 'I1'
        assert om.orderReqId == 'OR1'
        assert om.exchangeOrderId == '100'
        assert om.portfolio == 'Global'
        assert om.side == 1
        assert om.timestamp == 10l
    }

}
