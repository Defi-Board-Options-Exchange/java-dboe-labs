package com.ngontro86.cloud.publisher

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import org.springframework.context.annotation.Bean

@DBOEComponent
class CloudPublisherFactory {

    @ConfigValue(config = "cloudPublishing")
    private Boolean cloudPublishing = false

    @Bean
    CloudPublisher gloudPublisher() {
        if(!cloudPublishing) {
            return new NoCloudPublisher()
        }
        return new GoogleCloudPublisher()
    }

}
