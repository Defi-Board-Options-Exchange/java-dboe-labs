package com.ngontro86.utils


class MapEqualUtils {

    static boolean mapContains(Map map1, Map map2) {
        boolean yes = true
        map2.each { k, v ->
            def eq = map1[k] == v
            if(!eq) {
                println "Two values are not equal. ${map1[k]}, $v"
            }
            yes &= eq
        }
        return yes
    }

}
