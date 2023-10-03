package com.ngontro86.cep

import com.ngontro86.cep.esper.EsperEngine
import com.ngontro86.cep.siddhi.SiddhiEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class CepEngineProvider {

    @ConfigValue
    private Boolean esperEnabled = false

    @Bean
    CepEngine engine() {
        esperEnabled ? new EsperEngine() : new SiddhiEngine()
    }
}
