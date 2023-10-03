package com.ngontro86.server.dboe.services

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.serials.ObjMap

import javax.inject.Inject

@DBOEComponent
class CepService {

    @Inject
    private CepEngine cepEngine

    void updateEvent(String eventName, Map event) {
        cepEngine.accept(new ObjMap(eventName, event))
    }

}
