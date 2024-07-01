package com.ngontro86.dboe.web3j.encryption

import com.ngontro86.utils.ResourcesUtils
import org.junit.Test

import java.text.SimpleDateFormat

class KeyHashUtilsTest {

    @Test
    void "should be able to unhash a string"() {
        def date = new SimpleDateFormat('yyyyMMdd').parse('20241230')
        [
                'xxx'
        ].each {
            println KeyHashUtils.sign(KeyHashUtils.unhashedKey(it, 'DBOEToTheMoon'), 'DBOEToTheMoon', date)
        }
    }

    @Test
    void "should be able to unhash"() {
        println KeyHashUtils.unhashedKey(
                '',
                'DBOEToTheMoon'
        )
    }


    @Test
    void "should be able to unhash a string 2"() {
        def date = new SimpleDateFormat('yyyyMMdd').parse('20241230')
        ResourcesUtils.lines("to-encrypt.csv").each { line ->
            println "insert into private_keys values('TeamLP', '${line.split(',')[0]}',1, '${KeyHashUtils.sign(line.split(",")[1], 'DBOEToTheMoon', date)}','DBOEToTheMoon',20241230, '');"
        }
    }

}
