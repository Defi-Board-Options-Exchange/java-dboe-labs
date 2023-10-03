package com.ngontro86.dboe.web3j

import org.junit.Test

import static com.ngontro86.dboe.web3j.Utils.padding

class UtilsTest {
    @Test
    void "should be able to padd properly"() {
        def bytes = padding(32, "350-351" as byte[])
        println bytes
        assert bytes.length == 32
    }

    @Test
    void "should be able to padd an empty string properly"() {
        def bytes = padding(32, "" as byte[])
        println bytes
        assert bytes.length == 32
    }
}
