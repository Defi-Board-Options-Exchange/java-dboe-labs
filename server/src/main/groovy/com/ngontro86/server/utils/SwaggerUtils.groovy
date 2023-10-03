package com.ngontro86.server.utils


public class SwaggerUtils {

    public static String toString(Collection<Map> maps) {
        def sb = new StringBuilder()
        maps.each { sb.append(toString(it)).append('\n') }
        return sb.toString()
    }

    public static String toString(Map map) {
        def orderedMap = [:] as TreeMap
        orderedMap.putAll(map)
        def sb = new StringBuilder()
        map.each { k, v ->
            sb.append(k).append(':').append(v).append(',')
        }
        return sb.toString()
    }

}
