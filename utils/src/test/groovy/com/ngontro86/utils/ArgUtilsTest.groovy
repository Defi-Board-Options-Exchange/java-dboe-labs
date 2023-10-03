package com.ngontro86.utils

import org.junit.Test

import static ArgUtils.getArgMap
import static ArgUtils.getWhereCondition


class ArgUtilsTest {

    @Test
    void "should get argument map"() {
        assert getArgMap('key1:value1,key2:value2') ==
                [
                        'key1': 'value1',
                        'key2': 'value2'
                ]
    }

    @Test
    void "should get where condition clause for String"() {
        assert getWhereCondition(['A', 'B']) == " ('A','B') "
    }

    @Test
    void "should get where condition clause for non String"() {
        assert getWhereCondition([1, 2]) == " (1,2) "
    }
}
