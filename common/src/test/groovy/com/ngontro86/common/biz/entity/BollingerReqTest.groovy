package com.ngontro86.common.biz.entity

import org.junit.Test


class BollingerReqTest {

    @Test
    void "should build req 1"() {
        def req = BollingerReq.build(
                [
                        'min_d'        : 10d,
                        'max_loss'     : 105d,
                        'delete_after' : 5,
                        'aggressive'   : 1,
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
        assert req.minD == 10d
        assert req.maxLossBps == 105d
        assert req.deleteAfter == 5
        assert req.aggressive == 1
        assert req.imbalance == 10d
        assert req.hl == 'hl'
        assert req.strategyName == 'EURO 1'
        assert req.refSignal == 'EURO Pair'
        assert req.reqId == '1'
        assert req.qty == 5
        assert req.broker == 'IB'
        assert req.account == 'acc1'
        assert req.portfolio == 'Global'

    }


}
