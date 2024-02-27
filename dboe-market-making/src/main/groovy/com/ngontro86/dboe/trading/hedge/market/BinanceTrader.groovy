package com.ngontro86.dboe.trading.hedge.market

import com.binance.connector.futures.client.impl.UMFuturesClientImpl
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.trading.utils.MarketMakingUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct

import static com.ngontro86.dboe.trading.utils.MarketMakingUtils.*
import static com.ngontro86.restful.common.json.JsonUtils.fromJson

class BinanceTrader implements HedgingTrader {

    @Logging
    private Logger logger

    private UMFuturesClientImpl futuresClient

    @ConfigValue(config = "futuresApiKey")
    private String futuresApiKey

    @ConfigValue(config = "futuresSecretKey")
    private String futuresSecretKey

    @ConfigValue(config = "liveHedging")
    private Boolean liveHedging = false

    private Set<String> symbols = []

    @ConfigValue(config = "qtyFmts")
    private Collection qtyFmts = ["SOL:0", "ETH:0.00", "BTC:0.000"]

    private Map<String, String> fmts = [:]

    @PostConstruct
    private void init() {
        futuresClient = new UMFuturesClientImpl(futuresApiKey, futuresSecretKey)
        qtyFmts.each {
            def toks = it.toString().split(":")
            fmts[toks[0]] = toks[1]
        }
    }

    @Override
    void setUnderlyings(Set<String> underlyings) {
        symbols.addAll(umSymbols(underlyings))
    }

    @Override
    void cancelOpenOrders() {
        throwExceptionIfUnsetSymbols()
        if (liveHedging) {
            symbols.each {
                futuresClient.account().cancelAllOpenOrders(['symbol': it] as LinkedHashMap)
            }
        }
    }

    @Override
    Map<String, Double> loadPositions() {
        throwExceptionIfUnsetSymbols()
        logger.info "Load position for: ${symbols}"
        println "Load position for: ${symbols}"
        def posJsonInfo = futuresClient.account().positionInformation([:] as LinkedHashMap)
        fromJson(posJsonInfo, Map[])
                .findAll { symbols.contains(it['symbol']) }
                .collectEntries {
                    [(underlying(it['symbol'])): Double.valueOf(it['positionAmt'])]
                }
    }

    @Override
    void hedgeRisk(String underlying, double absDelta) {
        if (liveHedging) {
            def qty = Math.abs(MarketMakingUtils.round(absDelta, fmts[underlying]))
            if (qty > 0) {
                def res = futuresClient.account().newOrder(
                        [
                                'symbol'  : umSymbol(underlying),
                                'side'    : absDelta > 0 ? 'SELL' : 'BUY',
                                'type'    : 'MARKET',
                                'quantity': qty
                        ] as LinkedHashMap)
                logger.info("Submitted MKT order: ${res}")
            }
        }
    }

    private void throwExceptionIfUnsetSymbols() {
        if (symbols.isEmpty()) {
            throw new IllegalStateException("Unset symbols")
        }
    }
}
