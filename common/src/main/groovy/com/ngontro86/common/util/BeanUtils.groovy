package com.ngontro86.common.util

import groovy.transform.Undefined.CLASS
import org.apache.commons.beanutils.BeanUtilsBean
import org.apache.commons.beanutils.ConvertUtilsBean

import static org.apache.commons.beanutils.BeanUtils.describe


class BeanUtils {

    static class InstanceHolder {
        static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new ConvertUtilsBean() {
            @Override
            Object convert(String value, Class clazz) {
                if (clazz.isEnum()) {
                    return Enum.valueOf(clazz, value)
                } else {
                    return super.convert(value, clazz)
                }
            }
        })
    }

    static <T> T populateObject(Map map, Class<T> objClass) {
        T newT = objClass.newInstance()
        copyProperties(newT, map)
        return newT
    }

    static void copyProperties(Object obj, Map map) {
        try {
            InstanceHolder.beanUtilsBean.populate(obj, map)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    static void copyProperties(Object obj, Map map, Map<String, String> propertyRenames) {
        try {
            def newMap = [:]
            newMap.putAll(map)
            propertyRenames.each {k1, k2 ->
                newMap.put(k2, map.get(k1))
            }
            InstanceHolder.beanUtilsBean.populate(obj, newMap)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    static Map properties(Object b) {
        def map = describe(b)
        map.remove('metaClass')
        map.remove('class')
        return map
    }

}
