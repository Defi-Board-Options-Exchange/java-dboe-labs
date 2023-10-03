package com.ngontro86.utils

import org.junit.Test

import static com.ngontro86.utils.AESUtils.decrypt
import static com.ngontro86.utils.AESUtils.encrypt

class AESUtilsTest {

    @Test
    void "should be able to decrypt after encrypting text"() {
        10.times {
            def encryptedText = encrypt("user encrypted passphrase", "Hello World")
            println encryptedText
            def decryptBack = decrypt("user encrypted passphrase", encryptedText)
            println decryptBack
            assert decryptBack == "Hello World"
        }
    }

}
