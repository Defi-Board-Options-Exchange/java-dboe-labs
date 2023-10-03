package com.ngontro86.server.integration

import com.ngontro86.app.common.db.DataSourceProvider
import com.ngontro86.app.common.db.FlatDao
import com.ngontro86.app.common.postprocessor.ConfigValuePostProcessor
import com.ngontro86.app.common.postprocessor.LoggerPostProcessor
import com.ngontro86.component.testing.ComponentEnv
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class MarketTimeFilling {

    ComponentEnv env

    FlatDao flatDao

    @Before
    void init() {
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl")
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        System.setProperty("user.timezone", "Asia/Singapore")

        [
                'datasource.host'    : 'localhost',
                'datasource.name'    : 'ngontro86',
                'datasource.username': 'user_rw',
                'datasource.password': '251120Vi'
        ].each { k, v -> System.setProperty(k, v) }

        env = ComponentEnv.env([ConfigValuePostProcessor, LoggerPostProcessor, DataSourceProvider])
        flatDao = env.component(FlatDao)
    }

    @Test
    void "should fill in missing time periods"() {
        def marketTimes = flatDao.queryList("select * from market_times")
        def missingPeriods = []
        marketTimes.groupBy { it['underlying'] }.each { underlying, listOfPeriods ->
            def startingTimes = []
            def endingTimes = []
            listOfPeriods.sort { it['starting_time'] }.each { onePeriod ->
                startingTimes << onePeriod['starting_time']
                endingTimes << onePeriod['ending_time']
            }
            int xIndex = 0
            startingTimes.eachWithIndex { time, idx ->
                if (idx == 0 && time != 0) {
                    missingPeriods << [
                            'underlying'   : underlying,
                            'state'        : "X-${xIndex++}".toString(),
                            'starting_time': 0,
                            'ending_time'  : time
                    ]
                }
                if (idx < startingTimes.size() - 1 && startingTimes.get(idx + 1) > endingTimes.get(idx)) {
                    missingPeriods << [
                            'underlying'   : underlying,
                            'state'        : "X-${xIndex++}".toString(),
                            'starting_time': endingTimes.get(idx) + 1,
                            'ending_time'  : startingTimes.get(idx + 1)
                    ]
                }
                if (idx == startingTimes.size() - 1 && endingTimes.get(idx) < 235959) {
                    missingPeriods << [
                            'underlying'   : underlying,
                            'state'        : "X-${xIndex++}".toString(),
                            'starting_time': endingTimes.get(idx) + 1,
                            'ending_time'  : 235959
                    ]
                }
            }
        }
        missingPeriods = missingPeriods.findAll { it['ending_time'] > it['starting_time'] }
        missingPeriods.each {
            println  it
        }
        flatDao.persist('market_times', missingPeriods)
        println "Done persistence"
    }

}
