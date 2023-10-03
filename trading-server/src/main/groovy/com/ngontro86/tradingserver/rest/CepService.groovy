package com.ngontro86.tradingserver.rest

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.serials.ObjMap

import javax.inject.Inject


@DBOEComponent
class CepService {

    @Inject
    private CepEngine cepEngine

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    boolean updateEvent(String eventName, Map event) {
        cepEngine.accept(new ObjMap(eventName, event))
    }

    void updateMM(String mmName, String qAliasInstId, Map map) {
        def mms = flatDao.queryList("select * from mm_config where name = '${mmName}' and q_alias_inst_id = '${qAliasInstId}'")
        if(!mms.isEmpty()) {
            def event = new ObjMap('mm_config_event', mms.first())
            map.each {k, v ->
                if(event.containsKey(k)) {
                    event.put(k, v)
                }
            }
            cepEngine.accept(event)
        }
    }

    Collection<Map> allMMs() {
        flatDao.queryList("select * from mm_config where active = 1")
    }
}
