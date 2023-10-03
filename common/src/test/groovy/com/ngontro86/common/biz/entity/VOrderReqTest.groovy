package com.ngontro86.common.biz.entity

import org.junit.Test

class VOrderReqTest {

    @Test
    void "should set properties"() {
        def bean = VOrderReq.build([
                'verified_timestamp': 10l,
                'inst_id'           : 'FO',
                'qty'               : 10
        ])
        println bean
        assert bean.verifiedTimestamp == 10l
        assert bean.getQty() == 10
        assert bean.instId == 'FO'
    }

}
