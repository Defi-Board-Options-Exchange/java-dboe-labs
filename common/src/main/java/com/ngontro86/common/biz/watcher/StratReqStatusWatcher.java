package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.biz.entity.StratReqStatus;

import javax.annotation.PostConstruct;
import java.util.Map;

@DBOEComponent
public class StratReqStatusWatcher extends AbstractWatcher<String, StratReqStatus> {

    @ConfigValue(config = "StratReqStatusWatcher")
    private String watcherId = "StratReqStatusWatcher";

    @PostConstruct
    private void init() {
        super.init(watcherId);
    }

    @Override
    public String getKey(StratReqStatus obj) {
        return obj.strategyName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StratReqStatus getValue(Object obj) {
        return StratReqStatus.build((Map<String, Object>) obj);
    }

    @Override
    public boolean isEqual(StratReqStatus value1, StratReqStatus value2) {
        return (0 == value1.compareTo(value2));
    }
}
