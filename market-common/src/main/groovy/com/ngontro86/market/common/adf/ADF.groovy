package com.ngontro86.market.common.adf

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix


class ADF {
    private int lag
    private static double PVALUE_THRESHOLD = -3.45d

    double computeADFStatistics(double[] ts) {
        if (lag == 0) {
            lag = (int) Math.floor(Math.cbrt(ts.length - 1))
        }
        double[] y = ADFUtils.diff(ts)
        RealMatrix designMatrix = null
        int k = lag + 1
        int n = ts.length - 1
        def z = MatrixUtils.createRealMatrix(ADFUtils.laggedMatrix(y, k))
        def zcol1 = z.getColumnVector(0)

        double xt1 = ADFUtils.subsetArray(ts, k - 1, n - 1)

        double[] trend = ADFUtils.sequence(k, n)
        if (k > 1) {
            def yt1 = z.getSubMatrix(0, ts.length - 1 - k, 1, k - 1)
            designMatrix = MatrixUtils.createRealMatrix(ts.length - 1 - k + 1, 3 + k - 1)
            designMatrix.setColumn(0, xt1)
            designMatrix.setColumn(1, ADFUtils.ones(ts.length - 1 - k + 1))
            designMatrix.setColumn(2, trend)
            designMatrix.setSubMatrix(yt1.getData(), 0, 3)
        } else {
            designMatrix = MatrixUtils.createRealMatrix(ts.length - 1 - k + 1, 3)
            designMatrix.setColumn(0, xt1)
            designMatrix.setColumn(1, ADFUtils.ones(ts.length - 1 - k + 1))
            designMatrix.setColumn(2, trend)
        }

        def regression = new RidgeRegression(designMatrix.getData(), zcol1.toArray())
        regression.updateCoefficients(0.0001)
        return regression.getCoefficients()[0] / regression.getStandarderrors()[0]

    }

}
