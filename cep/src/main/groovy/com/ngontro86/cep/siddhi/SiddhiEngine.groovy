package com.ngontro86.cep.siddhi

import com.ngontro86.cep.CepEngine
import com.ngontro86.cep.esper.EsperHandlerListener
import com.ngontro86.cep.setting.CepModuleDeployer
import com.ngontro86.common.Handler
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.copyq.QFilter
import com.ngontro86.common.file.FileObj
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.utils.Utils
import io.siddhi.core.SiddhiAppRuntime
import io.siddhi.core.SiddhiManager
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.ngontro86.cep.siddhi.SiddhiUtils.constructSiddhi
import static com.ngontro86.cep.siddhi.SiddhiUtils.toMap

class SiddhiEngine implements CepEngine {

    @Logging
    private Logger logger

    protected volatile long totalEvent = 0, queryCount

    @ConfigValue(config = 'instanceId')
    private String serverId

    @ConfigValue(config = "cepEngine")
    private String cepEngineId

    @ConfigValue(config = "externalClock")
    private Boolean externalClock = false

    @Inject
    private QFilter<FileObj> copyQ

    private SiddhiManager siddhiManager
    private SiddhiAppRuntime appRuntime

    @Inject
    private CepModuleDeployer modulesDeployer;

    private LinkedList<SiddhiHandlerListener<? extends Object>> handlers = new LinkedList<>()

    @PostConstruct
    private void init() {
        siddhiManager = new SiddhiManager()
        def siddhiApp = constructSiddhi(externalClock, serverId)
        logger.info(siddhiApp)
        appRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp)
        appRuntime.start()
    }

    @Override
    boolean accept(Object obj) {
        if (obj instanceof ObjMap) {
            def objMap = (ObjMap) obj
            def inputHandler = appRuntime.getInputHandler(objMap.name)
            if (inputHandler != null) {
                inputHandler.send(objMap.values() as Object[])
            }
        }
        this.copyQ.put(new FileObj(System.currentTimeMillis(), obj))
        totalEvent++
        true
    }

    @Override
    List<Map<String, Object>> queryMap(String query) {
        def events = appRuntime.query(query)
        final def attributes = appRuntime.getStoreQueryOutputAttributes(query)
        return events.collect { event -> toMap(attributes, event) }
    }

    @Override
    List<Object> queryObj(String query) {
        return null
    }

    @Override
    boolean registerMapHandler(String query, Handler<Map<String, Object>> handler) {
        def siddhiHandlerListener = new SiddhiHandlerListener(this, handler, appRuntime.getStoreQueryOutputAttributes(query), query)
        appRuntime.addCallback(query, siddhiHandlerListener)
        Utils.startThread(siddhiHandlerListener, Thread.NORM_PRIORITY)
        handlers.add(siddhiHandlerListener)
        true
    }

    @Override
    boolean registerObjectHandler(String query, Handler<Object> handler) {
        return false
    }

    @Override
    boolean registerSubscriber(String query, Object subscriber) {
        return false
    }

    @Override
    boolean unregisterHandler(Handler<?> handler) {
        synchronized (this.handlers) {
            Iterator<SiddhiHandlerListener<? extends Object>> it = this.handlers.iterator()
            while (it.hasNext()) {
                EsperHandlerListener<? extends Object> h = it.next()
                if (h.handler.equals(handler)) {
                    h.stop()
                }
            }
        }
        cleanListeners()
        true
    }

    @Override
    void unregisterMapHandler(Handler<Map<String, Object>> handler) {
        unregisterHandler(handler)
    }

    @Override
    long getCurrentTimeMillis() {
        0
    }

    @Override
    void cleanListeners() {
        synchronized (this.handlers) {
            Iterator<SiddhiHandlerListener<? extends Object>> it = this.handlers.iterator()
            while (it.hasNext()) {
                if (it.next().getStopped()) {
                    it.remove()
                }
            }
        }
    }

    @Override
    void terminate() {
        appRuntime.shutdown()
    }
}
