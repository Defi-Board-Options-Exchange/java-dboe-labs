package com.ngontro86.market.common.pca

import org.ejml.data.DenseMatrix64F
import org.ejml.factory.DecompositionFactory
import org.ejml.interfaces.decomposition.SingularValueDecomposition


class SvdUtils {
    static SingularValueDecomposition<DenseMatrix64F> svd(int row, int col, DenseMatrix64F ip) {
        def svd = DecompositionFactory.svd(row, col, false, true, false)
        if (!svd.decompose(ip)) {
            throw new IllegalStateException("Cannot decompose...")
        }
        return svd
    }

}
