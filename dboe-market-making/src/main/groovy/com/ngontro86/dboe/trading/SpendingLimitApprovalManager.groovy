package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.dboe.web3j.Web3jManager
import com.ngontro86.dboe.web3j.token.TokenLoader
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Lazy(false)
@DBOEComponent
class SpendingLimitApprovalManager<T> {

    @Logging
    private Logger logger

    @Inject
    private Web3jManager web3jManager

    @Inject
    private TokenLoader<T> tokenLoader

    @ConfigValue(config = "defaultSpendingLimit")
    private Integer defaultSpendingLimit = 100000

    @ConfigValue(config = "minSpendingLimit")
    private Integer minSpendingLimit = 10000

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    @Inject
    private ExchangeSpecsLoader dexSpecsLoader

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)

    @PostConstruct
    private void init() {
        approveSLUpfront()

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                approveSLUpfront()
            }
        }, 5, 5, TimeUnit.MINUTES)
    }

    private void approveSLUpfront() {
        logger.info "Approve Currency Spending Limit first!"
        def options = dexSpecsLoader.loadOptions(chain)
        approveClearingHouseSLIfNeeded(options)
        sleep 1000
        logger.info "Approve Options Spending Limit"
        approveCurrencySLIfNeeded(options)
    }

    private void approveClearingHouseSLIfNeeded(Collection<Map> options) {
        logger.info("Got: ${options.size()} to approve spending limit!")
        options.each { option ->
            approveClearinghouse(tokenLoader.load(option['long_contract_address']), defaultSpendingLimit, option['clearing_address'])
            approveClearinghouse(tokenLoader.load(option['short_contract_address']), defaultSpendingLimit, option['clearing_address'])
        }
    }

    private void approveCurrencySLIfNeeded(Collection<Map> options) {
        def currClearingHouseMap = options.collect {
            new CurrencyClearingHouseAddress(currencyAddr: it['currency_address'], clearingHouseAddr: it['clearing_address'])
        } as HashSet

        logger.info("Found: ${currClearingHouseMap.size()} unique currency and clearing house pair")
        currClearingHouseMap.each {
            approveClearinghouse(tokenLoader.load(it.currencyAddr), defaultSpendingLimit, it.clearingHouseAddr)
        }
    }

    private void approveClearinghouse(T token, int spendingLimit, String clearingHouseAddress) {
        def decimal = tokenLoader.decimals(token)
        if (tokenLoader.allowance(token, web3jManager.getWallet(), clearingHouseAddress) <= Math.pow(10, decimal) * minSpendingLimit) {
            tokenLoader.approve(token, clearingHouseAddress, Math.pow(10, decimal) * spendingLimit as BigInteger)
        }
    }

    private class CurrencyClearingHouseAddress {
        String currencyAddr
        String clearingHouseAddr

        @Override
        boolean equals(Object obj) {
            if (!(obj instanceof CurrencyClearingHouseAddress)) {
                return false
            }
            def other = (CurrencyClearingHouseAddress) obj
            return currencyAddr == other.currencyAddr && clearingHouseAddr == other.clearingHouseAddr
        }

        @Override
        int hashCode() {
            return Objects.hash(currencyAddr, clearingHouseAddr)
        }
    }
}
