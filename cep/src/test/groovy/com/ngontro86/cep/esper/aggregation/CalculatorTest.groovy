package com.ngontro86.cep.esper.aggregation

import org.junit.Test

import static com.ngontro86.cep.esper.aggregation.Calculator.imbalance
import static com.ngontro86.utils.EqualUtils.equals

class CalculatorTest {

    @Test
    void "should calculate imbalance"() {
        assert equals(imbalance(10, 5), 16.6667, 0.0001)

        assert equals(imbalance(20, 5), 30.0, 0.0001)

        assert equals(imbalance(25, 5), 33.3333, 0.0001)

        assert equals(imbalance(5, 25), -33.3333, 0.0001)
    }

    @Test
    void "should find gcd"() {
        assert Calculator.gcd(12, 8) == 4
    }

    @Test
    void "should run stats"() {
        println Calculator.stats([2.0d, -3.0d, 7.0d, -5.0d, 9.0d] as Double[])
    }

    @Test
    void "should run corr"() {
        println Calculator.corr([
                [
                        'firstRet' : 0.1d,
                        'secondRet' : 0.2d
                ],
                [
                        'firstRet' : 0.15d,
                        'secondRet' : 0.25d
                ],
                [
                        'firstRet' : 0.07d,
                        'secondRet' : 0.16d
                ],
                [
                        'firstRet' : 0.12d,
                        'secondRet' : 0.22d
                ],
                [
                        'firstRet' : -0.15d,
                        'secondRet' : -0.2d
                ],
                [
                        'firstRet' : -0.3d,
                        'secondRet' : -0.9d
                ],
                [
                        'firstRet' : 0.3d,
                        'secondRet' : 0.8d
                ],
                [
                        'firstRet' : 0.25d,
                        'secondRet' : 0.4d
                ],
                [
                        'firstRet' : 0.6d,
                        'secondRet' : 1.0d
                ],
                [
                        'firstRet' : -0.3d,
                        'secondRet' : -0.9d
                ],
        ] as Map[])
    }


    @Test
    void "should run stdev"() {
        println Calculator.stdev([2.0d, -3.0d, 7.0d, -5.0d, 9.0d] as Double[])
    }

}
