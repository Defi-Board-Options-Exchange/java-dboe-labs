package com.ngontro86.dboe.ref.updater

import org.junit.Test

class UtilsTest {

    @Test
    void "should return a good list of strikes"() {
        println Utils.listStrikes(true, 1592.5, 10.0, 2, 3, 100)
        println Utils.listStrikes(false, 1592.5, 10.0, 2, 3, 100)

        println Utils.listStrikes(true, 26900.5, 150.0, 2, 5, 100)
        println Utils.listStrikes(false, 26900.5, 150.0, 2, 5, 100)
    }

    @Test
    void "should work out instr ids"() {
        Utils.listStrikes(true, 1592.5, 10.0, 1, 3, 1).each {
            println Utils.instrId('ETH', true, it, 20230926)
        }

        Utils.listStrikes(false, 1592.5, 10.0, 1, 3, 1).each {
            println Utils.instrId('ETH', false, it, 20230926)
        }

        Utils.listStrikes(false, 1592.5, 10.0, 1, 3, 1).each {
            println Utils.instrId('ETH', false, it, 20231002)
        }
    }
}
