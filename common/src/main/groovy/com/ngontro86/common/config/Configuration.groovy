package com.ngontro86.common.config

import com.ngontro86.utils.ResourcesUtils


class Configuration {
    private boolean initialized
    private Map<String, String> configMap = [:]

    private Configuration() {
        System.properties.each { k, v ->
            configMap[k] = v
        }
    }

    boolean containConfig(String key) {
        return configMap.containsKey(key)
    }

    String getConfig(String key) {
        return configMap[key]
    }

    int getIntConfig(String key) {
        throwUnknownKeyException(key)
        return Integer.parseInt(getConfig(key))
    }

    int getIntConfig(String key, int i) {
        if (!containConfig(key)) {
            return i
        }
        return Integer.parseInt(getConfig(key))
    }

    boolean getBooleanConfig(String key) {
        throwUnknownKeyException(key)
        return Boolean.parseBoolean(getConfig(key))
    }

    boolean getBooleanConfig(String key, boolean i) {
        if (!containConfig(key)) {
            return i
        }
        return Boolean.parseBoolean(getConfig(key))
    }

    double getDoubleConfig(String key) {
        throwUnknownKeyException(key)
        return Double.parseDouble(getConfig(key))
    }

    double getDoubleConfig(String key, double d) {
        if (!containConfig(key)) {
            return d
        }
        return Double.parseDouble(getConfig(key))
    }

    long getLongConfig(String key) {
        throwUnknownKeyException(key)
        return Long.parseLong(getConfig(key))
    }

    Collection<String> getCollectionConfig(String key) {
        throwUnknownKeyException(key)
        return getConfig(key).split(',') as Collection<String>
    }

    void loadConfigs(Set configs) {
        configs.each { config ->
            def lines = ResourcesUtils.lines(config)
            lines.findAll { !it.startsWith('#') && it.contains("=") }.each { line ->
                def lineToks = line.trim().split('=')
                configMap[lineToks[0].trim()] = line
                        .replaceFirst("${lineToks[0]}", "")
                        .trim()
                        .replaceFirst("=", "")
                        .trim()
            }
        }
        initialized = true
    }
    private static Configuration configuration = new Configuration()

    static void setConfigs(Set<String> configs) {
        if (configuration.initialized) {
            throw new IllegalStateException('Configuration is already configured.')
        }
        configuration.loadConfigs(configs)
    }

    static Configuration config() {
        return configuration
    }

    void throwUnknownKeyException(String key) {
        if (!containConfig(key)) {
            throw new IllegalStateException("No such config: ${key}")
        }
    }
}
