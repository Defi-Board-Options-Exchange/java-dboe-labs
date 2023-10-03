package com.ngontro86.market.common.pca

import com.ngontro86.market.common.NormalizeMethod
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.ejml.data.DenseMatrix64F
import org.ejml.ops.CommonOps
import org.ejml.ops.SingularOps

import java.text.DecimalFormat


public class PCA {

    private int window
    private Collection<String> factors
    private NormalizeMethod normalizeMethod = NormalizeMethod.LOG_RET

    private int row
    DenseMatrix64F inp = new DenseMatrix64F()

    double[] mean
    double[] stdevs
    DenseMatrix64F meanTranspose
    DenseMatrix64F stdevTranspose


    private int reduceDim
    DenseMatrix64F pca
    double[] pcaWeights

    public PCA init() {
        mean = new double[factors.size()]
        stdevs = new double[factors.size()]

        inp = new DenseMatrix64F(window, mean.length)
        return this
    }

    public PCA addData(Map data) {
        if (row > window) {
            println "Exceeding data window (${row} and ${window}. Compute PCA and reset. Ignoring now..."
            return this
        }

        factors.eachWithIndex { name, idx ->
            if (!data.containsKey(name)) {
                throw new IllegalArgumentException("Invalid data input. Non exist factor:${name}")
            }
            inp.set(row, idx, data[name])
        }
        row++
        return this
    }

    public PCA computeBasis(int reducedDim) {
        this.reduceDim = reducedDim
        calculateStats().normalize()

        def svd = SvdUtils.svd(row, mean.length, inp)
        pca = svd.getV(null, true)
        def w = svd.getW(null)
        pcaWeights = pcaWeights(svd.singularValues)
        SingularOps.descendingOrder(null, false, w, pca, true)
        pca.reshape(reduceDim, mean.length, true)
    }

    public Map getBasisVectorAsMap(int which, double min) {
        def bv = getBasisVector(which)
        def totalAbsWeight = bv.collect { Math.abs(it) }.sum()
        def ret = [:]
        for (int idx = 0; idx < bv.length; idx++) {
            if (Math.abs(bv[idx]) / totalAbsWeight > min) {
                ret[factors[idx]] = String.format(".3f", bv[idx])
            }
        }
        return ret
    }

    public double[] getBasisVector(int which) {
        if (which < 0 || which >= mean.length) {
            throw new IllegalArgumentException("Invalid argument ${which}")
        }
        def v = new DenseMatrix64F(1, mean.length)
        CommonOps.extract(pca, which, which + 1, 0, mean.length, v, 0, 0)
        return v.data
    }

    private DenseMatrix64F filterPca(double min) {
        def filterPca = new DenseMatrix64F(reduceDim, mean.length)
        for (int row = 0; row < reduceDim; row++) {
            double totalWeight = 0d
            for (int col = 0; col < mean.length; col++) {
                totalWeight += Math.abs(pca.get(row, col))
            }
            for (int col = 0; col < mean.length; col++) {
                if (Math.abs(pca.get(row, col)) >= min * totalWeight) {
                    filterPca.set(row, col, pca.get(row, col))
                } else {
                    filterPca.set(row, col, 0d)
                }
            }
        }
        return filterPca
    }

    public double[] sampleToEigenSpace(Map sampleData, double min) {
        def ip = factors.collect { name ->
            if (!sampleData.containsKey(name)) {
                throw new IllegalArgumentException("invalid factor: ${name} from sample data")
            }
            sampleData[name]
        }.toArray(new double[0])

        def s = new DenseMatrix64F(mean.length, 1, true, ip)
        CommonOps.subtract(s, meanTranspose, s)

        if (normalizeMethod == NormalizeMethod.LOG_RET) {
            CommonOps.elementDiv(s, meanTranspose, s)
        } else if (normalizeMethod == NormalizeMethod.Z_SCORE) {
            CommonOps.elementDiv(s, stdevTranspose, s)
        }

        def r = new DenseMatrix64F(reduceDim, 1)
        CommonOps.mult(filterPca(min), s, r)
        return r.data
    }

    public double[] sampleToEigenSpace(Map sampleData) {
        return sampleToEigenSpace(sampleData, 0d)
    }

    private double[] pcaWeights(double[] singulars) {
        def format = new DecimalFormat('.####')
        final double singularSum = singulars.sum()
        return singulars.collect { it -> Double.valueOf(format.format(100d * it / singularSum)) }
    }

    private PCA calculateStats() {
        def descriptiveStatistics = new DescriptiveStatistics[factors.size()]
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < descriptiveStatistics.length; j++) {
                if (descriptiveStatistics[j] == null) {
                    descriptiveStatistics[j] = new DescriptiveStatistics()
                }
                descriptiveStatistics[j].addValue(inp.get(i, j))
            }
        }

        mean = descriptiveStatistics.collect { it.mean } as double[]
        stdevs = descriptiveStatistics.collect { it.standardDeviation } as double[]
        meanTranspose = DenseMatrix64F.wrap(mean.length, 1, mean)
        stdevTranspose = DenseMatrix64F.wrap(mean.length, 1, stdevs)
        return this
    }

    private PCA normalize() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < mean.length; j++) {
                inp.set(i, j, normalize(inp.get(i, j), j))
            }
        }
        return this
    }

    private double normalize(double val, int factorIdx) {
        def denom = normalizeMethod == NormalizeMethod.Z_SCORE ?
                stdevs[factorIdx] :
                (normalizeMethod == NormalizeMethod.LOG_RET ? mean[factorIdx] : 1d)
        return (val - mean[factorIdx]) / denom
    }

    NormalizeMethod getNormalizeMethod() {
        return normalizeMethod
    }
}
