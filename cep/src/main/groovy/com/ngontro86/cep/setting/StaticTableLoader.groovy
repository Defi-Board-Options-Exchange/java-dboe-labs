package com.ngontro86.cep.setting

interface StaticTableLoader {

    Map<String, Collection<Map>> load(String cepType, String instanceId, String version)

    Map<String, Collection<Map>> reload(String cepType, String instanceId, String version)

    Map<String, Collection<Map>> slowReload(String cepType, String instanceId, String version)

    Map<String, Collection<Map>> verySlowReload(String cepType, String instanceId, String version)
}