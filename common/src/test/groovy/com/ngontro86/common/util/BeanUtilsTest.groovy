package com.ngontro86.common.util

import org.junit.Test

import static com.ngontro86.common.util.BeanUtils.copyProperties
import static com.ngontro86.common.util.BeanUtils.properties


class BeanUtilsTest {

    @Test
    void "should copy properties 1"() {
        def bean = new BeanA()
        copyProperties(bean, [
                'name': 'Gavin',
                'age' : 1
        ])
        assert bean.age == 1
        assert bean.name == 'Gavin'
    }

    @Test
    void "should copy properties 2"() {
        def bean = new BeanA()
        copyProperties(bean, [
                'name': 'Gavin',
                'age' : '1'
        ])
        assert bean.age == 1
        assert bean.name == 'Gavin'
    }

    @Test
    void "should copy properties 3"() {
        def bean = new BeanA()
        copyProperties(bean,
                [
                        'name'     : 'Gavin',
                        'first_age': '1'
                ],
                ['first_age' : 'age']
        )

        assert bean.age == 1
        assert bean.name == 'Gavin'
    }

    @Test
    void "should get properties 1"() {
        assert properties(new BeanA(name: 'Test', age: 5)) == ['name': 'Test', 'age': '5']
    }

    public class BeanA {
        String name
        int age
    }

}
