package com.ngontro86.cep.setting

import com.ngontro86.auth.client.AuthRestClient
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

class RestfulStaticTableLoader implements StaticTableLoader {

    @Logging
    private Logger logger

    @Inject
    private AuthRestClient restClient

    @ConfigValue(config = "tableLoaderPath")
    private String tableLoaderPath

    @PostConstruct
    private void init() {
        logger.info("Restful Static Table Loader...")
    }

    @Override
    Map<String, Collection<Map>> load(String cepType, String instanceId, String version) {
        return restClient.withQueryParams("${tableLoaderPath}/${cepType}/${instanceId}", [:], Map)
    }

    @Override
    Map<String, Collection<Map>> reload(String cepType, String instanceId, String version) {
        return [:]
    }

    @Override
    Map<String, Collection<Map>> slowReload(String cepType, String instanceId, String version) {
        return [:]
    }
}
