package com.ngontro86.market.volatility

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class VolEstimatorProvider {

    @ConfigValue(config = "volModel")
    private String volModel = "WingModel"

    @Bean
    VolatilityEstimator getVolModel() {
        volModel == 'WingModel' ? new WingModelVolEstimator() :
                (volModel == 'Flat' ? new FlatVolEstimator() : (
                volModel == 'Floating'? new FloatingVolEstimator() : null
                ))
    }
}
