package com.ngontro86.utils

import org.junit.Test

import static StringUtils.join


class StringUtilsTest {

    @Test
    void "should join string"() {
        assert join(['1', '2'], ',') == '1,2'

        assert join(['1', '2'], '|') == '1|2'
    }

    @Test
    void "should join string with qualifier"() {
        assert StringUtils.join(['A', 'B'], ",", "'") == "'A','B'"
    }

}
