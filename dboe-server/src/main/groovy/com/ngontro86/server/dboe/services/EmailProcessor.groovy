package com.ngontro86.server.dboe.services

import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.NonTxTransactional
import com.ngontro86.common.config.MaskedConfig
import com.ngontro86.common.email.Emailer
import com.ngontro86.restful.common.json.JsonUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.*

import static com.ngontro86.common.times.GlobalTimeController.currentTimeMillis
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat

@DBOEComponent
class EmailProcessor {

    @Logging
    private Logger logger

    @ConfigValue(config = "emailReloadMin")
    private Integer emailReloadMin = 45

    @ConfigValue(config = "persistFreqMin")
    private Integer persistFreqMin = 10

    @ConfigValue(config = "fromEmail")
    private String fromEmail = 'inbox@dboe.io'

    private Collection<Emailer> emailers
    private Emailer workingEmailer
    private Map<String, Map> emailTemplates = [:]

    @Inject
    @NonTxTransactional
    private FlatDao flatDao

    private BlockingQueue<EmailingRequest> q = new LinkedBlockingDeque<>()

    private Map<String, Map> requestToPersist = [:]

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3)

    @PostConstruct
    private void init() {
        initEmailers()

        executorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    void run() {
                        persist()
                    }
                }, 5, persistFreqMin, TimeUnit.MINUTES)

        executorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    void run() {
                        initEmailers()
                    }
                }, 1, emailReloadMin, TimeUnit.MINUTES
        )

        executorService.schedule(new Runnable() {
            @Override
            void run() {
                continuouslyProcessEmails()
            }
        }, 5, TimeUnit.MINUTES)

    }

    void email(String template, String toEmail, Map params) {
        if (emailTemplates.containsKey(template)) {
            def req = new EmailingRequest(template: template, toEmail: toEmail, params: params)
            if (!requestToPersist.containsKey(req.key())) {
                q.put(req)
                requestToPersist.put(req.key(), req.toMap('NA'))
            }
        }
    }

    private void persist() {
        logger.info("Persisting ${requestToPersist.size()} items into DB...")
        if (requestToPersist.size() > 0) {
            def clonedList = []
            clonedList.addAll(requestToPersist.values())
            requestToPersist.clear()
            flatDao.persist('dboe_email_records', clonedList)
        }
    }

    private void initEmailers() {
        try {
            emailers = flatDao.queryList("select smtp, port, user, hashedPassword from dboe_static_email_senders where active=1").collect { map ->
                new Emailer(host: map['smtp'], port: map['port'].toString(), username: map['user'], password: MaskedConfig.newInstance().setHashedValue(map['hashedPassword']).build().unmaskedValue)
            }
            pickRandomEmailer()

            emailTemplates = flatDao.queryList("select * from dboe_static_email_templates").collectEntries {
                [(it['name']): it]
            }
        } catch (Exception e) {
            logger.error(e)
        }

    }

    private void pickRandomEmailer() {
        Collections.shuffle(emailers)
        workingEmailer = emailers.get(0)
        logger.info("Picked email: ${workingEmailer.username}")
    }

    private void continuouslyProcessEmails() {
        while (true) {
            try {
                try {
                    def req = q.take()
                    def templateInfo = emailTemplates.get(req.template)
                    workingEmailer.sendMessage(fromEmail, [req.toEmail], templateInfo['subject'], Utils.body(templateInfo['content'], '##', req.params))
                    requestToPersist.put(req.key(), req.toMap(workingEmailer.username))
                } catch (Exception e) {
                    logger.error("Error process, email ${workingEmailer.username}: ${e}")
                    pickRandomEmailer()
                }
            } catch (Exception e) {
                logger.error(e)
            }
        }
    }

    private static class EmailingRequest {
        String template
        String toEmail
        Map params

        String key() {
            return "${template}${toEmail}"
        }

        Map toMap(String sender) {
            [
                    'template' : template,
                    'to_email' : toEmail,
                    'sent'     : sender,
                    'params'   : JsonUtils.toJson(params),
                    'timestamp': getTimeFormat(currentTimeMillis, 'yyyyMMddHHmmss')
            ]
        }
    }
}
