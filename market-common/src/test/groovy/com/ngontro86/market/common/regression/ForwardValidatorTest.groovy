package com.ngontro86.market.common.regression

import com.ngontro86.common.config.Configuration
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import java.nio.file.Paths

@Ignore
class ForwardValidatorTest {

    @Before
    void init() {
        Configuration.setConfigs(['market-common-config.cfg'] as Set)
    }

    @Test
    void "should read params file"() {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("RegParams.csv");
        final File file = Paths.get(url.toURI()).toFile()
        def validator = new ForwardValidator(file, 0.0001d)
        println validator.avgRegParamDateMap
        assert validator.avgRegParamDateMap == [
                0       : [-0.043839438499999994, 0.003209985, -0.0178727425, 0.0190109185],
                20131003: [-0.0665907355, -0.003290018499999999, -0.0011237569999999995, -0.010858082500000001]
        ]
    }

    @Test
    void "should estimate"() {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("RegParams.csv");
        final File file = Paths.get(url.toURI()).toFile()
        def validator = new ForwardValidator(file, 0.0001d)
        assert validator.est(20131003, [1, 1, 1]) == -0.08186259350000001

        assert validator.est([1, 1, 1]) == validator.est(0, [1, 1, 1])
    }

}
