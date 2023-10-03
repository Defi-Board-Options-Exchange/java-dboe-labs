package com.ngontro86.server.dboe.engine

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.app.common.db.FlatDaoProvider
import com.ngontro86.app.common.db.NonTxDataSource
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test

class DboeOptionListINTTest {

    ComponentEnv env

    @Before
    void "init env"() {
        [
                'datasource.host'    : '35.224.75.65',
                'datasource.name'    : 'dboe',
                'datasource.username': 'user_rw',
                'datasource.password': '251120Vi'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([NonTxDataSource, FlatDaoProvider])
    }

    @Test
    void "init Options"() {
        def flatDao = env.component(FlatDao)
        println flatDao.toString()
    }

}
