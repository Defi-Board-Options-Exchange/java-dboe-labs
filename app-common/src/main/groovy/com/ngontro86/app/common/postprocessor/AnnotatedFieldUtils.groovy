package com.ngontro86.app.common.postprocessor

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging

import java.lang.reflect.Field

class AnnotatedFieldUtils {

    static Collection<Field> getConfigFields(Object bean) {
        return getAnnotationFields(bean, ConfigValue.class)
    }

    static Field getLoggerField(Object bean) {
        def field = bean.getClass().getDeclaredFields().find { it.isAnnotationPresent(Logging.class) }
        if (field != null) {
            field.setAccessible(true)
        }
        return field
    }

    private static Collection<Field> getAnnotationFields(Object bean, Class annotation) {
        Collection fields = new ArrayList<>()
        bean
                .getClass()
                .getDeclaredFields()
                .findAll { it.isAnnotationPresent(annotation) }
                .each { field ->
            field.setAccessible(true)
            fields.add(field)
        }
        fields
    }

}
