package com.ngontro86.analytic.server


import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.restful.common.DropwizardRestServer
import com.ngontro86.utils.Utils
import org.apache.logging.log4j.Logger

import javax.inject.Inject

class AnalyticServerApp {

    @Logging
    private Logger logger

    @Inject
    private DropwizardRestServer restServer

    @EntryPoint
    void go() {
        logger.info("~~~~~~~ AnalyticServerApp starting ~~~~~~~")

        println "Done..."

        long cnt = 0
        while (true) {
            Utils.pause(60000)
            if (cnt++ % 5 == 0) {
                logger.info("Sleeping. cnt = ${cnt}")
            }
        }
    }
}
