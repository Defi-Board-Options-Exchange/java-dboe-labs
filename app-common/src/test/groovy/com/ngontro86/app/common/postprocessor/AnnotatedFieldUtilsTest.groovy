package com.ngontro86.app.common.postprocessor

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import org.apache.logging.log4j.Logger
import org.junit.Test

import static com.ngontro86.app.common.postprocessor.AnnotatedFieldUtils.getConfigFields
import static com.ngontro86.app.common.postprocessor.AnnotatedFieldUtils.getLoggerField


class AnnotatedFieldUtilsTest {
    @Test
    void "should detect config value prop"() {
        assert getConfigFields(new TestBean()).size() == 1
    }

    @Test
    void "should set logger"() {
        assert getLoggerField(new TestBean2()) != null
    }

    class TestBean {
        @ConfigValue
        private Integer testProp
    }

    class TestBean2 {
        @Logging
        private Logger logger
    }

}
