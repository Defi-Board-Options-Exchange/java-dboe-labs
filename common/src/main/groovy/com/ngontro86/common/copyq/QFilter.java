package com.ngontro86.common.copyq;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.Logging;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.file.FileObj;
import com.ngontro86.common.serials.ObjMap;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

@DBOEComponent
public class QFilter<T extends Object> extends LinkedBlockingQueue<T> {

    @Logging
    private Logger logger;

    private static final long serialVersionUID = 1L;

    @ConfigValue
    private Boolean copyqEnabled = true;

    @ConfigValue(config = "copyqObjects")
    private Collection objectNames;

    private Set uniqueObjectNames;

    @PostConstruct
    private void init() {
        logger.info("QFilter got enabled: " + copyqEnabled);
        uniqueObjectNames = new HashSet<>(objectNames);
    }

    @Override
    public void put(T obj) {
        if(!copyqEnabled) {
            return;
        }
        String eventName = getEventName(obj);
        if (!uniqueObjectNames.contains(eventName)) {
            return;
        }
        try {
            super.put(obj);
        } catch (Exception e) {
            logger.warn("Q Filter got exception: ", e);
        }
    }

    private String getEventName(Object obj) {
        return obj instanceof FileObj ? getEventName(((FileObj) obj).getData()) :
                (obj instanceof ObjMap ? ((ObjMap) obj).name : obj.getClass().getName());
    }
}
