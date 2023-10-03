package com.ngontro86.common.email

import org.junit.Ignore
import org.junit.Test

@Ignore
class EmailerTest {

    @Test
    void "should email 1"() {
        def emailer = new Emailer(host: 'smtp.gmail.com', port: 587, username: 'quantvu86@gmail.com', password: 'xxx')
        emailer.sendMessage(
                ['truongvinh.vu@gmail.com', 'truongvinh.vu@sgx.com'],
                'Test Email',
                'Test email'
        )
    }

}
