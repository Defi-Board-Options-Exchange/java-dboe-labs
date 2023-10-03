package com.ngontro86.server;

import com.ngontro86.cep.CepEngine;
import com.ngontro86.cep.CepTimeController;
import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.EntryPoint;
import com.ngontro86.common.annotations.Logging;
import com.ngontro86.restful.common.DropwizardRestServer;
import com.ngontro86.server.copyq.QWriter;
import com.ngontro86.server.gateway.ObjReceiver;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.inject.Inject;

import static com.ngontro86.utils.Utils.pause;

public class ServerApp {

    @Logging
    protected Logger logger;

    @Inject
    private CepEngine cep;

    @Inject
    private QWriter qWriter;

    @ConfigValue(config = "exposeObjConnection")
    private Boolean exposeObjConnection = true;

    @Inject
    private DropwizardRestServer restServer;

    @Inject
    private WebSocketMessageBrokerConfigurer webSocketServer;

    @Inject
    private ApplicationContext context;

    @EntryPoint
    public void go() {
        CepTimeController.setCepEngine(cep);
        logger.info("Done initializing Cep. Pause for 100 ms");
        pause(100);

        if (exposeObjConnection) {
            context.getBean(ObjReceiver.class);
        }

        long cnt = 0;
        while (true) {
            Utils.pause(60000);
            if (cnt++ % 5 == 0) {
                logger.info(String.format("Sleeping. cnt = %d", cnt));
            }
        }
    }

}
