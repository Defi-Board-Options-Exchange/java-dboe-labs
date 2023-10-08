package com.ngontro86.cep.setting

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import org.apache.logging.log4j.Logger

import javax.inject.Inject

class LocalDbStaticTableLoader implements StaticTableLoader {

    @Logging
    private Logger logger

    @ConfigValue(config = "staticTables")
    private Collection<String> staticTables

    @ConfigValue(config = "reloadTables")
    private Collection<String> reloadTables

    @ConfigValue(config = "slowReloadTables")
    private Collection<String> slowReloadTables

    @ConfigValue(config = "verySlowReloadTables")
    private Collection<String> verySlowReloadTables

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @Override
    Map<String, Collection<Map>> load(String cepType, String instanceId, String version) {
        logger.info("Loading db, ${cepType}, ${instanceId}")
        return staticTables.collectEntries { table -> [(table): flatDao.queryList("select * from ${table}")] }
    }

    @Override
    Map<String, Collection<Map>> reload(String cepType, String instanceId, String version) {
        logger.info("Reloading db, ${cepType}, ${instanceId}")
        return reloadTables.collectEntries { table -> [(table): flatDao.queryList("select * from ${table}")] }
    }

    @Override
    Map<String, Collection<Map>> slowReload(String cepType, String instanceId, String version) {
        logger.info("Slow reloading db, ${cepType}, ${instanceId}")
        return slowReloadTables.collectEntries { table -> [(table): flatDao.queryList("select * from ${table}")] }
    }

    @Override
    Map<String, Collection<Map>> verySlowReload(String cepType, String instanceId, String version) {
        logger.info("Very Slow reloading db, ${cepType}, ${instanceId}")
        return verySlowReloadTables.collectEntries { table -> [(table): flatDao.queryList("select * from ${table}")] }
    }
}
