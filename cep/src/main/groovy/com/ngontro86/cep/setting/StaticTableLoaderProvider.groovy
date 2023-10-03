package com.ngontro86.cep.setting

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class StaticTableLoaderProvider {

    @ConfigValue(config = "distributedPlatform")
    private Boolean distributionPlatform

    @Bean
    StaticTableLoader staticTableLoader() {
        return distributionPlatform ? new RestfulStaticTableLoader() : new LocalDbStaticTableLoader()
    }
}
