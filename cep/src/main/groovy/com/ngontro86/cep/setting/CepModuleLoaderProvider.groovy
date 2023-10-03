package com.ngontro86.cep.setting

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class CepModuleLoaderProvider {

    @ConfigValue(config = "distributedPlatform")
    private Boolean distributionPlatform = false

    @Bean
    CepModuleLoader cepModuleLoader() {
        return distributionPlatform ? new RestfulCepModuleLoader() : new LocalResourceCepModuleLoader()
    }

}
