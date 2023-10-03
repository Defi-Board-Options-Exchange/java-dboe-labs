package com.ngontro86.utils;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils {

    public static <T> Collection<Collection<T>> split(final Collection<T> values, int trunkSize) {
        final Collection<Collection<T>> ret = new ArrayList<>();
        Collection<T> oneTrunk = new ArrayList<>();
        ret.add(oneTrunk);
        for (T value : values) {
            if (oneTrunk.size() == trunkSize) {
                oneTrunk = new ArrayList<>();
                ret.add(oneTrunk);
            }
            oneTrunk.add(value);
        }
        return ret;
    }

    public static String join(Collection<Object> objects, String sep) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(Object obj : objects) {
            if(!first) {
                sb.append(sep);
            }
            first = false;
            sb.append(obj);
        }
        return sb.toString();
    }

    public static boolean isEmpty(Collection col) {
        return (col == null || col.isEmpty());
    }


    public static boolean isNotEmpty(Collection col) {
        return !isEmpty(col);
    }

}
