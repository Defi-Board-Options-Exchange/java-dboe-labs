package com.ngontro86.server.dboe.engine

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.app.common.db.FlatDaoProvider
import com.ngontro86.app.common.db.NonTxDataSource
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class DboeOptionListINTTest {

    ComponentEnv env

    @Before
    void "init env"() {
        [
                'datasource.host'    : 'xxxx',
                'datasource.name'    : 'dboe',
                'datasource.username': 'xxxx',
                'datasource.password': 'xxxx'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([NonTxDataSource, FlatDaoProvider])
    }

    @Test
    void "init Options"() {
        def flatDao = env.component(FlatDao)
        println flatDao.toString()
    }

}
