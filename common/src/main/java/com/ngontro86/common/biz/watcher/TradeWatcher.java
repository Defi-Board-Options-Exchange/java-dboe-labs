package com.ngontro86.common.biz.watcher;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.biz.entity.Trade;

import javax.annotation.PostConstruct;
import java.util.Map;

@DBOEComponent
public class TradeWatcher extends AbstractWatcher<String, Trade> {

    @ConfigValue(config = "TradeWatcher")
    private String watcherId = "TradeWatcher";

    @PostConstruct
    private void init() {
        super.init(watcherId);
    }

    @Override
    public String getKey(Trade obj) {
        return obj == null ? null : obj.orderReqId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Trade getValue(Object obj) {
        return Trade.build((Map<String, Object>) obj);
    }

    @Override
    public boolean isEqual(Trade value1, Trade value2) {
        return (0 == value1.compareTo(value2));
    }
}
