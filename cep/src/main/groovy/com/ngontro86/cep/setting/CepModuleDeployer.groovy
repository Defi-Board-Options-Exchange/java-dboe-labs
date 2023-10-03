package com.ngontro86.cep.setting

import com.espertech.esper.client.deploy.DeploymentOptions
import com.espertech.esper.client.deploy.Module
import com.ngontro86.cep.esper.EsperEngine
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import org.apache.logging.log4j.Logger

import javax.inject.Inject

@DBOEComponent
class CepModuleDeployer {
    @Logging
    private Logger logger

    @Inject
    private CepModuleLoader cepModuleLoader

    void deploy(EsperEngine engine, String cepType, String esperId) {
        def modules = cepModuleLoader.load(cepType, esperId)
        def rootFile = modules.keySet().find { it.endsWith("${esperId}.esper") }
        loadDeployment(rootFile, modules, engine)
    }

    private void loadDeployment(String rootFileName, Map<String, String> contentMap, EsperEngine engine) {
        try {
            final Map<String, Module> allModules = [:]
            contentMap.each { fileName, fileContent ->
                def module = engine.getDeployAdmin().parse(fileContent)
                allModules.put(module.getName(), module)
            }
            def rootModule = engine.getDeployAdmin().parse(contentMap.get(rootFileName))
            deployModule(engine, rootModule, allModules, new HashSet<>())
        } catch (Exception e) {
            logger.error("Ep Modules Deployment failed!", e)
        }
    }

    private void deployModule(EsperEngine engine, Module module, Map<String, Module> allModules, Set<String> parentModules) throws Exception {
        logger.info("Deploying: " + module.getName())
        parentModules.add(module.getName())
        module.getUses().each { use ->
            if (!engine.getDeployAdmin().isDeployed(use) && !parentModules.contains(use)) {
                deployModule(engine, allModules.get(use), allModules, parentModules)
            }
        }
        engine.getDeployAdmin().deploy(module, new DeploymentOptions())
        logger.info("Deployed: " + module.getName())
        parentModules.remove(module.getName())
    }
}
