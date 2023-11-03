package com.ngontro86.dboe.analytic.rest

import org.junit.Test

class QueryUtilTest {

    @Test
    void "should form a valid SQL"() {
        println  QueryUtil.constructQuery("select * from polygon.token_transfers where transaction_hash in", ['0x48b54ca73616cc3416f68550350f15fe9c7af4ba58ed5b0c85a40312cd6ed06c', '0x21b7d7ee7a7acf34464cd0287230fd3a9897a1342da8eeaec77500c841f8bcfa', '0x8bd8663a1c233824a37048607887112da42a4865291254114f39cd74689fff7f'])
    }

    @Test
    void "should convert from hexa to long"() {
        assert QueryUtil.hexToLong('0x0000000000000000000000000000000000000000000000000000000000004c34') == 19508
        assert QueryUtil.hexToLong('0x00000000000000000000000000000000000000000000000003782dace9d90000') == 250000000000000000
    }

    @Test
    void "should trim hex address"() {
        assert QueryUtil.trimHexAddress('0x0000000000000000000000000000000000000000') == '0x0000000000000000000000000000000000000000'
        assert QueryUtil.trimHexAddress('0xec85c29e2d8fe910714f98a02c30dc7c9effcfb2') == '0xec85c29e2d8fe910714f98a02c30dc7c9effcfb2'

        assert QueryUtil.trimHexAddress('0x000000000000000000000000ec85c29e2d8fe910714f98a02c30dc7c9effcfb2') == '0xec85c29e2d8fe910714f98a02c30dc7c9effcfb2'
    }

}
