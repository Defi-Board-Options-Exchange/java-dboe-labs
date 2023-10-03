package com.ngontro86.cep.siddhi

import org.junit.Test

import static com.ngontro86.cep.siddhi.SiddhiUtils.constructSiddhi


class SiddhiUtilsTest {

    @Test
    void "should load siddhi app 1"() {
        println constructSiddhi(true, 'PROD')
    }

    @Test
    void "should load siddhi app 2"() {
        println constructSiddhi(false, 'PROD')
    }

}
