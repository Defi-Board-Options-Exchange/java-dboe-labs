package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.biz.entity.OrderMap;

import javax.annotation.PostConstruct;
import java.util.Map;

@DBOEComponent
public class OrderMapWatcher extends AbstractWatcher<String, OrderMap> {

    @ConfigValue(config = "OrderMapWatcher")
    private String watcherId = "OrderMapWatcher";

    @PostConstruct
    private void init() {
        super.init(watcherId);
    }

    @Override
    public String getKey(OrderMap obj) {
        return obj == null ? null : obj.orderReqId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public OrderMap getValue(Object obj) {
        return OrderMap.build((Map<String, Object>) obj);
    }

    @Override
    public boolean isEqual(OrderMap value1, OrderMap value2) {
        return (0 == value1.compareTo(value2));
    }

}
