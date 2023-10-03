package com.ngontro86.component.testing.defaultEnvs

import com.ngontro86.app.common.db.DataSource
import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.NonTxTransactional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope

class InMemDatabaseProvider {
    static class DataSourceHolder {
        private static DataSource nonTxDataSource = new HsqlDataSource()
    }

    @Bean
    @NonTxTransactional
    private DataSource nonTxDataSource() {
        return DataSourceHolder.nonTxDataSource
    }

    @Bean
    @NonTxTransactional
    @Scope("prototype")
    private FlatDao nonFlatDao() {
        return new FlatDao(DataSourceHolder.nonTxDataSource)
    }
}
