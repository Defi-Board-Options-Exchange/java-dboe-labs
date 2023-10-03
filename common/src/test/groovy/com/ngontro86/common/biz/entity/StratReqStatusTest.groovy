package com.ngontro86.common.biz.entity

import org.junit.Test

class StratReqStatusTest {

    @Test
    void "should build strat req status"() {
        def stratReq = StratReqStatus.build(
                [
                        "strategy_name"  : "Mini HS",
                        "strategy_req_id": "Mini HS^0",
                        "leg"            : 2,
                        "status"         : "FILLED",
                        "timestamp"      : 1497449918224
                ]
        )

        assert stratReq.strategyName == 'Mini HS'
        assert stratReq.status == 'FILLED'
        assert stratReq.leg == 2
        assert stratReq.strategyReqId == 'Mini HS^0'
    }
}
