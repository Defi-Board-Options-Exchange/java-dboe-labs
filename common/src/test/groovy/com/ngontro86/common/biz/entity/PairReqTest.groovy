package com.ngontro86.common.biz.entity

import org.junit.Test

import static com.ngontro86.utils.MapEqualUtils.mapContains

class PairReqTest {

    @Test
    void "should build order 1"() {
        def bean = PairReq.build([
                'pairName'                  : 'Hangseng',
                'pairOrderId'               : '1',
                'qty'                       : 1,
                'basis'                     : 0.5,
                'maxLoss'                   : 1500d,
                'timestamp'                 : 10l,
                'firstLegInstId'            : 'I1',
                'secondLegPrimary'          : 'I2',
                'firstLegMultiplier'        : 10d,
                'secondLegPrimaryMultiplier': 50d
        ])
        assert bean.pairOrderId == '1'
        assert bean.pairName == 'Hangseng'
        assert bean.qty == 1
        assert bean.timestamp == 10l
        assert bean.firstLegMultiplier == 10d
        assert bean.secondLegPrimaryMultiplier == 50d
        assert bean.maxLoss == 1500d

        assert !bean.startedExit
    }

    @Test
    void "should build order 2"() {
        def pairReq = PairReq.build([
                'pairName'                  : 'Hangseng',
                'portfolio'                 : 'Global',
                'broker'                    : 'IB',
                'account'                   : 'ngontro86',
                'firstLegInstId'            : 'I1',
                'secondLegPrimaryInstId'    : 'I2',
                'secondLegSecondaryInstId'  : '',
                'firstLegMultiplier'        : 10d,
                'secondLegPrimaryMultiplier': 50d,
                'pairOrderId'               : '1',
                'qty'                       : 1,
                'secondLegQty'              : -1.7d,
                'exitBasis'                 : 0.1d,
                'basis'                     : 0.5,
                'maxLoss'                   : 1500d,
                'timestamp'                 : 10l
        ])

        assert pairReq.pairOrderId == '1'
        assert pairReq.portfolio == 'Global'
        assert pairReq.pairName == 'Hangseng'
        assert pairReq.qty == 1
        assert pairReq.timestamp == 10l

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
    void "should build order 3"() {
        def pairReq = PairReq.build([
                'pairName'                    : 'Hangseng',
                'portfolio'                   : 'Global',
                'broker'                      : 'IB',
                'account'                     : 'ngontro86',
                'firstLegInstId'              : 'I1',
                'secondLegPrimaryInstId'      : 'I2',
                'secondLegSecondaryInstId'    : 'I3',
                'firstLegMultiplier'          : 10d,
                'secondLegPrimaryMultiplier'  : 50d,
                'secondLegSecondaryMultiplier': 10d,
                'pairOrderId'                 : '1',
                'qty'                         : 2,
                'secondLegQty'                : -0.7d,
                'exitBasis'                   : 0.1d,
                'basis'                       : 0.5,
                'timestamp'                   : 10l
        ])

        assert mapContains(pairReq.prebuiltOrderMap[true]['1-Entry-1'],
                [
                        'inst_id'     : 'I2',
                        'qty'         : -1,
                        'order_req_id': '1-Entry-1'
                ]
        )

        assert mapContains(pairReq.prebuiltOrderMap[true]['1-Entry-2'],
                [
                        'inst_id'     : 'I3',
                        'qty'         : 2,
                        'order_req_id': '1-Entry-2'
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
