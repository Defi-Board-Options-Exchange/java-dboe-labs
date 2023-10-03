package com.ngontro86.cep.setting

import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.util.ResourcesUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct


class LocalResourceCepModuleLoader implements CepModuleLoader {

    @Logging
    private Logger logger

    @PostConstruct
    private void init() {
        logger.info("Cep Modules deployed from local resources...")
    }

    @Override
    Map<String, String> load(String cepType, String cepId) {
        def eplFiles = ResourcesUtils.listResources("esper")
        return eplFiles.findAll { it.endsWith(".esper") }.collectEntries {
            [(it): content(it)]
        }
    }

    private String content(String file) {
        def ret = ResourcesUtils.content(file)
        if (ret != null) {
            return ret
        }
        return com.ngontro86.utils.ResourcesUtils.content(file)
    }

}
