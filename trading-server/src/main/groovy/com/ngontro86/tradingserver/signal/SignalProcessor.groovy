package com.ngontro86.tradingserver.signal


import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.timer.TaskScheduler
import org.apache.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject

@Lazy(false)
@DBOEComponent
class SignalProcessor {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    @Inject
    private TaskScheduler taskScheduler

    @PostConstruct
    private void init() {
        logger.info("Signal Processor initiated...")
    }
}
