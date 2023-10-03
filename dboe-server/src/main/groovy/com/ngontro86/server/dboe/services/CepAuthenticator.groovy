package com.ngontro86.server.dboe.services

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent


@DBOEComponent
class CepAuthenticator {

    @ConfigValue(config = "cepAuthPasscode")
    private String cepAuthPasscode = "2511"

    boolean auth(String passCode) {
        return cepAuthPasscode == passCode
    }

}
