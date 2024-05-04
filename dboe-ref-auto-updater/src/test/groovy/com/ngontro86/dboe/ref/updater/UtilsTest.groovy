package com.ngontro86.dboe.ref.updater


import org.junit.Test

import static com.ngontro86.dboe.ref.updater.Utils.listStrikes
import static com.ngontro86.utils.EqualUtils.equals

class UtilsTest {

    @Test
    void "should return epoch time given date and time LTT"() {
        assert Utils.getTimeUtc(20240503, 80000) == 1714723200000
    }

    @Test
    void "should return a good list of strikes"() {
        assert equals(listStrikes(true, 1592.5, 10.0, 2, 5, 100), [157000, 158000, 159000, 160000, 161000, 162000, 163000, 164000])
        assert equals(listStrikes(false, 1592.5, 10.0, 2, 5, 100), [155000, 156000, 157000, 158000, 159000, 160000, 161000, 162000])

        assert equals(listStrikes(true, 26900.5, 500.0, 2, 5, 100), [2550000, 2600000, 2650000, 2700000, 2750000, 2800000, 2850000, 2900000])
        assert equals(listStrikes(false, 26900.5, 500.0, 2, 5, 100), [2450000, 2500000, 2550000, 2600000, 2650000, 2700000, 2750000, 2800000])
    }

    @Test
    void "should work out instr ids"() {
        assert Utils.instrId('E', '0', true, 2210.05, 20230926) == 'E2210C926'
        assert Utils.instrId('BN', '0', true, 310.2, 20230926) == 'BN310C926'
        assert Utils.instrId('S', '0.0', true, 102.5, 20230926) == 'S102.5C926'
        assert Utils.instrId('X', '0.00', true, 0.6405, 20230926) == 'X0.64C926'
        assert Utils.instrId('L', '0.0', true, 15.5, 20230926) == 'L15.5C926'
        assert Utils.instrId('D', '0.000', true, 0.065, 20230926) == 'D0.065C926'
    }

    @Test
    void "return atm call and put"() {
        assert Utils.atmCallPut(3710d,
                [
                        [
                                'kind'  : 'Call',
                                'strike': 3600d
                        ],
                        [
                                'kind'  : 'Call',
                                'strike': 3700d
                        ],
                        [
                                'kind'  : 'Call',
                                'strike': 3800d
                        ],
                        [
                                'kind'  : 'Put',
                                'strike': 3700d
                        ],
                        [
                                'kind'  : 'Put',
                                'strike': 3600d
                        ],
                        [
                                'kind'  : 'Put',
                                'strike': 3500d
                        ]
                ]).equals(
                [
                        [
                                'kind'  : 'Call',
                                'strike': 3800d
                        ],
                        [
                                'kind'  : 'Put',
                                'strike': 3700d
                        ]
                ]
        )
    }
}
