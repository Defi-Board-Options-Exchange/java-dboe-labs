package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.biz.entity.Price;

import javax.annotation.PostConstruct;
import java.util.Map;

@DBOEComponent
public class PriceWatcher extends AbstractWatcher<String, Price> {

    @ConfigValue(config = "PriceWatcher")
    private String watcherId = "PriceWatcher";

    @PostConstruct
    private void init() {
        super.init(watcherId);
    }

    @Override
    public String getKey(Price obj) {
        return obj == null ? null : obj.instId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Price getValue(Object obj) {
        return Price.build((Map<String, Object>) obj);
    }

    @Override
    public boolean isEqual(Price value1, Price value2) {
        return (0 == value1.compareTo(value2));
    }
}
