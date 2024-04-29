package com.ngontro86.server.dboe.volsurface

import org.junit.Test

class VolSpreadWingTest {

    @Test
    void "should compute vol smoothly"() {
        def expiry = 5 * 24 * 3600 * 1000L // 5 days
        def volSpreadWing = new VolSpreadWing(expiry)

        volSpreadWing.set_ref_price(3282.5d)
        volSpreadWing.refit(
                3282.5d,
                0, 0,
                [
                        new Spread(3000d, 3300d, Kind.C).set_bid_ask(203.6d, 207.7d),
                        new Spread(3100d, 3400d, Kind.C).set_bid_ask(156.6d, 159.7d),
                        new Spread(3200d, 3500d, Kind.C).set_bid_ask(109.7d, 111.9d),
                        new Spread(3300d, 3600d, Kind.C).set_bid_ask(69.7d, 71.1d),
                        new Spread(3400d, 3700d, Kind.C).set_bid_ask(41.0d, 42.1d),
                        new Spread(3500d, 3800d, Kind.C).set_bid_ask(23.5d, 24.0d),
                        //new Spread(3000d, 3300d, Kind.C).set_bid_ask(203.6d, 207.7d),
                        new Spread(2900d, 2600d, Kind.P).set_bid_ask(6.7d, 18.0d),
                        new Spread(3000d, 2700d, Kind.P).set_bid_ask(14.5d, 14.8d),
                        new Spread(3100d, 2800d, Kind.P).set_bid_ask(29.3d, 29.9d),
                        new Spread(3200d, 2900d, Kind.P).set_bid_ask(55.4d, 56.5d),
                        new Spread(3300d, 3000d, Kind.P).set_bid_ask(93.4d, 95.2d),
                        new Spread(3500d, 3200d, Kind.P).set_bid_ask(187.3d, 191.1d)

                ]
        )
        println volSpreadWing.toString()
        [2900, 3000, 3100, 3200, 3300, 3400, 3500].each {
            println "strike: ${it}, vol: ${volSpreadWing.get_vol(3282.0d, it)}"
        }
    }

}
