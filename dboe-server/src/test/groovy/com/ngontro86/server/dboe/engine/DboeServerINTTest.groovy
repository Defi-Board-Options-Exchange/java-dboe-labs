package com.ngontro86.server.dboe.engine

import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.market.option.Black76
import com.ngontro86.utils.GlobalTimeUtils
import org.junit.Before
import org.junit.Test

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

import static com.ngontro86.market.option.OptionKind.Call
import static com.ngontro86.market.option.OptionKind.Put
import static com.ngontro86.restful.common.client.RestClientBuilder.build

class DboeServerINTTest {

    @Before
    void init() {
        [
                'dboeHost.host'    : 'http://dboe.exchange',
                //'dboeHost.host'    : 'http://localhost',
                'dboeHost.basePath': 'api',
                'dboeHost.port'    : '8686'
        ].each { k, v -> System.setProperty(k, v) }
    }

    @Test
    void "should populate vol surface data"() {
        def restClient = build('dboeHost')
        def atmMap = [
                'ETH': 1710,
                'BNB': 275d,
                'BTC': 19650d
        ]
        def volMap = [
                'ETH': 117d,
                'BNB': 78d,
                'BTC': 73d
        ]

        def optionChain = restClient.withQueryParams('query/listInstrument', [:], Collection) as Collection<Map>
        println "Found: ${optionChain.size()} options..."
        optionChain.each { option ->
            def atm = atmMap[option['underlying']]
            def vol = volMap[option['underlying']]
            def moneyness = Math.log(option['strike'] / atm)
            [option['expiry'], 20220923].each { expiry ->
                def map = [
                        'underlying'  : option['underlying'],
                        'expiry'      : expiry,
                        'timeToExpiry': ttExpiry(expiry) * 365d,
                        'moneyness'   : moneyness,
                        'atm_price'   : atm,
                        'strike'      : option['strike'],
                        'vol'         : vol + 250d * moneyness * moneyness + 25d * ttExpiry(expiry) / 365d
                ]
                println "Posting this vol info: ${map}..."
                restClient.postWithQParamsAndHeader(
                        'coreEngine/updateEvent',
                        ['eventName': 'DboeVolSurfaceEvent'],
                        'passCode', '2511',
                        Entity.entity(map, MediaType.APPLICATION_JSON_TYPE), Void)
            }
        }

    }

    @Test
    void "should submit greeks"() {
        def restClient = build('dboeHost')
        def atmMap = [
                'ETH': 1000d,
                'BNB': 220d,
                'BTC': 20000d
        ]
        def volMap = [
                'ETH': 100d,
                'BNB': 100d,
                'BTC': 95d
        ]

        def optionChain = restClient.withQueryParams('query/listInstrument', [:], Collection) as Collection<Map>
        println "Got ${optionChain.size()} options"
        optionChain.each { option ->
            def greeks = Black76.greekDboe option: [kind: option['kind'] == 'Call' ? Call : Put, atm: atmMap[option['underlying']], strike: option['strike'], condStrike: option['cond_strike'], r: 0.0, t: ttExpiry(option['expiry']), vol: volMap[option['underlying']] / 100d]

            def map = [
                    'instr_id'    : option['instr_id'],
                    'timeToExpiry': ttExpiry(option['expiry']),
                    'vol'         : volMap[option['underlying']],
                    'atm_price'   : atmMap[option['underlying']],
                    'delta'       : greeks['delta'],
                    'vega'        : greeks['vega'],
                    'gamma'       : greeks['gamma'],
                    'theta'       : greeks['theta'],
                    'in_timestamp': System.currentTimeMillis()
            ]
            restClient.postWithQParamsAndHeader(
                    'coreEngine/updateEvent',
                    ['eventName': 'DboeOptionGreekEvent'],
                    'passCode', '2511',
                    Entity.entity(map, MediaType.APPLICATION_JSON_TYPE), Void)
        }
    }


    @Test
    void "should submit order in two steps using DBOE Test1"() {
        def restClient = build('dboeHost')
        def atmMap = [
                'ETH': 1000d,
                'BNB': 220d,
                'BTC': 40000d
        ]
        def volMap = [
                'ETH': 100d,
                'BNB': 100d,
                'BTC': 95d
        ]

        Integer orderIdx = 0

        def optionChain = restClient.withQueryParams('query/listInstrument', [:], Collection) as Collection<Map>
        println "Got ${optionChain.size()} options"

        1.times {
            sleep 1000
            optionChain.findAll { it['instr_id'] != 'E1000C722' }.each { option ->
                def px = Black76.priceDboe option: [kind: option['kind'] == 'Call' ? Call : Put, atm: atmMap[option['underlying']], strike: option['strike'], condStrike: option['cond_strike'], r: 0.0, t: ttExpiry(option['expiry']), vol: volMap[option['underlying']] / 100d]

                [0.1, 0.12, 0.15].each { spread ->
                    def spreadPct = Math.random() * spread
                    def bid = Math.max(0.01, Math.round(px * (1d - spreadPct) * 10d) / 10d)
                    def ask = Math.max(0.02, Math.round(px * (1d + spreadPct) * 10d) / 10d)

                    println "MM option: ${option['instr_id']}, bid: ${bid}, ask: ${ask}..."
                    def exBidOrderId = restClient.postWithQueryParams(
                            'coreEngine/submitOrder',
                            [
                                    'walletId'   : '0xAb2e8ABD855d35036526Dd843dC39e6D36D53C98',
                                    'instrId'    : option['instr_id'],
                                    'buySell'    : 1,
                                    'userOrderId': "Test-${++orderIdx}",
                                    'orderType'  : 'LMT',
                                    'price'      : bid,
                                    'amount'     : Math.round(1d + Math.random() * 5) / 5d
                            ], Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String) as String
                    println "Ex Bid Order Id: ${exBidOrderId}"
                    restClient.postWithQueryParams(
                            'coreEngine/confirmOrder',
                            [
                                    'walletId'   : '0xAb2e8ABD855d35036526Dd843dC39e6D36D53C98',
                                    'instrId'    : option['instr_id'],
                                    'userOrderId': "Test-${orderIdx}",
                                    'exOrderId'  : exBidOrderId,
                                    'txnHashId'  : UUID.randomUUID().toString()
                            ], Entity.entity("", MediaType.APPLICATION_JSON_TYPE), Boolean)

                    def exAskOrderId = restClient.postWithQueryParams(
                            'coreEngine/submitOrder',
                            [
                                    'walletId'   : '0xAb2e8ABD855d35036526Dd843dC39e6D36D53C98',
                                    'instrId'    : option['instr_id'],
                                    'buySell'    : 2,
                                    'userOrderId': "Test-${++orderIdx}",
                                    'orderType'  : 'LMT',
                                    'price'      : ask,
                                    'amount'     : Math.round(1d + Math.random() * 5) / 5d
                            ], Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String) as String

                    println "Ex Ask Order Id: ${exAskOrderId}"

                    restClient.postWithQueryParams(
                            'coreEngine/confirmOrder',
                            [
                                    'walletId'   : '0xAb2e8ABD855d35036526Dd843dC39e6D36D53C98',
                                    'instrId'    : option['instr_id'],
                                    'userOrderId': "Test-${orderIdx}",
                                    'exOrderId'  : exAskOrderId,
                                    'txnSig'     : UUID.randomUUID().toString()
                            ], Entity.entity("", MediaType.APPLICATION_JSON_TYPE), Boolean)

                }
            }

        }

    }

    @Test
    void "should submit a MKT order in two steps"() {
        def restClient = build('dboeHost')
        Integer orderIdx = 1000
        def optionChain = restClient.withQueryParams('query/listInstrument', [:], Collection) as Collection<Map>
        println "Got ${optionChain.size()} options"
        optionChain.each { option ->
            println "Sweep option: ${option['instr_id']}..."
            def exBidOrderId = restClient.postWithQueryParams(
                    'coreEngine/submitOrder',
                    [
                            'walletId'   : '0xAb2e8ABD855d35036526Dd843dC39e6D36D53C98',
                            'instrId'    : option['instr_id'],
                            'buySell'    : Math.random() < 0.5 ? 1 : 2,
                            'userOrderId': "Test-${++orderIdx}",
                            'orderType'  : 'MKT',
                            'amount'     : Math.round(1d + Math.random() * 5) * 50d
                    ], Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String) as String

            println "Ex Order Id: ${exBidOrderId}"

            restClient.postWithQueryParams(
                    'coreEngine/confirmOrder',
                    [
                            'walletId'   : '0xAb2e8ABD855d35036526Dd843dC39e6D36D53C98',
                            'instrId'    : option['instr_id'],
                            'userOrderId': "Test-${orderIdx}",
                            'exOrderId'  : exBidOrderId,
                            'txnHashId'  : UUID.randomUUID().toString()
                    ], Entity.entity("", MediaType.APPLICATION_JSON_TYPE), Boolean)
        }

    }


    @Test
    void "should find out time to expiry"() {
        println ttExpiry(20211230)
        Integer idx = 0
        println "${idx++}"
        println "${idx}"
        println "${++idx}"
        println "${idx}"

        def map = ['a': 1]
        println map << ['b': 2]

    }

    double ttExpiry(int date) {
        return (GlobalTimeUtils.getTimeUtc("${date}", 'yyyyMMdd') - GlobalTimeController.getCurrentTimeMillis()) / 365 / 24 / 60 / 60 / 1000
    }

}
