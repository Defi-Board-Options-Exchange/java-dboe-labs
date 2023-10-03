package com.ngontro86.common.biz.entity

import org.junit.Test

import static com.ngontro86.common.biz.entity.OrderStatus.*

class OrderStatusTest {

    @Test
    void "should get Order Status"() {
        assert status('Filled') == FILLED
        assert status('Partialfill') == PARTIAL_FILLED
        assert status('') == ACKED
    }
}
