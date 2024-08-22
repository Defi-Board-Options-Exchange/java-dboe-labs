package com.ngontro86.analytic.server.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.NonTxTransactional

import javax.inject.Inject

@DBOEComponent
class FixingPriceService {

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    Collection<Map> fixing(int expiry) {
        flatDao.queryList("select chain, underlying, fsp/1000000000000.0 as fixing from dboe_fsp where expiry=${expiry}")
    }
}
