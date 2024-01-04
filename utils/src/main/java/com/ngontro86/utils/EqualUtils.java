package com.ngontro86.utils;

import java.util.ArrayList;
import java.util.Collection;

public class EqualUtils {

    public static boolean equalsWithNull(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        }
        return obj1.equals(obj2);
    }

    public static boolean equals(double d1, double d2, double epsilon) {
        return Math.abs(d1 - d2) <= epsilon;
    }

    public static <T> boolean equals(Collection<T> col1, Collection<T> col2) {
        if (col1 == null || col2 == null) {
            return true;
        }
        if ((col1 == null && col2 != null) || (col1 != null && col2 == null)) {
            return false;
        }
        if (col1.size() != col2.size()) {
            return false;
        }
        Collection<T> newCol1 = new ArrayList<>(col1);
        newCol1.removeAll(col2);
        return newCol1.isEmpty();
    }

}
