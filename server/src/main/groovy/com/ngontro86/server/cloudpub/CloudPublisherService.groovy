package com.ngontro86.server.cloudpub

import com.ngontro86.cep.CepEngine
import com.ngontro86.cloud.publisher.CloudPublisher
import com.ngontro86.common.Handler
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject

@DBOEComponent
@Lazy(false)
class CloudPublisherService {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    @ConfigValue(config = "cloudPublishing")
    private Boolean cloudPublishing = false

    @Inject
    private CloudPublisher publisher

    @PostConstruct
    private void init() {
        if (cloudPublishing) {
            logger.info("Cloud Publishing enabled...")
            cep.registerMapHandler("select * from TradeWin", new Handler<Map<String, Object>>() {
                @Override
                boolean handle(Map<String, Object> obj) {
                    return publisher.add(obj)
                }
            })
        }
    }
}
