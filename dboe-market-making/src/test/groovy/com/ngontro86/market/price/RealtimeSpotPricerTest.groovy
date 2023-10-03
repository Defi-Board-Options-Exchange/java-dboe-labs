package com.ngontro86.market.price

import org.junit.Test

class RealtimeSpotPricerTest {

    @Test
    void "should update prev and latest map"() {
        def spotPricer = new RealtimeSpotPricer()
        spotPricer.update('ETH', 1800)
        assert spotPricer.spot('ETH') == 1800
    }
}
