package com.ngontro86.server.dboe.services

import com.ngontro86.common.times.GlobalTimeController
import org.junit.Test

import static com.ngontro86.server.dboe.services.Utils.dboeOptionTimeToExpiry
import static java.lang.Math.abs

class UtilsTest {

    @Test
    void "should return correct time to expiry for DBOE Option"() {
        assert abs(dboeOptionTimeToExpiry(20220821, 1660452161251) - 0.02) < 1.0/365.0
    }

    @Test
    void "should price option with shocks"() {
        def shocks = Utils.priceOptionWithShocks(true, 20220821, 1660452161251L, 2000, 2000.0, 2200.0, 125, [-0.25, 0, 0.25] as double[], [-0.25, 0, +0.25] as double[])
        shocks.each {
            println it
        }
    }

    @Test
    void "should work out txn fee"() {
        assert Utils.txnFee(2.0, 10.0, 1000) == 2.0
        assert Utils.txnFee(2.0, 10.0, 20000) == 20.0
    }

    @org.junit.Test
    void "test default return value"() {
        println new DefaultReturnTest().ret()
    }

}
