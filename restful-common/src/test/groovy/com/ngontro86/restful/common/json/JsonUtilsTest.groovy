package com.ngontro86.restful.common.json


import org.junit.Test

import static JsonUtils.toJson


class JsonUtilsTest {

    @Test
    void "should convert Map to json"() {
        println toJson(
                [
                        'fxRate'                  : [
                                'SGD': 1.35,
                                'JPY': 101.0,
                                'EUR': 0.82,
                                'CAD': 1.35,
                                'GBP': 0.67
                        ],
                        'simulation'              : true,
                        'PnL Email'               : 'truongvinh.vu@gmail.com',
                        'PnL Freq sec'            : 300,
                        'rolling future ahead LTD': 3
                ]
        )
    }

    @Test
    void "should convert Map to json 2"() {
        println toJson(
                [
                        'FX'         : ['EUR/USD': true, 'JPY/USD': false, 'CAD/USD': true],
                        'Securities' : ['VNM': true, 'FB': false, 'GOOG': true, 'BABA': false],
                        'Derivatives': ['SIMSCI Index': true, 'HSI Index': true, 'HSCEI Index': false, 'NKY Index': false]
                ]
        )
    }

    @Test
    void "should convert Map to json 5"() {
        println toJson(
                [
                        [
                                'ref_signal'  : 'Pair MAVG',
                                'inst_id'     : 'HI1 Index',
                                'order_req_id': 'Pair_HI1_MAVG_1',
                                'ref_price'   : 0.5d,
                                'timestamp'   : 2018060514302500
                        ],
                        [
                                'ref_signal'  : 'Pair MAVG',
                                'inst_id'     : 'QZ1 Index',
                                'order_req_id': 'Pair_QZ1_MAVG_1',
                                'ref_price'   : 0.25d,
                                'timestamp'   : 2018060514302500
                        ],
                        [
                                'ref_signal'  : 'Pair MAVG',
                                'inst_id'     : 'VIX1 Index',
                                'order_req_id': 'Pair_VIX1_MAVG_1',
                                'ref_price'   : 0.75d,
                                'timestamp'   : 2018060514302500
                        ],
                ]
        )
    }

    @Test
    void "should convert Map to json 3"() {
        println toJson(
                [
                        [
                                'date'    : 20180501,
                                'strategy': 'EU-US Vol',
                                'pnl'     : 107d
                        ],
                        [
                                'date'    : 20180502,
                                'strategy': 'EU-US Vol',
                                'pnl'     : -52d
                        ],
                        [
                                'date'    : 20180503,
                                'strategy': 'EU-US Vol',
                                'pnl'     : 120d
                        ],
                        [
                                'date'    : 20180504,
                                'strategy': 'EU-US Vol',
                                'pnl'     : -75d
                        ],
                        [
                                'date'    : 20180505,
                                'strategy': 'EU-US Vol',
                                'pnl'     : 450d
                        ],
                        [
                                'date'    : 20180515,
                                'strategy': 'EU-US Vol',
                                'pnl'     : 650d
                        ],
                        [
                                'date'    : 20180614,
                                'strategy': 'EU-US Vol',
                                'pnl'     : 450d
                        ],
                        [
                                'date'    : 20180501,
                                'strategy': 'CME Pair',
                                'pnl'     : -87d
                        ],
                        [
                                'date'    : 20180502,
                                'strategy': 'CME Pair',
                                'pnl'     : 150d
                        ],
                        [
                                'date'    : 20180503,
                                'strategy': 'CME Pair',
                                'pnl'     : 120d
                        ],
                        [
                                'date'    : 20180504,
                                'strategy': 'CME Pair',
                                'pnl'     : 180d
                        ],
                        [
                                'date'    : 20180505,
                                'strategy': 'CME Pair',
                                'pnl'     : 450d
                        ],
                        [
                                'date'    : 20180525,
                                'strategy': 'CME Pair',
                                'pnl'     : -100d
                        ],
                        [
                                'date'    : 20180605,
                                'strategy': 'CME Pair',
                                'pnl'     : 450d
                        ]
                ]
        )
    }


    @Test
    void "should be able to parse back from Position Json String into an array of Map"() {
        def listOfMap = JsonUtils.fromJson('[{"symbol":"BTCUSDT","positionAmt":"0.000","entryPrice":"0.0","markPrice":"0.00000000","unRealizedProfit":"0.00000000","liquidationPrice":"0","leverage":"20","maxNotionalValue":"15000000","marginType":"cross","isolatedMargin":"0.00000000","isAutoAddMargin":"false","positionSide":"BOTH","notional":"0","isolatedWallet":"0","updateTime":0}]', Map[])
        listOfMap.each { map ->
            map.each { k, v -> println "${k} - ${v}" }
        }
    }
}
