package com.ngontro86.cep.setting

interface CepModuleLoader {
    Map<String, String> load(String cepType, String cepId)
}