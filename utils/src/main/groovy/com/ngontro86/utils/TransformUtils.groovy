package com.ngontro86.utils


class TransformUtils {

    static Map<String, Map<String, Object>> transform(List<Map<String, Object>> listMap, String keyProperty) {
        Map<String, Map<String, Object>> ret = new TreeMap<>()
        for (Map<String, Object> map : listMap) {
            Object keyVal = map.get(keyProperty)
            ret.put((String) keyVal, map)
        }
        return ret
    }

    static Map<String, Map<Object, Collection<Map>>> series(Collection<Map<String, Object>> listMap,
                                                            String idProperty, String timestampProperty) {
        final Map<Object, Map<Object, Map>> ret = [:] as TreeMap
        listMap.each { map ->
            def id = map.get(idProperty)
            if (!ret.containsKey(id)) {
                ret.put(id, [:] as TreeMap)
            }
            ret.get(id).put(map.get(timestampProperty), map)
        }
        return ret
    }

    /**
     *
     * @param listMap
     * @param idProperty
     * @param timestampProperty
     * @param priceProperty
     * @param asOfTimestamps
     * @return a tree map of timestamp and a map of id vs. price
     */
    static Map<Long, Map> samplingAsOfTimestamp(Collection<Map<String, Object>> listMap,
                                                String idProperty,
                                                String timestampProperty,
                                                String priceProperty,
                                                Collection<Long> asOfTimestamps) {
        final Map<Long, Map> ret = [:] as TreeMap
        final Map<Object, Map<Object, Map>> series = series(listMap, idProperty, timestampProperty)
        series.each { seriesId, timeSeries ->
            asOfTimestamps.each { timestamp ->
                if (!ret.containsKey(timestamp)) {
                    ret.put(timestamp, [:])
                }
                ret.get(timestamp).put(seriesId, nearest(timeSeries, timestamp).get(priceProperty))
            }
        }
        return ret
    }

    static Map nearest(Map<Object, Map> maps, Long timestamp) {
        if (maps.containsKey(timestamp)) {
            return maps.get(timestamp)
        }
        Long nearestTimestamp = maps.keySet().sort { (it - timestamp).abs() }.first()
        return maps.get(nearestTimestamp)
    }
}
