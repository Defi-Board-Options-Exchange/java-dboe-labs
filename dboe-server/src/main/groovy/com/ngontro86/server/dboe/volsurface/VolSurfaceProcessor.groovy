package com.ngontro86.server.dboe.volsurface

import com.ngontro86.cep.CepEngine
import com.ngontro86.cep.esper.utils.OptionUtils
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.common.times.GlobalTimeController
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

import static com.ngontro86.utils.GlobalTimeUtils.getTimeUtc

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

    private Map<String, Map<Integer, VolSpreadWing>> volSpreadWings = [:]

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

    private void initVolSpreadWing(Collection<Map> options) {
        options.each {
            volSpreadWings.putIfAbsent(it['underlying'], [:])
            volSpreadWings.get(it['underlying']).putIfAbsent(it['expiry'], new VolSpreadWing(getTimeUtc(String.format("%08d %06d", it['expiry'], it['ltt']), "GMT", "yyyyMMdd HHmmss")))
        }
    }

    private void fit(Collection<Map> options) {
        options.groupBy { [underlying: it['underlying'], expiry: it['expiry']] }.each { k, v ->
            try {
                def volSpreadWing = volSpreadWings.get(k.underlying).get(k.expiry)
                def mktPrices = v.flatten()
                if (!mktPrices.isEmpty() && volSpreadWing != null) {
                    volSpreadWing.set_ref_price(mktPrices.first()['spot'])
                    volSpreadWing.refit(
                            mktPrices.first()['spot'],
                            GlobalTimeController.currentTimeMillis,
                            0,
                            mktPrices.findAll { it['bid'] > 0d && it['ask'] > 0d }.collect {
                                new Spread(it['strike'], it['cond_strike'], it['kind'] == 'Call' ? Kind.C : Kind.P).set_bid_ask(it['bid'], it['ask'])
                            }
                    )
                }
            } catch (Exception e) {
                logger.error(e)
            }
        }
    }

    private void implyVols(Collection<Map> options) {
        options.each {
            def volSpreadWing = volSpreadWings.get(it['underlying']).get(it['expiry'])
            if (volSpreadWing != null) {
                it['ref_iv'] = volSpreadWing.get_vol(it['spot'], it['strike']) * 100d
            }
        }
    }

    private void processRawVols() {
        try {
            def optionWithMktPrices = cepEngine.queryMap(queryRawPx)
            initVolSpreadWing(optionWithMktPrices)
            fit(optionWithMktPrices)
            implyVols(optionWithMktPrices)
            VolSurfaceHelper.updateGreek(optionWithMktPrices)
            logger.info("Publishing ${optionWithMktPrices.findAll { it['ref_iv'] > 0d }.size()} events to CEP...")
            optionWithMktPrices.findAll { it['ref_iv'] > 0d }.each {
                cepEngine.accept(new ObjMap("DboeOptionGreekEvent",
                        [
                                'instr_id'    : it['instr_id'],
                                'ref_iv'      : it['ref_iv'],
                                'bid_iv'      : it['ref_iv'],
                                'ask_iv'      : it['ref_iv'],
                                'spot'        : it['spot'],
                                'ref'         : it['ref'],
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
