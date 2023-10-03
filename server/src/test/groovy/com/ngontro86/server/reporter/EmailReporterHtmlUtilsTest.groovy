package com.ngontro86.server.reporter

import org.junit.Test

import static EmailReporterHtmlUtils.pnlHtml
import static com.ngontro86.server.reporter.EmailReporterHtmlUtils.positionHtml


class EmailReporterHtmlUtilsTest {

    @Test
    void "should generate pl html format 1"() {
        println pnlHtml(
                [
                        [
                                'val'        : 100,
                                'slice'      : 'trading',
                                'micro_price': 27015.0,
                                'broker'     : 'IB',
                                'account'    : 'acc1',
                                'portfolio'  : 'Global HS',
                                'inst_id'    : 'HSIN17'
                        ],
                        [
                                'val'        : -50,
                                'micro_price': 26915.0,
                                'slice'      : 'trading',
                                'broker'     : 'IB',
                                'account'    : 'acc1',
                                'portfolio'  : 'Global HS',
                                'inst_id'    : 'HHI.HKN17'
                        ]
                ]
        )
    }

    @Test
    void "should generate position html format 1"() {
        println positionHtml(
                [
                        [
                                'size'     : 1,
                                'abs_size' : 10,
                                'broker'   : 'IB',
                                'account'  : 'acc1',
                                'portfolio': 'Global HS',
                                'inst_id'  : 'HSIN17'
                        ],
                        [
                                'size'     : -2,
                                'abs_size' : 25,
                                'broker'   : 'IB',
                                'account'  : 'acc1',
                                'portfolio': 'Global HS',
                                'inst_id'  : 'HHI.HKN17'
                        ]
                ]
        )
    }

}
