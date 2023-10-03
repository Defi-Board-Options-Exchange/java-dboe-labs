package com.ngontro86.cloud.publisher

import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class CloudPublisherTest {

    ComponentEnv env

    @Before
    void init() {
        env = ComponentEnv.env([GoogleCloudPublisher, ConfigValuePostProcessor, LoggerPostProcessor])
        [
                'projectId': 'black-overview-192905',
                'topicId'  : 'userTrades_topic',
        ].each { k, v -> System.setProperty(k, v) }
    }

    @Test
    void "should publish a message"() {
        def cloudPublisher = env.component(GoogleCloudPublisher)
        10.times {
            cloudPublisher.add(['inst_id': 'SGPV20', 'qty': 10])
        }
        sleep 10000
    }

}
