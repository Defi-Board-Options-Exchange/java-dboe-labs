package com.ngontro86.cep.esper

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.cep.setting.CepModuleDeployer
import com.ngontro86.cep.setting.LocalResourceCepModuleLoader
import com.ngontro86.cep.setting.ui.UserInfoSetup
import com.ngontro86.common.Handler
import com.ngontro86.common.copyq.QFilter
import com.ngontro86.common.serials.ObjMap
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test

class EsperEngineTest {

    ComponentEnv env

    @Before
    void init() {
        [
                'copyqObjects'    : 'IBPriceEvent,IBSizeEvent',
                'esperEnabled'    : 'true',
                'allowUserToSetUp': 'false',
                'cepInstanceId'   : 'DBOE-SERV',
                'externalClock'   : 'false'

        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([EsperEngine, CepModuleDeployer, QFilter, ConfigValuePostProcessor, LoggerPostProcessor, LocalResourceCepModuleLoader, UserInfoSetup])
    }

    @Test
    void "should bring up DBOE Server"() {
        def cep = env.component(EsperEngine)

        cep.registerMapHandler("select * from DboeOptionInstrWin", new Handler<Map<String, Object>>() {
            @Override
            boolean handle(Map<String, Object> obj) {
                println "Match: ${obj}"
                return true
            }
        })

        cep.registerMapHandler("select * from DboeOrderBookWin", new Handler<Map<String, Object>>() {
            @Override
            boolean handle(Map<String, Object> obj) {
                println "OD: ${obj}"
                return true
            }
        })

        cep.registerObjectHandler("select * from DboeBBOWin", new Handler<Object>() {
            @Override
            boolean handle(Object obj) {
                println "BBO: ${obj}"
                return true
            }
        })

        [
                [
                        'instr_id'              : 'Opt1',
                        'collateral_group'      : '20%',
                        'ltt'                   : 150000,
                        'currency'              : 'USDT',
                        'underlying'            : 'ETH',
                        'kind'                  : 'Call',
                        'expiry'                : 20211230,
                        'strike'                : 4500,
                        'cond_strike'           : 5100,
                        'multiplier'            : 1,
                        'long_address'          : '0x123',
                        'short_address'         : '0x123',
                        'ob_address'            : '0x123',
                        'option_factory_address': '0x123',
                        'clearing_address'      : '0x123',
                ],
                [
                        'instr_id'              : 'Opt2',
                        'collateral_group'      : '20%',
                        'ltt'                   : 150000,
                        'currency'              : 'USDT',
                        'underlying'            : 'ETH',
                        'kind'                  : 'Put',
                        'expiry'                : 20211230,
                        'strike'                : 4400,
                        'cond_strike'           : 3800,
                        'multiplier'            : 1,
                        'long_address'          : '0x123',
                        'short_address'         : '0x123',
                        'ob_address'            : '0x123',
                        'option_factory_address': '0x123',
                        'clearing_address'      : '0x123',
                ]
        ].each { cep.accept(new ObjMap('DboeOptionInstrEvent', it)) }

    }

}
