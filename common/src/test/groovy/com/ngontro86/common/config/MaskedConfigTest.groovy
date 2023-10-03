package com.ngontro86.common.config

import org.junit.Test


class MaskedConfigTest {

    @Test
    void "should run various scenarios"() {
        println MaskedConfig.newInstance().setRawValue("Let build a great Trading Platform").build().hashedValue
        println MaskedConfig.newInstance().setHashedValue("TGV0IGJ1aWxkIGEgZ3JlYXQgVHJhZGluZyBQbGF0Zm9ybQ==").build().unmaskedValue
    }

    @Test
    void "should encode"() {
        assert MaskedConfig.newInstance().setRawValue("HelloWorld").build().hashedValue == "SGVsbG9Xb3JsZA=="
    }

    @Test
    void "should decode"() {
        assert MaskedConfig.newInstance().setHashedValue("SGVsbG9Xb3JsZA==").build().unmaskedValue == "HelloWorld"
    }

    @Test
    void "should encode for INT test"() {
        println MaskedConfig.newInstance().setRawValue("123456789").build().hashedValue
    }

}
