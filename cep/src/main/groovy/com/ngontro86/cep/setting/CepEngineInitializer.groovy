package com.ngontro86.cep.setting

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.serials.ObjMap
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

@Lazy(false)
@DBOEComponent
class CepEngineInitializer {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    @Inject
    private StaticTableLoader loader

    @ConfigValue(config = "cepInstanceId")
    private String esperId

    @ConfigValue(config = "reloadStaticTableSec")
    private Integer reloadStaticTableSec = 15

    @ConfigValue(config = "slowReloadStaticTableMin")
    private Integer slowReloadStaticTableMin = 15

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)

    @PostConstruct
    void loadStaticTables() {
        loader.load("esper", esperId, "1.0").each { table, listOfEntries ->
            logger.info("Loading table: ${table} with ${listOfEntries.size()} records...")
            publish(table, listOfEntries)
        }

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                try {
                    loader.reload("esper", esperId, "1.0").each { table, listOfEntries ->
                        logger.info("Reloading table: ${table} with ${listOfEntries.size()} records...")
                        publish(table, listOfEntries)
                    }
                } catch (Exception e) {
                    logger.error("Reloading exception: ", e)
                }
            }
        }, 5, reloadStaticTableSec, SECONDS)

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            void run() {
                try {
                    loader.slowReload("esper", esperId, "1.0").each { table, listOfEntries ->
                        logger.info("Slowreloading table: ${table} with ${listOfEntries.size()} records...")
                        publish(table, listOfEntries)
                    }
                } catch (Exception e) {
                    logger.error("Slowreloading exception: ", e)
                }
            }
        }, 2, slowReloadStaticTableMin, MINUTES)
    }

    private void publish(String table, Collection<Map> events) {
        events.each { map ->
            if (!map.containsKey('active') || map.get('active') == 1) {
                cep.accept(new ObjMap("${table}_event".toString(), map))
            }
        }
    }
}
