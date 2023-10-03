package com.ngontro86.cloud.publisher


import com.google.api.gax.retrying.RetrySettings
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.TopicName
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import org.apache.logging.log4j.Logger
import org.threeten.bp.Duration

import javax.annotation.PostConstruct
import java.util.concurrent.*


class GoogleCloudPublisher implements CloudPublisher{

    @Logging
    private Logger logger

    @ConfigValue(config = "projectId")
    private String projectId

    @ConfigValue(config = "topicId")
    private String topicId

    private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>()

    private ExecutorService executorService = Executors.newSingleThreadExecutor()

    private TopicName topicName
    private Publisher publisher

    private int totalMsgCnt, pendingMsgCnt

    @PostConstruct
    private void init() {
        executorService.submit(new Runnable() {
            @Override
            void run() {
                while (true) {
                    try {
                        publishOneMsg(queue.take())
                    } catch (Exception e) {
                        logger.error("Exception caught: ${e}. Shutting down.")
                        shutDown()
                    }
                }
            }
        })
        topicName = TopicName.of(projectId, topicId)
        logger.info("Topic name: ${topicName.toString()}")
        def retrySettings = RetrySettings.newBuilder()
                .setInitialRetryDelay(Duration.ofMillis(100))
                .setRetryDelayMultiplier(2.0)
                .setMaxRetryDelay(Duration.ofSeconds(60))
                .setInitialRpcTimeout(Duration.ofSeconds(1))
                .setRpcTimeoutMultiplier(1.0)
                .setMaxRpcTimeout(Duration.ofSeconds(600))
                .setTotalTimeout(Duration.ofSeconds(600))
                .build()
        publisher = Publisher.newBuilder(topicName).setRetrySettings(retrySettings).build()
        logger.info("CLoudPublisher started...")
    }

    void shutDown() {
        executorService.shutdown()
        if (publisher != null) {
            publisher.shutdown()
            publisher.awaitTermination(1, TimeUnit.MINUTES)
        }
    }

    void add(Object newMsg) {
        queue.put(newMsg)
        totalMsgCnt++
        pendingMsgCnt++
        logger.info("Add one msg. Total Msg Count:${totalMsgCnt}, Pending in Queue: ${pendingMsgCnt}")
    }

    private void publishOneMsg(Object msg) throws IOException, ExecutionException, InterruptedException {
        def data = ByteString.copyFromUtf8(msg.toString())
        def message = PubsubMessage.newBuilder().setData(data).build()
        String messageId = publisher.publish(message).get()
        pendingMsgCnt--
        logger.info("Published message id: ${messageId}. Total msg:${totalMsgCnt}, pending: ${pendingMsgCnt}")
    }
}
