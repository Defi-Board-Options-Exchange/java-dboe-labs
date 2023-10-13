package com.ngontro86.app.common;

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor;
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor;
import com.ngontro86.common.annotations.EntryPoint;
import com.ngontro86.common.annotations.UncaughtExceptionHandler;
import com.ngontro86.common.config.Configuration;
import com.ngontro86.utils.ResourcesUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


public class AppBootstrap {

    static {
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        System.setProperty("user.timezone", "Asia/Singapore");
    }

    public static void main(String[] args) {
        try {
            final Class appClass = Class.forName(System.getProperty("application"));
            System.out.println("Application to bootstrap: " + appClass);
            final String appConfigs = System.getProperty("appConfigs");
            if (appConfigs != null) {
                System.out.println("App Configs: " + appConfigs);
                Configuration.setConfigs(new LinkedHashSet<>(Arrays.asList(appConfigs.split(","))));
            }
            final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.register(appClass);
            context.register(ConfigValuePostProcessor.class);
            context.register(LoggerPostProcessor.class);

            context.scan("com.ngontro86.app.common");
            Set<String> excludedPackages = new HashSet<>();
            for (String exlPackage : ResourcesUtils.lines("excludedComponents")) {
                excludedPackages.add(exlPackage);
            }
            for (String packageBase : ResourcesUtils.lines("components")) {
                if (!excludedPackage(excludedPackages, packageBase)) {
                    context.scan(packageBase);
                    System.out.println("Scanning: " + packageBase);
                }
            }
            context.refresh();

            final Object app = context.getBean(appClass);
            final Method entry = findMarkedMethod(appClass, EntryPoint.class);
            final Method uncaughtExceptionHandling = findMarkedMethod(appClass, UncaughtExceptionHandler.class);
            if (uncaughtExceptionHandling != null) {
                Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
                    try {
                        uncaughtExceptionHandling.invoke(app, e);
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                });
            }
            System.out.println(String.format("Invoking entry point method, process id: %s....", ManagementFactory.getRuntimeMXBean().getName()));
            entry.invoke(app, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean excludedPackage(Set<String> excls, String basePath) {
        for (String excl : excls) {
            if (excl.startsWith(basePath)) {
                return true;
            }
        }
        return false;
    }

    static Method findMarkedMethod(Class cl, Class annotatedMethod) {
        for (Method method : cl.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotatedMethod)) {
                return method;
            }
        }
        return null;
    }
}
