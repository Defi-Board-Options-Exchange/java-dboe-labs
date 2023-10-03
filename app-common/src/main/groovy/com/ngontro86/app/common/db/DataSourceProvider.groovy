package com.ngontro86.app.common.db

import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class DataSourceProvider {

    static class DataSourceHolder {
        private static DataSource nonTxDataSource = new NonTxDataSource()
    }

    @Bean
    @NonTxTransactional
    private DataSource nonTxDataSource() {
        return DataSourceHolder.nonTxDataSource
    }

}
