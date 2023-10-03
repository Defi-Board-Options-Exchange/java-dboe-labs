package com.ngontro86.obmatcher

import org.junit.Test

import static com.ngontro86.obmatcher.OrderType.*

class OrderTypeTest {
    @Test
    void "should return order type enum"() {
        assert orderType("LMT") == LMT
        assert orderType("MKT") == MKT

        try {
            orderType("MLT")
        } catch (IllegalArgumentException e) {
            assert true
        }
    }

}
