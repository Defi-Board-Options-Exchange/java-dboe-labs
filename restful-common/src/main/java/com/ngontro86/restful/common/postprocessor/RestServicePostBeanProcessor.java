package com.ngontro86.restful.common.postprocessor;

import com.ngontro86.common.annotations.RestService;
import com.ngontro86.common.annotations.DBOEComponent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.ws.rs.Path;

@DBOEComponent
public class RestServicePostBeanProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RestService.class)) {
            if (!bean.getClass().isAnnotationPresent(Path.class)) {
                throw new IllegalStateException("Class: " + bean.getClass() + " is marked as Rest Service but not java.ws.rs compliant");
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
