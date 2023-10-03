package com.ngontro86.common.biz.entity

import org.junit.Ignore
import org.junit.Test

import static com.ngontro86.utils.MapEqualUtils.mapContains

@Ignore
class SnipingPairReqTest {

    @Test
    void "should build order 1"() {
        def pairReq = PairReq.build([
                'pairName'       : 'TW-Nifty',
                'pairOrderId'    : '1',
                'basis'          : 0.5,
                'maxLoss'        : 1500d,
                'timestamp'      : 10l,
                'firstLegInstId' : 'I1',
                'secondLegInstId': 'I2',
                'firstLegQty'    : 1,
                'secondLegQty'   : -2,
                'isBuy'          : false
        ])
        assert pairReq.pairOrderId == '1'
        assert pairReq.pairName == 'TW-Nifty'
        //assert pairReq.firstLegQty == 1
        assert pairReq.secondLegQty == -2
        assert pairReq.firstLegInstId == 'I1'
        //assert pairReq.secondLegInstId == 'I2'
        assert pairReq.maxLoss == 1500d
        assert pairReq.timestamp == 10l

        assert !pairReq.startedExit


        def entries = pairReq.prebuiltOrderMap[true]
        assert mapContains(entries['1-Entry-0'],
                [
                        'inst_id'     : 'I1',
                        'qty'         : 1,
                        'order_req_id': '1-Entry-0'
                ]
        )

        assert mapContains(entries['1-Entry-1'],
                [
                        'inst_id'     : 'I2',
                        'qty'         : -2,
                        'order_req_id': '1-Entry-1'
                ]
        )
        def exits = pairReq.prebuiltOrderMap[false]
        assert mapContains(exits['1-Exit-0'],
                [
                        'inst_id'     : 'I1',
                        'qty'         : -1,
                        'order_req_id': '1-Exit-0'
                ]
        )
        assert mapContains(exits['1-Exit-1'],
                [
                        'inst_id'     : 'I2',
                        'qty'         : 2,
                        'order_req_id': '1-Exit-1'
                ]
        )

        assert !pairReq.entryComplete()
        assert !pairReq.exitComplete()

        pairReq.setExchangeOrderId('Sim', '1-Exit-1')
        assert mapContains(exits['1-Exit-1'],
                [
                        'inst_id'          : 'I2',
                        'qty'              : 2,
                        'order_req_id'     : '1-Exit-1',
                        'exchange_order_id': 'Sim'
                ]
        )

        pairReq.setOrderStatus('1-Entry-0', OrderStatus.status('Filled'))
        pairReq.setOrderStatus('1-Entry-1', OrderStatus.status('Filled'))
        assert pairReq.entryComplete() && !pairReq.startedExit
        pairReq.setStartedExit(true)

        assert pairReq.startedExit
    }

    @Test
    void "should build order 2"() {
        def pairReq = PairReq.build([
                'pairName'       : 'TW-Nifty',
                'pairOrderId'    : '1',
                'basis'          : 0.5,
                'maxLoss'        : 1500d,
                'timestamp'      : 10l,
                'firstLegInstId' : 'I2',
                'secondLegInstId': 'I3',
                'firstLegQty'    : 1,
                'secondLegQty'   : -2
        ])

        assert mapContains(pairReq.prebuiltOrderMap[true]['1-Entry-0'],
                [
                        'inst_id'     : 'I2',
                        'qty'         : 1,
                        'order_req_id': '1-Entry-0'
                ]
        )

        assert mapContains(pairReq.prebuiltOrderMap[true]['1-Entry-1'],
                [
                        'inst_id'     : 'I3',
                        'qty'         : -2,
                        'order_req_id': '1-Entry-1'
                ]
        )

        pairReq.setOrderStatus('1-Entry-0', OrderStatus.status('Filled'))
        pairReq.setOrderStatus('1-Entry-1', OrderStatus.status('Submitted'))
        pairReq.setOrderStatus('1-Entry-2', OrderStatus.status('Filled'))
        assert !pairReq.entryComplete()
        pairReq.setOrderStatus('1-Entry-1', OrderStatus.status('Filled'))
        assert pairReq.entryComplete()
    }

}
