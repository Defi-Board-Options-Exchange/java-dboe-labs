package com.ngontro86.common.biz.entity

import org.junit.Test


class TrendReqTest {
    @Test
    void "should build trend req 1"() {
        def req = TrendReq.build(
                [
                        'max_loss'     : 105d,
                        'delete_after' : 5,
                        'exit_coeff'   : 0.5d,
                        'imbalance'    : 10d,
                        'hl'           : 'hl',
                        'strategy_name': 'EURO 1',
                        'ref_signal'   : 'EURO Pair',
                        'inst_id'      : 'EURO/USD',
                        'req_id'       : '1',
                        'qty'          : 5,
                        'broker'       : 'IB',
                        'account'      : 'acc1',
                        'portfolio'    : 'Global',
                ]
        )
        assert req.instId == 'EURO/USD'
        assert req.exitCoeff == 0.5d
        assert req.maxLossBps == 105d
        assert req.deleteAfter == 5
        assert req.strategyName == 'EURO 1'
        assert req.refSignal == 'EURO Pair'
        assert req.reqId == '1'
        assert req.qty == 5
        assert req.broker == 'IB'
        assert req.account == 'acc1'
        assert req.portfolio == 'Global'
    }

}
