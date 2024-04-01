package com.ngontro86.dboe.web3j.encryption

import org.junit.Test

import java.text.SimpleDateFormat

class KeyHashUtilsTest {

    @Test
    void "should be able to unhash a string"() {
        def date = new SimpleDateFormat('yyyyMMdd').parse('20240630')
        [
                'xxx'
        ].each {
            println KeyHashUtils.sign(KeyHashUtils.unhashedKey(it, 'DBOEToTheMoon'), 'DBOEToTheMoon', date)
        }
    }

}
