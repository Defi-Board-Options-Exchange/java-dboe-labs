package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.biz.entity.Instrument;

import javax.annotation.PostConstruct;
import java.util.Map;

@DBOEComponent
public class InstrumentWatcher extends AbstractWatcher<String, Instrument> {

    @ConfigValue(config = "InstWatcher")
    private String watcherId = "InstWatcher";

    @PostConstruct
    private void init() {
        super.init(watcherId);
    }

    @Override
    public String getKey(Instrument obj) {
        return obj.getInstId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Instrument getValue(Object obj) {
        return Instrument.build((Map<String, Object>) obj);
    }

    @Override
    public boolean isEqual(Instrument value1, Instrument value2) {
        return (0 == value1.compareTo(value2));
    }
}
