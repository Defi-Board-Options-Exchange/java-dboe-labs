package com.ngontro86.common.biz.entity

import com.ngontro86.common.biz.entity.Trade
import org.junit.Test

class TradeTest {

    @Test
    void "should build Trade 1"() {
        def trade = Trade.build(
                [
                        'inst_id'          : 'I1',
                        'order_req_id'     : 'o1',
                        'exchange_order_id': '10',
                        'portfolio'        : 'Global',
                        'avg_price'        : 100d,
                        'size'             : 2,
                        'status'           : 'Partial_Filled',
                        'timestamp'        : 10l
                ]
        )

        assert trade.instId == 'I1'
        assert trade.avgPrice == 100d
        assert trade.size == 2
        assert trade.status == 'Partial_Filled'
        assert trade.orderReqId == 'o1'
        assert trade.exchangeOrderId == '10'
        assert trade.portfolio == 'Global'
    }

    @Test
    void "should build Trade 2"() {
        def trade1 = Trade.build(
                [
                        'inst_id'          : 'I1',
                        'order_req_id'     : 'o1',
                        'exchange_order_id': '10',
                        'portfolio'        : 'Global',
                        'broker'           : 'IB',
                        'account'          : 'acc1',
                        'avg_price'        : 100d,
                        'size'             : 2,
                        'status'           : 'Partial_Filled',
                        'timestamp'        : 10l
                ]
        )

        def trade2 = Trade.build(
                [
                        'inst_id'          : 'I1',
                        'order_req_id'     : 'o1',
                        'exchange_order_id': '10',
                        'portfolio'        : 'Global',
                        'broker'           : 'IB',
                        'account'          : 'acc1',
                        'avg_price'        : 100d,
                        'size'             : 2,
                        'status'           : 'Partial_Filled',
                        'timestamp'        : 10l
                ]
        )

        assert trade1.compareTo(trade2) == 0

        def trade3 = Trade.build(
                [
                        'inst_id'          : 'I1',
                        'order_req_id'     : 'o1',
                        'exchange_order_id': '10',
                        'portfolio'        : 'Global',
                        'broker'           : 'IB',
                        'account'          : 'acc1',
                        'avg_price'        : 100d,
                        'size'             : 2,
                        'status'           : 'Filled',
                        'timestamp'        : 10l
                ]
        )

        assert trade1.compareTo(trade3) > 0
    }

}
