package com.ngontro86.utils

import org.junit.Test

import static TransformUtils.transform


class TransformUtilTest {

    @Test
    void "should transform"() {
        assert transform(
                [
                        [
                                'x': 'key1',
                                'y': 'A'
                        ],
                        [
                                'x': 'key2',
                                'y': 'B'
                        ]
                ], 'x') ==
                [
                        'key1': [
                                'x': 'key1',
                                'y': 'A'
                        ],
                        'key2': [
                                'x': 'key2',
                                'y': 'B'
                        ]
                ]
    }

}
