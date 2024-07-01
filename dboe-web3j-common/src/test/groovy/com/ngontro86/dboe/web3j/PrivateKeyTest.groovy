package com.ngontro86.dboe.web3j

import com.ngontro86.app.common.db.DataSourceProvider
import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.app.common.db.FlatDaoProvider
import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test

class PrivateKeyTest {

    ComponentEnv env

    @Before
    void "init"() {
        [
                'datasource.host'    : 'localhost',
                'datasource.name'    : 'dboe_key_admin',
                'datasource.username': 'root',
                'datasource.password': 'root'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([FlatDao, FlatDaoProvider, DataSourceProvider, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to update the hashed keys"() {
        def flatDao = env.componentWithAnnotation(NonTxTransactional.class, FlatDao)
        def keys = flatDao.queryList("select * from dboe_key_admin.private_keys")
        keys.each {
            println it
        }
    }

}
