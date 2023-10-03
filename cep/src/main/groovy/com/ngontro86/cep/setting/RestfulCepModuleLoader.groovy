package com.ngontro86.cep.setting

import com.ngontro86.auth.client.AuthRestClient
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

class RestfulCepModuleLoader implements CepModuleLoader {

    @Logging
    private Logger logger

    @Inject
    private AuthRestClient restClient

    @ConfigValue(config = "cepResourcesPath")
    private String cepResourcesPath

    @PostConstruct
    private void init() {
        logger.info("Cep Modules deployed via Restful API calls...")
    }

    @Override
    Map<String, String> load(String cepType, String cepId) {
        def map = restClient.withQueryParams("${cepResourcesPath}/${cepType}/${cepId}", [:], Map)
        return map.collectEntries { [(it.key): restClient.decrypt(it.value)] }
    }
}
