package com.ngontro86.market.volatility.downloader

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.market.time.TimeSource
import com.ngontro86.restful.common.json.JsonUtils
import org.apache.logging.log4j.Logger

import javax.inject.Inject

import static com.ngontro86.dboe.web3j.Utils.timeDiffInYear

class DeribitVolDownloader implements VolDownloader {

    @Logging
    private Logger logger

    @Inject
    private TimeSource timeSource

    @ConfigValue(config = "cutOffTimeToExpiry")
    private Double cutOffTimeToExpiry = 21.0 / 365.0

    @Override
    Map<Long, Map<Double, Double>> loadVols(String underlying) {
        def currentTime = timeSource.currentTimeMilliSec()
        def options = loadActiveOptions(underlying).findAll {
            timeDiffInYear(it.expiration_timestamp, currentTime) <= cutOffTimeToExpiry
        }
        logger.info("Found: ${options.size()} ${underlying} Options on Deribit")
        println "Found: ${options.size()}  ${underlying} Options on Deribit"

        Map<Long, Map<Double, Double>> surfaceMap = [:]
        options.each { option ->
            def optionPx = loadOptionPrice(option.instrument_name)
            surfaceMap.putIfAbsent(option.expiration_timestamp, [:])
            boolean otm = (option.option_type == 'call' && option.strike >= optionPx.underlying_price) ||
                    (option.option_type == 'put' && option.strike <= optionPx.underlying_price)
            if (otm) {
                double moneyness = Math.log(option.strike / optionPx.underlying_price)
                surfaceMap.get(option.expiration_timestamp).putIfAbsent(moneyness, optionPx.mark_iv)
            }
        }

        return surfaceMap
    }

    private Collection<DeribitOption> loadActiveOptions(String underlying) {
        JsonUtils.fromJson(queryDeribit("https://www.deribit.com/api/v2/public/get_instruments?currency=${underlying}&kind=option&expired=false"), DeribitOptionRes).result as Collection<DeribitOption>
    }

    private DeribitOptionPrice loadOptionPrice(String instrName) {
        JsonUtils.fromJson(queryDeribit("https://www.deribit.com/api/v2/public/ticker?instrument_name=${instrName}"), DeribitOptionPriceRes).result
    }

    private String queryDeribit(String url) {
        def is = new URL(url).openConnection().getInputStream()
        def json = new StringBuilder()
        new BufferedReader(new InputStreamReader(is)).readLines().each { json.append(it) }
        return json.toString()
    }

    private static class DeribitOptionRes {
        String jsonrpc
        DeribitOption[] result
        long usIn
        long usOut
        long usDiff
        boolean testnet
    }

    private static class DeribitOptionPriceRes {
        String jsonrpc
        DeribitOptionPrice result
        long usIn
        long usOut
        long usDiff
        boolean testnet
    }

    private static class DeribitOption {
        double strike
        String option_type
        String instrument_name
        String settlement_period
        long expiration_timestamp

        @Override
        String toString() {
            "${instrument_name}:${option_type},${strike},${settlement_period},${expiration_timestamp}"
        }
    }

    private static class DeribitOptionPrice {
        String instrument_name
        double underlying_price
        double open_interest
        long timestamp
        double mark_iv
        double bid_iv
        double ask_iv
        Stats stats
        Greek greeks

        @Override
        String toString() {
            "name:,${instrument_name},underlying px:,${underlying_price},oi:,${open_interest},stats:,${stats},greek:,${greeks},bid_iv:,${bid_iv},mark_iv:,${mark_iv},ask_iv:,${ask_iv},timestamp:,${timestamp}"
        }
    }

    private static class Stats {
        double volume_usd
        double volume
        double low
        double high

        @Override
        String toString() {
            "Stats:[${volume}:${volume_usd}:${low}:${high}]"
        }
    }

    private static class Greek {
        double vega
        double theta
        double rho
        double gamma
        double delta

        @Override
        String toString() {
            "Greek:[${delta}:${vega}:${gamma}:${theta}:${rho}]"
        }
    }
}
