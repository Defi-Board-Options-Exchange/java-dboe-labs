package com.ngontro86.app.common.postprocessor;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

import static com.ngontro86.app.common.postprocessor.AnnotatedFieldUtils.getLoggerField;

public class LoggerPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field field = getLoggerField(bean);
        try {
            if (field != null) {
                System.out.println("Setting logger for: " + bean.getClass().getSimpleName());
                field.set(bean, LogManager.getLogger(bean.getClass()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to set logger for: " + field.getName() + e);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
