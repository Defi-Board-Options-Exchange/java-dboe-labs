package com.ngontro86.utils

import org.junit.Test

import static EqualUtils.equals
import static EqualUtils.equalsWithNull


class EqualUtilsTest {

    @Test
    void "should compare nullable objects"() {
        assert !equalsWithNull(null, 1)
        assert !equalsWithNull(1, null)
        assert equalsWithNull(null, null)
        assert equalsWithNull('A', 'A')
    }

    @Test
    void "should compare double with precision"() {
        assert equals(16.666, 16.667, 0.01)
        assert !equals(16.666, 16.667, 0.001)
    }

    @Test
    void "should compare two collections"() {
        assert equals([1, 2], [2, 1])
        assert !equals([1, 2], [2, 1, 3])
    }

}
