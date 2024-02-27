package com.ngontro86.dboe.trading

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.dboe.web3j.annotations.OtherTokenLoader
import com.ngontro86.dboe.web3j.token.TokenLoader
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static java.util.concurrent.TimeUnit.MINUTES

@Lazy(false)
@DBOEComponent
class SpotSpendingLimitApprovalManager<T> {

    @Logging
    private Logger logger

    @Inject
    @OtherTokenLoader
    private TokenLoader<T> tokenLoader

    @ConfigValue(config = "defaultSpendingLimit")
    private Integer defaultSpendingLimit = 100000

    @ConfigValue(config = "minSpendingLimit")
    private Integer minSpendingLimit = 10000

    @ConfigValue(config = "chain")
    private String chain = 'AVAX'

    @Inject
    private ExchangeSpecsLoader dexSpecsLoader

    @ConfigValue(config = "spotEnabled")
    private Boolean spotEnabled = true

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)

    @PostConstruct
    private void init() {
        if (spotEnabled) {
            approveSpotSLUpfront()
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                void run() {
                    approveSpotSLUpfront()
                }
            }, 5, 30, MINUTES)
        }
    }

    private void approveSpotSLUpfront() {
        println "Approving Spending Limit for Spots..."
        dexSpecsLoader.loadSpotPairs(chain).each {
            approveOneSpotPair(it)
        }
    }

    private void approveOneSpotPair(it) {
        try {
            approveSLIfNeeded(tokenLoader.load(it['base_token']), defaultSpendingLimit, it['address'])
            approveSLIfNeeded(tokenLoader.load(it['quote_token']), defaultSpendingLimit, it['address'])
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private void approveSLIfNeeded(T token, int spendingLimit, String spender) {
        def decimal = tokenLoader.decimals(token)
        if (tokenLoader.allowance(token, tokenLoader.getOwnerAddress(), spender) <= Math.pow(10, decimal) * minSpendingLimit) {
            tokenLoader.approve(token, spender, Math.pow(10, decimal) * spendingLimit as BigInteger)
        }
    }
}
