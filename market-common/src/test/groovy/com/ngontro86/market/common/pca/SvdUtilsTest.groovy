package com.ngontro86.market.common.pca

import org.ejml.data.DenseMatrix64F
import org.junit.Test


class SvdUtilsTest {

    @Test
    void "should decompose 1"() {
        def ip = new DenseMatrix64F(2, 3)
        [3, 1, 1].eachWithIndex { val, idx ->
            ip.set(0, idx, val)
        }
        [-1, 3, 1].eachWithIndex{ val, idx->
            ip.set(1, idx, val)
        }
        def svd = SvdUtils.svd(2, 3, ip)
        assert svd.singularValues == [3.4641016151377553, 3.1622776601683804]
    }

}
