package com.ngontro86.common.util


class MapUtils {

    static String csv(LinkedHashMap map) {
        def sb = new StringBuilder()
        boolean first = true
        map.each { k, v ->
            if (!first) {
                sb.append(',')
            }
            sb.append("'${v}'")
            first = false
        }
        return sb.toString()
    }

    static String csv(LinkedHashMap map, Set selectedKeys) {
        def sb = new StringBuilder()
        boolean first = true
        map.each { k, v ->
            if (selectedKeys.contains(k)) {
                if (!first) {
                    sb.append(',')
                }
                sb.append("'${v}'")
                first = false
            }
        }
        return sb.toString()
    }

}
