package com.ngontro86.cep.esper.aggregation

import org.junit.Test


class PrevDoubleTest {

    @Test
    void "should find out the previous value 1"() {
        def pd = new PrevDouble()
        pd.enter(1000.0)
        pd.enter(999.0)
        assert pd.prev == 1000.0

        pd.enter(new BigDecimal('1001.0'))
        assert pd.prev == 999.0
    }
    @Test
    void "check BigDecminal is assignable to Double"() {
        def bg = BigDecimal.valueOf(1000.0)
        assert !bg.class.isAssignableFrom(Double.class)
    }


}
