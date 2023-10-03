package com.ngontro86.market.common.regression

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression

import static java.lang.System.arraycopy


class MultiLinearReg {

    @Delegate
    private OLSMultipleLinearRegression olsMultipleLinearRegression

    private List<double[]> xData = new ArrayList<>()
    private List<Double> yData = new ArrayList<>()
    private int numOfDependent
    private String name

    private int observationCount

    MultiLinearReg(String name, int numOfDependent) {
        reset()
        this.numOfDependent = numOfDependent
        this.name = name
    }

    void addData(double[] x, double y) {
        this.xData.add(x)
        this.yData.add(y)
        observationCount++
    }

    void runRegression() {
        final double[][] x = new double[xData.size()][numOfDependent]
        for (int rowIdx = 0; rowIdx < xData.size(); rowIdx++) {
            final double[] rowData = xData.get(rowIdx)
            arraycopy(rowData, 0, x[rowIdx], 0, numOfDependent);
        }

        olsMultipleLinearRegression.newSampleData(yData as double[], x)
    }

    void reset() {
        this.olsMultipleLinearRegression = new OLSMultipleLinearRegression()
        xData.clear()
        yData.clear()
    }

    int getObCount() {
        return observationCount
    }
}
