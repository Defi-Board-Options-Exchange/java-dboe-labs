package com.ngontro86.restful.common.json.INT

import com.ngontro86.utils.GlobalTimeUtils
import com.ngontro86.utils.ResourcesUtils
import org.junit.Before
import org.junit.Test

import static com.ngontro86.restful.common.client.RestClientBuilder.build
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static com.ngontro86.utils.GlobalTimeUtils.getTimeUtc

class INTCryptoCompareTest {

    String apiKey

    @Before
    void init() {
        apiKey = ResourcesUtils.content("keys/cryptocompare")
        [
                'cryptoCompare.host'    : 'https://min-api.cryptocompare.com',
                'cryptoCompare.basePath': 'data',
                'cryptoCompare.port'    : '80'
        ].each { k, v -> System.setProperty(k, v) }
    }

    @Test
    void "pull all BTC and ETH hourly volume by exchange for past three months"() {

        def exchanges = exchangeInfo().findAll {
            it.CentralizationType == "Centralized" && it.btc24Vol >= 4000d
        }.collectEntries { [(it.Name): it] } as Map<String, ExchangeInfo>

        def output = new File("C:\\Users\\truon\\hourlyExVolume.csv")
        def restClient = build('cryptoCompare')
        def startingTime = getTimeUtc("20220301", "yyyyMMdd")
        def sb = new StringBuilder()
        sb.append("TimeUtc,Hour,Crypto,Exchange,Country,Grade,GradePoints,Btc24HVolume,VolumeFrom,VolumeTo,TotalVolume\n")
        exchanges.keySet().each { ex ->
            def timesFound = [:] as Map<String, Set>
            ['BTC', 'ETH'].each { crypto ->
                def params = [
                        'fsym'   : crypto,
                        'tsym'   : 'USD',
                        'e'      : ex,
                        'api_key': apiKey,
                        'limit'  : 2000
                ]
                timesFound << [(crypto): [] as HashSet]
                def pivotTime = 0L
                def dupe = false
                while (!dupe && (pivotTime == 0 || pivotTime > startingTime)) {
                    if (pivotTime != 0) {
                        params << ['toTs': pivotTime]
                    }
                    def res = restClient.withQueryParams('exchange/symbol/histohour', params, HistoVolumeExchangeRes) as HistoVolumeExchangeRes
                    res.Data.each {
                        if (!timesFound[crypto].contains(it.time)) {
                            sb.append("${it.time},${getTimeFormat(it.time * 1000, "yyyyMMddHH")},$crypto,${ex},${exchanges[ex].Grade},${exchanges[ex].GradePoints},${exchanges[ex].btc24Vol},${it.volumefrom},${it.volumeto},${it.volumetotal}").append("\n")
                            timesFound[crypto].add(it.time)
                        } else {
                            dupe = true
                            println "Start seeing dupe records..."
                        }
                    }
                    pivotTime = res.Data.collect { it.time }.min()

                    if (Math.random() < 0.1) {
                        output << sb.toString()
                        sb.setLength(0)
                        println "Continue pulling ${crypto}, pivot time: ${pivotTime}..."
                    }
                }
            }
        }
        output << sb.toString()
    }

    @Test
    void "pull all BTC and ETH daily volume for past 24 months"() {
        def restClient = build('cryptoCompare')

        def data = new HashMap<String, Map<Integer, Map<Integer, Double>>>()
        ['BTC', 'ETH'].each { crypto ->
            data.putIfAbsent(crypto, [:])
            def params = [
                    'fsym'   : crypto,
                    'tsym'   : 'USD',
                    'api_key': apiKey,
                    'limit'  : 2000
            ]
            def res = restClient.withQueryParams('symbol/histoday', params, HistoAggregateVolRes) as HistoAggregateVolRes
            res.Data.each {
                int date = GlobalTimeUtils.getTimeFormat(it.time * 1000, "yyyyMMdd")
                int month = GlobalTimeUtils.getTimeFormat(it.time * 1000, "yyyyMM")
                data[crypto].putIfAbsent(month, [:])
                data[crypto].get(month).put(date, it.total_volume_total)
            }
        }
        data.each { crypto, monthlyData ->
            monthlyData.each { month, dat ->
                println "${crypto}, ${month}, ${median(dat.values()) / 1000000d}"
            }
        }
    }

    def median(data) {
        def copy = data.toSorted()
        def middle = data.size().intdiv(2)
        data.size() % 2 ? copy[middle] : (copy[middle - 1] + copy[middle]) / 2
    }

    Collection<ExchangeInfo> exchangeInfo() {
        def restClient = build('cryptoCompare')
        def res = restClient.withQueryParams('exchanges/general', ['api_key': apiKey], ExchangeInfoRes) as ExchangeInfoRes
        return res.Data.values()
    }

    @Test
    void "pull exchanges with descending 24 volume"() {
        def res = exchangeInfo()
        println "Name,Country,CentralisedType,Grade,GradePoints,Recommended,OrderBook,Trades,Btc24HVolume"
        res.each {
            println "${it.Name},${it.Country},${it.CentralizationType},${it.Grade},${it.GradePoints},${it.Recommended},${it.OrderBook},${it.Trades},${it.btc24Vol}"
        }
    }

    @Test
    void "pull all BTC and ETH hourly volume across exchanges for past two years"() {
        def output = new File("C:\\Users\\truon\\hourlyVolume.csv")
        def restClient = build('cryptoCompare')
        def startingTime = getTimeUtc("20220301", "yyyyMMdd")

        println "Pulling data from now till ${startingTime}..."

        def timesFound = [:] as Map<String, Set>

        def sb = new StringBuilder()
        sb.append("TimeUtc,Hour,Crypto,TopTierVolumeQuote,TopTierVolumeBase,TopTierTotalVolume,CCAGGVolumeQuote,CCAGGVolumeBase,CCAGTotalVolume,TotalVolumeQuote,TotalVolumeBase,TotalVolume\n")
        ['BTC', 'ETH'].each { crypto ->
            def params = [
                    'fsym'   : crypto,
                    'tsym'   : 'USD',
                    'api_key': apiKey,
                    'limit'  : 2000
            ]
            timesFound << [(crypto): [] as HashSet]
            def pivotTime = 0L
            def dupe = false
            while (!dupe && (pivotTime == 0 || pivotTime > startingTime)) {
                if (pivotTime != 0) {
                    params << ['toTs': pivotTime]
                }
                def res = restClient.withQueryParams('symbol/histohour', params, HistoAggregateVolRes) as HistoAggregateVolRes
                res.Data.each {
                    if (!timesFound[crypto].contains(it.time)) {
                        sb.append("${it.time},${getTimeFormat(it.time * 1000, "yyyyMMddHH")},$crypto,${it.top_tier_volume_base},${it.top_tier_volume_quote},${it.top_tier_volume_total},${it.cccagg_volume_quote},${it.cccagg_volume_base},${it.cccagg_volume_total},${it.total_volume_quote},${it.total_volume_base},${it.total_volume_total}").append("\n")
                        timesFound[crypto].add(it.time)
                    } else {
                        dupe = true
                        println "Start seeing dupe records..."
                    }
                }
                pivotTime = res.Data.collect { it.time }.min()

                if (Math.random() < 0.1) {
                    output << sb.toString()
                    sb.setLength(0)
                    println "Continue pulling ${crypto}, pivot time: ${pivotTime}..."
                }
            }
        }
        output << sb.toString()
    }

    @Test
    void "pull all BTC and ETH daily OHLCV across exchanges for past two years"() {
        def output = new File("${System.getProperty("user.home")}\\OneDrive\\Desktop\\dailyOHLCV.csv")

        output << "TimeUtc,yyyyMMdd,fSym,toSym,High,Low,Open,Close,VolumeFrom,VolumeTo\n"

        def restClient = build('cryptoCompare')
        def startingTime = getTimeUtc("20220201", "yyyyMMdd")

        println "Pulling data from now till ${startingTime}..."

        def sb = new StringBuilder()
        ['BTC', 'ETH'].each { crypto ->
            ['USD', 'USDT'].each { tSym ->
                def timesFound = [:] as Map<String, Set>
                timesFound << [(crypto): [] as HashSet]

                def params = [
                        'fsym'   : crypto,
                        'tsym'   : tSym,
                        'api_key': apiKey,
                        'limit'  : 2000
                ]

                def pivotTime = 0L
                def dupe = false
                while (!dupe && (pivotTime == 0 || pivotTime > startingTime)) {
                    if (pivotTime != 0) {
                        params << ['toTs': pivotTime]
                    }
                    def res = restClient.withQueryParams('v2/histoday', params, HistoOHLCVRes) as HistoOHLCVRes
                    res.Data.Data.each {
                        if (!timesFound[crypto].contains(it.time)) {
                            sb.append("${it.time},${getTimeFormat(it.time * 1000, "yyyyMMddHH")},$crypto,${tSym},${it.high},${it.low},${it.open},${it.close},${it.volumefrom},${it.volumeto}").append("\n")
                            timesFound[crypto].add(it.time)
                        } else {
                            dupe = true
                            println "Start seeing dupe records..."
                        }
                    }
                    pivotTime = res.Data.Data.collect { it.time }.min()

                    if (Math.random() < 0.1) {
                        output << sb.toString()
                        sb.setLength(0)
                        println "Continue pulling ${crypto}, pivot time: ${pivotTime}..."
                    }
                }
            }
        }
        output << sb.toString()
    }

    static class HistoOHLCVRes {
        public String Response
        public String Message
        public int Type
        public HistoOHLCVData Data
        public Collection RateLimit
        public boolean HasWarning
    }

    static class HistoOHLCVData {
        public boolean Aggregated
        public long TimeFrom
        public long TimeTo
        public Collection<HistoOHLCV> Data = []
    }

    static class HistoOHLCV {
        public long time
        public double high
        public double low
        public double open
        public double close
        public double volumefrom
        public double volumeto
    }


    static class HistoAggregateVolRes {
        public String Message
        public int Type
        public Collection<HistoAggregateVol> Data = []
        public long TimeFrom
        public long TimeTo
        public boolean FirstValueInArray
        public String ConversionType
        public Collection RateLimit
        public boolean HasWarning
    }

    static class HistoAggregateVol {
        public long time
        public double top_tier_volume_quote
        public double top_tier_volume_base
        public double top_tier_volume_total
        public double cccagg_volume_quote
        public double cccagg_volume_base
        public double cccagg_volume_total
        public double total_volume_quote
        public double total_volume_base
        public double total_volume_total
    }


    static class ExchangeInfo {
        public String Id
        public String Name
        public String CentralizationType
        public String Country
        public String Grade
        public double GradePoints
        public boolean OrderBook
        public boolean Trades
        public boolean Recommended
        public Map TOTALVOLUME24H

        public double getBtc24Vol() {
            TOTALVOLUME24H['BTC']
        }

        public void setBtc24Vol(double vol) {
            TOTALVOLUME24H['BTC'] = vol
        }
    }

    static class ExchangeInfoRes {
        public String Message
        public int Type
        public Collection RateLimit
        public boolean HasWarning
        public Map<String, ExchangeInfo> Data = [:]
    }

    static class HistoVolumeExchangeRes {
        public String Message
        public int Type
        public Collection<HistoExAggregateVol> Data = []
        public long TimeFrom
        public long TimeTo
        public boolean FirstValueInArray
        public String ConversionType
        public Collection RateLimit
        public boolean HasWarning
    }

    static class HistoExAggregateVol {
        public long time
        public double volumeto
        public double volumefrom
        public double volumetotal
    }
}
