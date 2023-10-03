package com.ngontro86.common.biz.watcher

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.biz.entity.PairBasis

import javax.annotation.PostConstruct

@DBOEComponent
class PairBasisWatcher extends AbstractWatcher<String, PairBasis> {

    @ConfigValue(config = "PairBasisWatcher")
    private String watcherId = "PairBasisWatcher"

    @PostConstruct
    private void init() {
        super.init(watcherId)
    }

    @Override
    String getKey(PairBasis obj) {
        return obj.getPairName()
    }

    @SuppressWarnings("unchecked")
    @Override
    PairBasis getValue(Object obj) {
        return PairBasis.build((Map<String, Object>) obj)
    }

    @Override
    boolean isEqual(PairBasis value1, PairBasis value2) {
        return false
    }
}
