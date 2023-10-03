package com.ngontro86.algo.utils

import com.ngontro86.common.util.BeanUtils

class StrategyUtils {
    static <T> T load(Collection<Map> strategyMap, Class<T> aClass) {
        def strategy = aClass.newInstance()
        def mapProperty = strategyMap.collectEntries { [(it.get('variable')): it.get('value')] }
        BeanUtils.copyProperties(strategy, mapProperty)
        return strategy
    }

    static void validatePairName(String pairName) {
        if (pairName.contains('-')) {
            throw new IllegalFormatException("Pair name ${pairName} contain character '-'. Invalid!")
        }
    }
}
