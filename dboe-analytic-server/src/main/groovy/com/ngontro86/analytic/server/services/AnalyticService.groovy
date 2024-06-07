package com.ngontro86.analytic.server.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.utils.ResourcesUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@DBOEComponent
class AnalyticService {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue
    private Integer reloadFreqMin = 30

    private String spotFirstTradeQ = ResourcesUtils.content("queries/spot-first-trades.sql")
    private String optionFirstTradeQ = ResourcesUtils.content("queries/option-first-trades.sql")
    private String invitesQ = ResourcesUtils.content("queries/no-invites.sql")

    private Map<String, Boolean> optionTrades = [:]
    private Map<String, Boolean> spotTrades = [:]
    private Map<String, Integer> invites = [:]

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    void run() {
                        update()
                    }
                }, 0, reloadFreqMin, TimeUnit.MINUTES
        )
    }

    private void update() {
        try {
            def opTradeWallets = flatDao.queryStringList(optionFirstTradeQ)
            println("Found: ${opTradeWallets.size()} wallets traded Options")
            optionTrades = opTradeWallets.collectEntries { [(it): true] }

            def spotTradeWallets = flatDao.queryStringList(spotFirstTradeQ)
            println("Found: ${spotTradeWallets.size()} wallets traded Spot")
            spotTrades = spotTradeWallets.collectEntries { [(it): true] }

            def noOfInvites = flatDao.queryList(invitesQ)
            println("Found: ${noOfInvites.size()} invitation records")
            invites = noOfInvites.collectEntries { [(it['Address']): it['numOfInvitations']] }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    Map firstOptionTrade(String address) {
        return ['data': ['result': optionTrades.getOrDefault(address.toLowerCase(), false)]]
    }

    Map firstSpotTrade(String address) {
        return ['data': ['result': spotTrades.getOrDefault(address.toLowerCase(), false)]]
    }

    Map invited(String address, int noOfInvites) {
        return ['data': ['result': invites.getOrDefault(address.toLowerCase(), 0) >= noOfInvites]]
    }
}
