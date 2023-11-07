package com.ngontro86.server.dboe.volsurface

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.utils.ResourcesUtils
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Lazy(false)
@DBOEComponent
class VolSurfaceProcessor {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cepEngine

    @ConfigValue(config = "reloadSurfaceFredMin")
    private Integer reloadSurfaceFredMin

    @ConfigValue(config = "smoothSurfaceEnable")
    private Boolean smoothSurfaceEnable = true

    private String query = ResourcesUtils.content("epl-query/raw-iv.sql")

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()

    @PostConstruct
    private void init() {
        if (smoothSurfaceEnable) {
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                void run() {
                    processRawVols()
                }
            }, 5, reloadSurfaceFredMin, TimeUnit.MINUTES)
        }
    }

    private void processRawVols() {
        try {
            def vols = cepEngine.queryMap(query)
            VolSurfaceHelper.smoothenVols(vols)
            vols.each {
                cepEngine.accept(new ObjMap("DboeVolSurfaceEvent", it))
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }
}
