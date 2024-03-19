package com.ngontro86.cep.esper;

import com.espertech.esper.client.*;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.TimerControlEvent;
import com.ngontro86.cep.CepEngine;
import com.ngontro86.cep.esper.aggregation.MAvgAggregationFunction;
import com.ngontro86.cep.esper.aggregation.PrevDoubleAggregationFunction;
import com.ngontro86.cep.setting.CepModuleDeployer;
import com.ngontro86.cep.setting.ui.UserInfoSetup;
import com.ngontro86.common.Handler;
import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.Logging;
import com.ngontro86.common.copyq.QFilter;
import com.ngontro86.common.file.FileObj;
import com.ngontro86.common.serials.ObjMap;
import com.ngontro86.utils.Utils;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.ngontro86.cep.esper.EsperUtils.parseEventBeanUnderlyingAsMap;

public class EsperEngine implements CepEngine {
    @Inject
    private UserInfoSetup setup;

    @ConfigValue
    private final Boolean externalClock = true;
    @ConfigValue
    private final Boolean priorityExecution = true;
    @ConfigValue
    private final Boolean executionLogDebugEnabled = false;
    @ConfigValue
    private final Boolean timerDebugEnabled = false;

    @ConfigValue
    private final Boolean queryPlanEnabled = false;

    private final LinkedList<EsperHandlerListener<? extends Object>> handlers = new LinkedList<>();
    protected volatile long totalEvent = 0, queryCount;

    @Logging
    private Logger logger;

    @ConfigValue
    private String serverId;

    @ConfigValue(config = "cepInstanceId")
    private String esperId;

    @Inject
    private CepModuleDeployer modulesDeployer;

    private EPServiceProvider epService;
    private EPAdministrator epAdmin;
    private EPRuntime epRuntime;
    private EPDeploymentAdmin deployAdmin;
    @Inject
    private QFilter<FileObj> copyQ;

    @PostConstruct
    private void init() {
        Configuration config = new Configuration();

        config.getEngineDefaults().getLogging().setEnableExecutionDebug(executionLogDebugEnabled);
        config.getEngineDefaults().getLogging().setEnableTimerDebug(timerDebugEnabled);
        config.getEngineDefaults().getLogging().setEnableQueryPlan(queryPlanEnabled);
        config.getEngineDefaults().getExecution().setPrioritized(priorityExecution);

        epService = EPServiceProviderManager.getProvider(serverId, config);

        ConfigurationOperations configOp = epService.getEPAdministrator().getConfiguration();

        configOp.addPlugInAggregationFunctionFactory("mavg", MAvgAggregationFunction.class.getName());
        configOp.addPlugInAggregationFunctionFactory("prevd", PrevDoubleAggregationFunction.class.getName());

        epAdmin = epService.getEPAdministrator();
        deployAdmin = epAdmin.getDeploymentAdmin();

        epRuntime = epService.getEPRuntime();

        if (externalClock) {
            config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
            this.accept(new TimerControlEvent(TimerControlEvent.ClockType.CLOCK_EXTERNAL));
            this.accept(new CurrentTimeEvent(1));
        }

        modulesDeployer.deploy(this, "esper", esperId);
        logger.info("Done initialising CEP...");
    }

    @Override
    public boolean accept(Object obj) {
        if (obj instanceof ObjMap) {
            ObjMap objMap = (ObjMap) obj;
            epRuntime.sendEvent(objMap, objMap.name);
        } else {
            epRuntime.sendEvent(obj);
        }
        this.copyQ.put(new FileObj(System.currentTimeMillis(), obj));
        totalEvent++;
        return true;
    }

    @Override
    public List<Map<String, Object>> queryMap(String query) {
        try {
            LinkedList<Map<String, Object>> ret = new LinkedList<Map<String, Object>>();
            synchronized (this) {
                Iterator<EventBean> it = epRuntime.executeQuery(query).iterator();
                while (it.hasNext()) {
                    ret.add(parseEventBeanUnderlyingAsMap(it.next()));
                }
            }
            queryCount++;
            return ret;
        } catch (Exception e) {
            logger.error(String.format("Could not execute queryObj. Query: %s; Error: %s", query, e.getMessage()));
            return null;
        }
    }

    @Override
    public List<Object> queryObj(String query) {
        try {
            List<Object> ret = new LinkedList<Object>();
            synchronized (this) {
                Iterator<EventBean> it = epRuntime.executeQuery(query).iterator();
                while (it.hasNext()) {
                    ret.add(it.next().getUnderlying());
                }
            }
            this.queryCount++;
            return ret;
        } catch (Exception e) {
            logger.error("Could not execute queryObj. Query: " + query + "; Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean registerMapHandler(String query, Handler<Map<String, Object>> handler) {
        if (handler == null) {
            logger.error("Null Handler cannot be registered!");
            return false;
        }
        EPStatement statement = epAdmin.createEPL(query);
        EsperHandlerListener<Map<String, Object>> hListener = new EsperHandlerListener<Map<String, Object>>(this, handler, statement, true);
        startListener(statement, hListener);
        logger.info("Registered handler for: " + query);
        return true;
    }

    @Override
    public boolean registerObjectHandler(String query, Handler<Object> handler) {
        try {
            if (handler == null) {
                logger.error("Null Handler cannot be registered!");
                return false;
            }
            EPStatement statement = epAdmin.createEPL(query);
            EsperHandlerListener<Object> hListener = new EsperHandlerListener<Object>(this, handler, statement, false);
            startListener(statement, hListener);
            logger.info("Registered object handler for: " + query);
        } catch (Exception e) {
            logger.error("Failed to register Object Handler for query: " + query, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean registerSubscriber(String query, Object subscriber) {
        try {
            if (subscriber == null) {
                logger.error("Null Subscriber cannot be registered!");
                return false;
            }
            EPStatement statement = epAdmin.createEPL(query);
            statement.setSubscriber(subscriber);
            logger.info("Registered subscriber for: " + query);
        } catch (Exception e) {
            logger.error("Failed to register subscriber for query: " + query, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean unregisterHandler(Handler<? extends Object> handler) {
        synchronized (this.handlers) {
            Iterator<EsperHandlerListener<? extends Object>> it = this.handlers.iterator();
            while (it.hasNext()) {
                EsperHandlerListener<? extends Object> h = it.next();
                if (h.handler.equals(handler)) {
                    h.stop(); //TODO: double check this equality of handlers!!!
                }
            }
        }
        cleanListeners();
        return true;
    }

    @Override
    public void unregisterMapHandler(Handler<Map<String, Object>> handler) {
        unregisterHandler(handler);
    }

    public EPDeploymentAdmin getDeployAdmin() {
        return deployAdmin;
    }

    private void startListener(EPStatement statement, EsperHandlerListener<? extends Object> hListener) {
        Utils.startThread(hListener, Thread.NORM_PRIORITY);
        statement.setSubscriber(hListener);
        synchronized (this.handlers) {
            this.handlers.add(hListener);
        }
    }

    @Override
    public long getCurrentTimeMillis() {
        return this.epRuntime.getCurrentTime();
    }

    @Override
    public void cleanListeners() {
        synchronized (this.handlers) {
            Iterator<EsperHandlerListener<? extends Object>> it = this.handlers.iterator();
            while (it.hasNext()) {
                if (it.next().getStopped()) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public void terminate() {
        this.epService.destroy();
        this.handlers.forEach(EsperHandlerListener::stop);
    }

}
