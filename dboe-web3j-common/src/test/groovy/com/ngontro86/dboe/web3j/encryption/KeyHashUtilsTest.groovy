package com.ngontro86.dboe.web3j.encryption

import org.junit.Test

import java.text.SimpleDateFormat

import static com.ngontro86.dboe.web3j.encryption.KeyHashUtils.unhashedKey

class KeyHashUtilsTest {

    @Test
    void "should be able to hash a string"() {
        def date = new SimpleDateFormat('yyyyMMdd').parse('20240331')
        println KeyHashUtils.sign('xxx', 'DBOEToTheMoon', date)
    }


    @Test
    void "should be able to unhash a string"() {
        println unhashedKey('xxx', 'DBOEToTheMoon')

    }
}
