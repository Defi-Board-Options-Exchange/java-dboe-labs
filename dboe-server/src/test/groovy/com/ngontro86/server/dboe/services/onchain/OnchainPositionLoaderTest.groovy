package com.ngontro86.server.dboe.services.onchain

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Test

class OnchainPositionLoaderTest {
    ComponentEnv env

    @Before
    void init() {
        [
                'ethereumNodeUrl': 'https://polygon-mainnet.infura.io/v3/7dc69b268e1d4520997d38cdc71e1e66'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([OnchainPositionLoader, ConfigValuePostProcessor, LoggerPostProcessor])
    }

    @Test
    void "should be able to pull balance"() {
        def posLoader = env.component(OnchainPositionLoader)
        println posLoader.loadPosition('Polygon', '0x627a80cb0bc705e674c8c3ad4096383043338613', '0x0173a51cf32fd0bcc46c1aa24273e72a32c045f8', '0x01d3b6725103280df855f8d8480cb098851bf3f6')
    }

    @Test
    void "should amend map object"() {
        def ret = [
                [
                        'instr_id': 'X1',
                        'avg_px'  : 25.5
                ]
        ]

        ret.findAll { it['instr_id'].toString().contains('X') }.each {
            it['avg_px'] = 25.2
            it['estimated'] = true
        }

        ret.each { println it }
    }

}
