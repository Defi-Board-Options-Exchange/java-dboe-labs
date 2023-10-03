package com.ngontro86.dboe.web3j.encryption

import org.junit.Test

import java.text.SimpleDateFormat

class KeyHashUtilsTest {

    @Test
    void "should be able to hashed a string"() {
        def date = new SimpleDateFormat('yyyyMMdd').parse('20231231')
        println KeyHashUtils.sign('xxx1', 'DBOEToTheMoon', date)
        println KeyHashUtils.sign('xxx2', 'DBOEToTheMoon', date)
        println KeyHashUtils.sign('xxx3', 'DBOEToTheMoon', date)

    }
}
