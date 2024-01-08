package com.ngontro86.server.dboe.volsurface

import com.ngontro86.cep.CepEngine
import com.ngontro86.cep.esper.utils.OptionUtils
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.market.volatility.ParamlessPolynomialSurface
import com.ngontro86.market.volatility.downloader.VolDownloader
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

    @ConfigValue(config = "injectExternalSurface")
    private Boolean injectExternalSurface = true

    @Inject
    private VolDownloader volDownloader

    @ConfigValue(config = "externalSurfaceUnderlyings")
    private Collection externalSurfaceUnderlyings = ['BTC', 'ETH']

    private String queryRawPx = ResourcesUtils.content("epl-query/raw-px.sql")
    private String queryActiveOptions = ResourcesUtils.content("epl-query/active-options.sql")

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)

    @PostConstruct
    private void init() {
        if (smoothSurfaceEnable) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                void run() {
                    processRawVols()
                }
            }, 5, reloadSurfaceFredMin, TimeUnit.MINUTES)
        }

        if (injectExternalSurface) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                void run() {
                    injectExternalVols()
                }
            }, 5, 30, TimeUnit.MINUTES)
        }
    }

    private void processRawVols() {
        try {
            def optionWithMktPrices = cepEngine.queryMap(queryRawPx)
            VolSurfaceHelper.implyVols(optionWithMktPrices)
            VolSurfaceHelper.smoothenVols(optionWithMktPrices)
            VolSurfaceHelper.updateGreek(optionWithMktPrices)
            logger.info("Publishing ${optionWithMktPrices.findAll { it['ref_iv'] > 0d }.size()} events to CEP...")
            optionWithMktPrices.findAll { it['ref_iv'] > 0d }.each {
                cepEngine.accept(new ObjMap("DboeOptionGreekEvent",
                        [
                                'instr_id'    : it['instr_id'],
                                'ref_iv'      : it['ref_iv'],
                                'bid_iv'      : it['bid_iv'],
                                'ask_iv'      : it['ask_iv'],
                                'spot'        : it['spot'],
                                'greek'       : it['greek'],
                                'in_timestamp': cepEngine.currentTimeMillis,
                        ]
                ))

                cepEngine.accept(new ObjMap("DboeVolSurfaceEvent",
                        [
                                'source'      : 'DBOE',
                                'underlying'  : it['underlying'],
                                'expiry'      : it['expiry'],
                                'kind'        : it['kind'],
                                'timeToExpiry': it['time_to_expiry'],
                                'moneyness'   : it['moneyness'],
                                'vol'         : it['ref_iv'],
                                'atm_price'   : it['spot'],
                                'strike'      : it['strike'],
                                'in_timestamp': cepEngine.currentTimeMillis,
                        ]
                ))
            }
        } catch (Exception e) {
            logger.error(e)
        }
    }

    private void injectExternalVols() {
        try {
            Map<String, ParamlessPolynomialSurface> surfaces = [:]
            externalSurfaceUnderlyings.each {
                surfaces.put(it, new ParamlessPolynomialSurface())
            }
            surfaces.each { und, surface ->
                def volData = volDownloader.loadVols(und)
                volData.each { expiryUtc, map ->
                    surface.addDataThenFit(expiryUtc, map)
                }
            }
            cepEngine.queryMap(queryActiveOptions).each {
                def und = it['underlying']
                if (surfaces.containsKey(und)) {
                    it << [
                            'source': 'Deribit',
                            'vol'   : surfaces.get(und).estVol(OptionUtils.getTimeUtc(it['expiry'], it['ltt']), it['moneyness'])
                    ]
                    cepEngine.accept(new ObjMap("DboeVolSurfaceEvent", it))
                }
            }
        } catch (Exception e) {
            logger.error(e)
        }


    }

}
