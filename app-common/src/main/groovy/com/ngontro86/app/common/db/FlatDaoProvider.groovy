package com.ngontro86.app.common.db

import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope

import javax.inject.Inject

@DBOEComponent
class FlatDaoProvider {

    @Inject
    @NonTxTransactional
    private DataSource nonTxnDataSource

    @Bean
    @NonTxTransactional
    @Scope("prototype")
    private FlatDao nonFlatDao() {
        return new FlatDao(nonTxnDataSource)
    }

}
