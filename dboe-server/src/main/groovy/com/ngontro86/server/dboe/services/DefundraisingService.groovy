package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.restful.common.json.JsonUtils
import com.ngontro86.utils.GlobalTimeUtils
import org.apache.logging.log4j.Logger

import javax.inject.Inject

@DBOEComponent
class DefundraisingService {

    @Logging
    private Logger logger

    @NonTxTransactional
    @Inject
    private FlatDao flatDao

    String signup(String walletAddr, Map info) {
        def uuid = UUID.randomUUID().toString()
        flatDao.persist("defundraising.project_info",
                [
                        [
                                'wallet_address': walletAddr,
                                'project_uid'   : uuid,
                                'project_info'  : JsonUtils.toJson(info),
                                'timestamp'     : GlobalTimeUtils.getTimeFormat(GlobalTimeController.currentTimeMillis, 'yyyyMMddHHmmss'),
                        ]
                ])
        return uuid
    }

}
