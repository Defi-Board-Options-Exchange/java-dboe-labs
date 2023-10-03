package com.ngontro86.utils;

import java.util.Collection;

public class StringUtils {

    public static boolean isNotEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(str);
    }

    public static boolean isEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isEmpty(str);
    }

    public static String join(Collection<?> collections, String separation) {
        final StringBuilder sb = new StringBuilder();
        boolean firstElement = true;
        for (Object obj : collections) {
            sb.append(String.format("%s%s", firstElement ? "" : separation, obj.toString()));
            firstElement = false;
        }
        return sb.toString();
    }

    public static String join(Collection<?> collections, String separation, String qualifier) {
        final StringBuilder sb = new StringBuilder();
        boolean firstElement = true;
        for (Object obj : collections) {
            if (!firstElement) {
                sb.append(separation);
            }
            sb.append(String.format("%s%s%s", qualifier, obj.toString(), qualifier));
            firstElement = false;
        }
        return sb.toString();
    }

    public static String joinWithSingleQuote(Collection<?> collections, String separation) {
        return org.apache.commons.lang3.StringUtils.join(collections.iterator(), separation);
    }

}
