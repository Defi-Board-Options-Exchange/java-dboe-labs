package com.ngontro86.utils

import org.junit.Test

import java.awt.*

import static ColorUtils.getColor

class ColorUtilsTest {

    @Test
    void "should get color"() {
        assert getColor(5, 10, 2) == new Color(159, 255, 159)

        assert getColor(15, 10, 2) == Color.GREEN
        assert getColor(-5, -7, 2) == Color.WHITE

        assert getColor(-3, 10, -2) == Color.RED

        assert getColor(1, 10, 2) == Color.WHITE
    }
}
