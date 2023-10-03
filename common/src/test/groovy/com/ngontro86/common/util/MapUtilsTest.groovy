package com.ngontro86.common.util

import org.junit.Test

class MapUtilsTest {

    @Test
    void "should get csv"() {
        assert MapUtils.csv(
                [
                        'a': '1',
                        'b': '2',
                        'c': '3'
                ]
        ) == "'1','2','3'"
    }

    @Test
    void "should get selected csv"() {
        assert MapUtils.csv(
                [
                        'a': '1',
                        'b': '2',
                        'c': '3'
                ],
                ['b', 'c'] as Set
        ) == "'2','3'"
    }

}
