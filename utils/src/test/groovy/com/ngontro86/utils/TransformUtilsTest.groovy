package com.ngontro86.utils

import org.junit.Test


class TransformUtilsTest {

    @Test
    void "should run snapshot 1"() {
        def series = TransformUtils.series([
                [
                        'timestamp': 10,
                        'instId'   : 'I1',
                        'price'    : 10d
                ],
                [
                        'timestamp': 10,
                        'instId'   : 'I2',
                        'price'    : 91d
                ],
                [
                        'timestamp': 11,
                        'instId'   : 'I1',
                        'price'    : 10.5d
                ]
        ], 'instId', 'timestamp')

        assert series['I1'] == [
                10: [

                        'timestamp': 10,
                        'instId'   : 'I1',
                        'price'    : 10d

                ],
                11: [
                        'timestamp': 11,
                        'instId'   : 'I1',
                        'price'    : 10.5d
                ]
        ]

        assert series['I2'] == [
                10: [
                        'timestamp': 10,
                        'instId'   : 'I2',
                        'price'    : 91d
                ]
        ]
    }

    @Test
    void "should find nearest"() {
        assert TransformUtils.nearest(
                [
                        10L: [
                                'price': 10d
                        ],
                        11L: [
                                'price': 11d
                        ],
                        12L: [
                                'price': 12d
                        ]
                ],
                10L) == ['price': 10d]

        assert TransformUtils.nearest(
                [
                        10L: [
                                'price': 10d
                        ],
                        11L: [
                                'price': 11d
                        ],
                        12L: [
                                'price': 12d
                        ]
                ],
                9L) == ['price': 10d]

    }

    @Test
    void "should sampling as of timestamp 1"() {
        assert TransformUtils.samplingAsOfTimestamp([
                [
                        'timestamp': 10L,
                        'instId'   : 'I1',
                        'price'    : 10d
                ],
                [
                        'timestamp': 10L,
                        'instId'   : 'I2',
                        'price'    : 91d
                ],
                [
                        'timestamp': 11L,
                        'instId'   : 'I1',
                        'price'    : 10.5d
                ]
        ], 'instId', 'timestamp', 'price', [10L, 11L]) == [
                10L: ['I1': 10d, 'I2': 91d],
                11L: ['I1': 10.5d, 'I2': 91d]
        ]
    }

}
