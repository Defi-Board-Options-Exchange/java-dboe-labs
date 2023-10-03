package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.biz.entity.Signal;

import javax.annotation.PostConstruct;
import java.util.Map;

@DBOEComponent
public class SignalWatcher extends AbstractWatcher<String, Signal> {

    @ConfigValue(config = "SignalWatcher")
    private String watcherId = "SignalWatcher";

    @PostConstruct
    private void init() {
        super.init(watcherId);
    }

    @Override
    public String getKey(Signal obj) {
        return obj == null ? null : obj.orderReqId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Signal getValue(Object obj) {
        return Signal.build((Map<String, Object>) obj);
    }

    @Override
    public boolean isEqual(Signal value1, Signal value2) {
        return (0 == value1.compareTo(value2));
    }
}
