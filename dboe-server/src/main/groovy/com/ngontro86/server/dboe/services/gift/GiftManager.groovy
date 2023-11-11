package com.ngontro86.server.dboe.services.gift

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

@DBOEComponent
class GiftManager {

    @Logging
    private Logger logger

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    @Inject
    private CepEngine engine

    private Collection<Map> giftConfigs

    @PostConstruct
    private void init() {
        giftConfigs = flatDao.queryList("select * from dboe_mysterious_gift_config")
    }

    Map<String, Integer> numOfGifts(String wallet) {

    }

    Double openGift(String wallet, String name) {

    }

}
