package com.ngontro86.server.reporter

import com.ngontro86.cep.CepEngine
import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.config.MaskedConfig
import com.ngontro86.common.email.Emailer
import com.ngontro86.common.timer.TaskScheduler
import com.ngontro86.common.times.GlobalTimeController
import com.ngontro86.common.util.IOUtils
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.ngontro86.server.reporter.EmailReporterHtmlUtils.pnlHtml
import static com.ngontro86.server.reporter.EmailReporterHtmlUtils.positionHtml
import static com.ngontro86.utils.CollectionUtils.isNotEmpty
import static com.ngontro86.utils.GlobalTimeUtils.getTimeFormat

@Lazy(false)
@DBOEComponent
class EmailReporter {

    @Logging
    private Logger logger

    @Inject
    private CepEngine cep

    @ConfigValue(config = "pnlEmailEnabled")
    private Boolean emailEnabled = true

    @ConfigValue(config = "emailSmtpHost")
    private String smtpHost

    @ConfigValue(config = "emailSmtpPort")
    private Integer smtpPort

    @ConfigValue(config = "emailSmtpUser")
    private String smtpUser

    @ConfigValue(config = "emailSmtpPassword")
    private MaskedConfig smtpPassword

    @ConfigValue
    private Integer startingReport = 80000

    @ConfigValue
    private Integer endingReport = 235900

    @ConfigValue
    private Integer reportFrequencyMin = 5

    @ConfigValue
    private Collection<String> recipientEmails

    @Inject
    private TaskScheduler taskScheduler

    private TimerTask task = new TimerTask() {
        @Override
        void run() {
            snapshotAndReport()
        }
    }

    private Date endingReportTime

    private String previousEmailStr

    @PostConstruct
    private void start() {
        endingReportTime = IOUtils.getDateFromTime(0, endingReport)
        taskScheduler.scheduleAtFixedRate(task, IOUtils.getDateFromTime(0, startingReport), reportFrequencyMin * 60 * 1000)

    }

    void snapshotAndReport() {
        if (emailEnabled) {
            reportPositionAndPnL()
        }
        cancelTimerIfPossible()
    }

    void reportPositionAndPnL() {
        List<Map> pnls = cep.queryMap("select * from PLWin order by broker, account, portfolio, inst_id, slice")
        def sb = new StringBuilder()
        if (isNotEmpty(pnls)) {
            sb.append(pnlHtml(pnls))
        }
        List<Map> positions = cep.queryMap("select * from PortfolioWin order by broker, account, portfolio, inst_id")
        if (isNotEmpty(positions)) {
            sb.append(positionHtml(positions))
        }
        if (sb.length() > 10 && !sb.toString().equals(previousEmailStr)) {
            def mailer = new Emailer(host: smtpHost, port: smtpPort, username: smtpUser, password: smtpPassword.unmaskedValue)
            mailer.sendMessage(recipientEmails, "Trading Report: ${getTimeFormat(endingReportTime.time, 'yyyyMMdd')}", sb.toString())
            previousEmailStr = sb.toString()
        }
    }

    void cancelTimerIfPossible() {
        if (GlobalTimeController.currentTimeMillis >= endingReportTime.time) {
            logger.info("Too late already! Cancelling timer task and get some sleep!")
            task.cancel()
        }
    }

}
