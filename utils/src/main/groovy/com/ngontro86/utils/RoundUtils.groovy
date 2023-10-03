package com.ngontro86.utils

class RoundUtils {

    static double roundToNearestStep(double x, double step) {
        return Math.round(x/step) * step
    }

}
