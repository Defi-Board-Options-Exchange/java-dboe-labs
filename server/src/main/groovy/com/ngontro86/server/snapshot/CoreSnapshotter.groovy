package com.ngontro86.server.snapshot

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.Handler
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.utils.GlobalTimeUtils
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.*

import static com.ngontro86.utils.ResourcesUtils.lines

@Lazy(false)
@DBOEComponent
class CoreSnapshotter {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @ConfigValue(config = "snapshotTableNames")
    private Collection tableNames

    @ConfigValue(config = "snapshotQ")
    private String snapshotQ = 'queries/snapshotQueries.sql'

    private BlockingQueue<OneDBItem> q = new LinkedBlockingQueue()

    @ConfigValue
    private Boolean snapshotEnabled = true

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()

    @Inject
    private CepEngine cep

    @ConfigValue(config = "coreSnapshotterFreqMin")
    private Integer coreSnapshotterFreqMin = 1

    @PostConstruct
    private void subscribe() {
        if (snapshotEnabled) {
            def queries = lines(snapshotQ)
            logger.info("Core Snapshot: Start subscribing .... now.")
            queries.eachWithIndex { query, idx ->
                cep.registerMapHandler(query,
                        new Handler<Map>() {
                            @Override
                            boolean handle(Map map) {
                                def mapCopy = [:]
                                mapCopy.putAll(map)
                                if (mapCopy.containsKey('timestamp')) {
                                    mapCopy['timestamp'] = GlobalTimeUtils.getTimeFormat(mapCopy['timestamp'], "yyyyMMddHHmmss")
                                    mapCopy['date'] = (int) (mapCopy['timestamp'] / 1000000)
                                }
                                q.put(new OneDBItem(table: tableNames.getAt(idx), data: mapCopy))
                                return true
                            }
                        }
                )
                Utils.pause(500)
            }
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                void run() {
                    def allRecords = [] as Collection<OneDBItem>
                    q.drainTo(allRecords)
                    if (!allRecords.isEmpty()) {
                        def mapList = [:].withDefault { [] }
                        allRecords.each {
                            mapList[it.table] << it.data
                        }
                        mapList.each { table, listOfRecords ->
                            logger.info("Persisting table ${table}, ${listOfRecords.size()}")
                            flatDao.persist(table, listOfRecords)
                        }
                    }
                }
            }, 1, coreSnapshotterFreqMin, TimeUnit.MINUTES)
        }
    }

    static class OneDBItem {
        String table
        Map data

        String getTable() {
            return table
        }

        void setTable(String table) {
            this.table = table
        }

        Map getData() {
            return data
        }

        void setData(Map data) {
            this.data = data
        }
    }
}
