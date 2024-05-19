package com.ngontro86.server.dboe.services


import org.junit.Test

import static com.ngontro86.server.dboe.services.Utils.dboeOptionTimeToExpiry
import static java.lang.Math.abs

class UtilsTest {

    @Test
    void "should return correct time to expiry for DBOE Option"() {
        assert abs(dboeOptionTimeToExpiry(20220821, 1660452161251) - 0.02) < 1.0 / 365.0
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

    @Test
    void "should replace params"() {
        def baseString = "##emailConfirmLink##?&email=##values.email##&walletAddress=##metamaskAccount##&isMobile=##isMobile##"
        println Utils.body(baseString, '##',
                [
                        'emailConfirmLink': 'https://dboe.exchange/invite',
                        'values.email'    : 'investorcrypto2022@gmail.com',
                        'isMobile'        : true,
                        'metamaskAccount' : '0xf92c4bb10394612ee38440e9b6711d29e76d9a4d'
                ])
    }

    @Test
    void "test the endWith function"() {
        assert "0x4acd5Cc057c1b8c771E2E3cD3e30780Ca257dEC0".toLowerCase().endsWith('ca257dec0')

        assert [
                '0x4acd5Cc057c1b8c771E2E3cD3e30780Ca257dEC0' : 'a@b.com',
                '0x4acd5Cc057c1b8c771E2E3cD3e30780Ca257dEC01': 'x@z.com'

        ].findAll { it.key.toString().toLowerCase().endsWith('ca257dec0') }
                .values().first() == 'a@b.com'
    }

    @Test
    void "find the min max from the list of object"() {
        println Utils.minMax(
                [
                        [
                                'strike': 12
                        ],
                        [
                                'strike': 13
                        ],
                        [
                                'strike': 14
                        ],
                        [
                                'strike': 15
                        ]
                ], 'strike', true
        )
    }

}
