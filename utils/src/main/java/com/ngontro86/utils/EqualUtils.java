package com.ngontro86.utils;

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

}
