package com.ngontro86.common.email


import org.junit.Test

class EmailerTest {

    @Test
    void "should email 1"() {
        def emailer = new Emailer(host: 'smtp.gmail.com', port: 587, username: 'xxx@dboe.io', password: 'xxxxx')
        emailer.sendMessage(
                ['xxx@gmail.com'],
                'Test Email',
                'Test email'
        )
    }

    @Test
    void "should email sendgrid"() {
        def emailer = new Emailer(host: 'smtp.xxxx.net', port: 587, username: 'apikey', password: 'xxx')
        emailer.sendMessage(
                'xxx@gmail.com',
                ['xxx@gmail.com'],
                'Test Email',
                'Test email'
        )
    }

}
