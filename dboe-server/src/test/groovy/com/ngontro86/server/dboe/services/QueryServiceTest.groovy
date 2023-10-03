package com.ngontro86.server.dboe.services

import org.junit.Test

class QueryServiceTest {


    @Test
    void "should work out risk profile"() {
        def greeks = [
                [
                        "instr_id"    : "C1",
                        "timeToExpiry": 0.2931506849315,
                        "vol"         : 134.46683608040206,
                        "atm_price"   : 4000,
                        "delta"       : 0.35,
                        "vega"        : 0.25,
                        "gamma"       : 0.5,
                        "theta"       : 0.5
                ],
                [
                        "instr_id"    : "P1",
                        "timeToExpiry": 0.2931506849315,
                        "vol"         : 134.46683608040206,
                        "atm_price"   : 4000,
                        "delta"       : -0.65,
                        "vega"        : 0.25,
                        "gamma"       : 0.5,
                        "theta"       : 0.5
                ]
        ]
        println QueryService.exposure(greeks, ['C1': 5.0, 'P1': -3.2])
    }
}
