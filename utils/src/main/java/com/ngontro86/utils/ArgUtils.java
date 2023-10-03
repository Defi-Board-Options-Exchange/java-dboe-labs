package com.ngontro86.utils;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArgUtils {

    /**
     * Will parse argument in the format: argument1:value1,argument2:value2 and etc into a map
     *
     * @param argStr
     * @return
     */
    public static Map<String, String> getArgMap(String argStr) {
        final String[] argPairs = argStr.split(",");
        final Map<String, String> retMap = new HashMap<>();
        for (String argPair : argPairs) {
            retMap.put(argPair.split(":")[0], argPair.split(":")[1]);
        }
        return retMap;
    }

    public static <T> String getWhereCondition(Collection<T> values) {
        final StringBuilder sb = new StringBuilder();
        final String separator = getSeparatorInWhereCondition(values.iterator().next());
        boolean first = true;

        sb.append(" (");
        for (T value : values) {
            if (!first) {
                sb.append(",");
            }
            sb
                    .append(separator)
                    .append(value)
                    .append(separator);
            first = false;
        }
        sb.append(") ");

        return sb.toString();
    }

    private static String getSeparatorInWhereCondition(Object value) {
        return value instanceof String ? "'" : "";
    }
}
