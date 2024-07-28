package com.ngontro86.analytic.server.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@DBOEComponent
class PointClaimService {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    private Collection<Map> events

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    void run() {
                        update()
                    }
                }, 0, 6, TimeUnit.HOURS
        )
    }

    private void update() {
        events = flatDao.queryList("select * from dboe_point_claim_addresses")
        println("Found: ${events.size()} events")
    }

    Collection getEvents(String chain) {
        return events.findAll { it['chain'] == chain }
    }
}
