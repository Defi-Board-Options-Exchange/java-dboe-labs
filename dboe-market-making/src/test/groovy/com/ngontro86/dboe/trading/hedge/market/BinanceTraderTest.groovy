package com.ngontro86.dboe.trading.hedge.market

import com.binance.connector.futures.client.impl.UMFuturesClientImpl
import com.ngontro86.restful.common.json.JsonUtils
import org.junit.Ignore
import org.junit.Test

@Ignore
class BinanceTraderTest {

    @Test
    void "should be able to pull user data"() {
        def futuresClient = new UMFuturesClientImpl('',
                '')
        def mkt = futuresClient.market()
        println mkt.exchangeInfo()

        def acc = futuresClient.account()
        def res = acc.positionInformation([
                'symbol': 'BTCUSDT'
        ] as LinkedHashMap)
        println res

        def marks = futuresClient.market().markPrice([
                'symbol': 'BTCUSDT'
        ] as LinkedHashMap)
        def markMap = JsonUtils.fromJson(marks, Map)
        println "${markMap['symbol']}: ${Double.valueOf(markMap['indexPrice'])}"

        int cnt = 0
        while (cnt < 100) {
            sleep 100
            cnt++
        }
    }

}
