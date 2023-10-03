package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.biz.entity.PriceMA;

import javax.annotation.PostConstruct;
import java.util.Map;

@DBOEComponent
public class PriceMAWatcher extends AbstractWatcher<String, PriceMA> {

    @ConfigValue(config = "PriceMAWatcher")
    private String watcherId = "PriceMAWatcher";

    @PostConstruct
    private void init() {
        super.init(watcherId);
    }

    @Override
    public String getKey(PriceMA obj) {
        return PriceMA.getKey(obj.instId, obj.hl);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PriceMA getValue(Object obj) {
        return PriceMA.build((Map<String, Object>) obj);
    }

    @Override
    public boolean isEqual(PriceMA value1, PriceMA value2) {
        return (0 == value1.compareTo(value2));
    }
}
