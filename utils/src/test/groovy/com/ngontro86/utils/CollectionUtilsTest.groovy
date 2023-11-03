package com.ngontro86.utils

import org.junit.Test

import static CollectionUtils.split
import static com.ngontro86.utils.CollectionUtils.isEmpty
import static com.ngontro86.utils.CollectionUtils.isNotEmpty


class CollectionUtilsTest {

    @Test
    void "should split collection"() {
        assert split([1, 2, 4], 2) == [[1, 2], [4]]

        assert split([1, 2, 4], 5) == [[1, 2, 4]]

        assert split([1, 2, 4], 1) == [[1], [2], [4]]
    }

    @Test
    void "should join a collection"() {
        assert CollectionUtils.join(['A', 'B', 'C'], ',') == 'A,B,C'
        assert CollectionUtils.join(['A', 'B', 'C'], "'",',') == "'A','B','C'"
    }

    @Test
    void "should know empty"() {
        assert isEmpty(null)
        assert isEmpty([])
        assert isNotEmpty([1])
    }

}
