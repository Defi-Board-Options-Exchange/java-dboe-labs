package com.ngontro86.app.common.postprocessor;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.config.MaskedConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static com.ngontro86.app.common.postprocessor.AnnotatedFieldUtils.getConfigFields;
import static com.ngontro86.common.config.Configuration.config;

public class ConfigValuePostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Field field : getConfigFields(bean)) {
            final ConfigValue configValue = field.getAnnotation(ConfigValue.class);
            final String propertyName = "".equals(configValue.config()) ? field.getName() : configValue.config();
            Class<?> fieldClass = field.getType();
            try {
                if (config().containConfig(propertyName)) {
                    System.out.println(String.format("Setting property: '%s' to be: '%s'", field.getName(), config().getConfig(propertyName)));
                    if (fieldClass == Integer.class || fieldClass == int.class) {
                        field.set(bean, config().getIntConfig(propertyName));
                    } else if (fieldClass == long.class || fieldClass == Long.class) {
                        field.set(bean, config().getLongConfig(propertyName));
                    } else if (fieldClass == Double.class || fieldClass == double.class) {
                        field.set(bean, config().getDoubleConfig(propertyName));
                    } else if (fieldClass == String.class) {
                        field.set(bean, config().getConfig(propertyName));
                    } else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
                        field.set(bean, config().getBooleanConfig(propertyName));
                    } else if (fieldClass == Collection.class || fieldClass == List.class) {
                        field.set(bean, config().getCollectionConfig(propertyName));
                    } else if (fieldClass == MaskedConfig.class) {
                        field.set(bean, MaskedConfig.newInstance().setHashedValue(config().getConfig(propertyName)).build());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to set config value for: " + field.getName() + e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


}
