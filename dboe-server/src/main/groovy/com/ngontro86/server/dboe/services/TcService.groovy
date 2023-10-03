package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat
import static java.lang.System.currentTimeMillis

@DBOEComponent
class TcService {

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    private Set<String> agreedWallets = [] as Set

    @PostConstruct
    private void init() {
        agreedWallets.addAll(flatDao.queryStringList("select distinct wallet_id from dboe_tc_agreements"))
    }

    boolean agreed(String walletId) {
        agreedWallets.add(walletId)
        flatDao.persist('dboe_tc_agreements', [['wallet_id': walletId, 'timestamp': getTimeFormat(currentTimeMillis(), "yyyyMMddHHmmss")]])
        return true
    }

    boolean exists(String walletId) {
        return agreedWallets.contains(walletId)
    }
}
