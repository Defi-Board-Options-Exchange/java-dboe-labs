package com.ngontro86.market.common.reg

import com.ngontro86.market.common.NormalizeMethod
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.ejml.data.DenseMatrix64F
import org.ejml.ops.CommonOps

import java.text.DecimalFormat

class LinearReg {
    private int window

    private String yFactor
    private Collection<String> factors

    private int row
    DenseMatrix64F x
    DenseMatrix64F y
    DenseMatrix64F beta
    Map betaAsMap

    double yMean
    double yStdev
    double[] xMean
    double[] xStdevs
    DenseMatrix64F xMeanMatrix
    DenseMatrix64F xStdevMatrix


    NormalizeMethod yNormalizeMethod = NormalizeMethod.LOG_RET
    NormalizeMethod xNormalizeMethod = NormalizeMethod.RAW

    LinearReg init() {
        x = new DenseMatrix64F(window, factors.size())
        y = new DenseMatrix64F(window, 1)

        xMean = new double[factors.size()]
        xStdevs = new double[factors.size()]

        beta = new DenseMatrix64F(factors.size(), 1)
        betaAsMap = [:].withDefault { Double.NaN }
        return this
    }

    LinearReg addData(Map data) {
        if (row > window) {
            println "Exceeding data window ${row}, ${window}. Run regression now and reset. Ignoring now..."
            return this
        }

        factors.eachWithIndex { name, idx ->
            if (!data.containsKey(name)) {
                throw new IllegalStateException("Invalid factor: ${name} from sample data")
            }
            x.get(row, idx, data.get(name))
        }
        y.set(row, 0, data.get(yFactor))
        row++
        return this
    }

    double estimate(Map data) {
        def inputMatrix = new DenseMatrix64F(1, factors.size())
        factors.eachWithIndex { name, idx ->
            if (!data.containsKey(name)) {
                throw new IllegalArgumentException("Sample data does not contain factor:${name}")
            }
            inputMatrix.set(0, idx, data.get(name))
        }
        if (xNormalizeMethod == NormalizeMethod.LOG_RET || xNormalizeMethod == NormalizeMethod.Z_SCORE) {
            CommonOps.subtract(inputMatrix, xMeanMatrix, inputMatrix)
        }

        if (xNormalizeMethod == NormalizeMethod.LOG_RET) {
            CommonOps.elementDiv(inputMatrix, xMeanMatrix, inputMatrix)
        } else if (xNormalizeMethod == NormalizeMethod.Z_SCORE) {
            CommonOps.elementDiv(inputMatrix, xStdevMatrix, inputMatrix)
        }
        def res = new DenseMatrix64F(1, 1)
        CommonOps.mult(inputMatrix, beta, res)
        if (yNormalizeMethod == NormalizeMethod.Z_SCORE) {
            return res.data[0] * yStdev + yMean
        } else if (yNormalizeMethod == NormalizeMethod.LOG_RET) {
            return res.data[0] * yMean + yMean
        }
        return res.data[0]
    }

    LinearReg calculateStats() {
        def xDescriptiveStatistics = new DescriptiveStatistics[factors.size()]
        def yDescriptiveStatistic = new DescriptiveStatistics()
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < xDescriptiveStatistics.length; j++) {
                if (xDescriptiveStatistics[j] == null) {
                    xDescriptiveStatistics[j] = new DescriptiveStatistics()
                }
                xDescriptiveStatistics[j].addValue(x.get(i, j))
            }
            yDescriptiveStatistic.addValue(y.data[i])
        }

        xMean = xDescriptiveStatistics.collect { it.mean } as double[]
        xStdevs = xDescriptiveStatistics.collect { it.standardDeviation } as double[]
        xMeanMatrix = DenseMatrix64F.wrap(1, xMean.length, xMean)
        xStdevMatrix = DenseMatrix64F.wrap(1, xMean.length, xStdevs)

        yMean = yDescriptiveStatistic.mean
        yStdev = yDescriptiveStatistic.standardDeviation
        return this
    }

    LinearReg normalize() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < xMean.length; j++) {
                x.set(i, j, normX(x.get(i, j), j))
            }
            y.set(i, 0, normY(y.get(i, 0)))
        }
    }

    double normX(double val, int factorIdx) {
        if (xNormalizeMethod == NormalizeMethod.RAW) {
            return val
        }
        def denom = xNormalizeMethod == NormalizeMethod.Z_SCORE ?
                xStdevs[factorIdx] :
                (xNormalizeMethod == NormalizeMethod.LOG_RET ? xMean[factorIdx] : 1d)
        return (val - xMean[factorIdx]) / denom
    }

    double normY(double val) {
        if (yNormalizeMethod == NormalizeMethod.RAW) {
            return val
        }
        def denom = yNormalizeMethod == NormalizeMethod.Z_SCORE ?
                yStdev :
                (yNormalizeMethod == NormalizeMethod.LOG_RET ? yMean : 1d)
        return (val - yMean) / denom
    }

    LinearReg calculateBeta() {
        calculateStats().normalize()
        def pInvX = new DenseMatrix64F(factors.size(), window)
        CommonOps.pinv(x, pInvX)
        CommonOps.mult(pInvX, y, beta)
        formBetaAsMap()
        return this
    }

    void formBetaAsMap() {
        def x = new DecimalFormat(".###")
        beta.data.eachWithIndex { val, idx ->
            betaAsMap.put(factors[idx], Double.valueOf(x.format(val)))
        }
    }

    Map getBetaAsMap(double min) {
        return betaAsMap.findAll { Math.abs(it.value) >= min }
    }

}
