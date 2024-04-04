package com.ngontro86.server.dboe.volsurface

import com.ngontro86.restful.common.json.JsonUtils
import com.ngontro86.utils.ResourcesUtils
import org.junit.Test

class VolSurfaceHelperTest {

    @Test
    void "should smoothen vol"() {
        def vols = JsonUtils.fromJson(ResourcesUtils.content('impliedVol.txt'), Collection) as Collection<Map>
        VolSurfaceHelper.implyVols(vols)
        VolSurfaceHelper.smoothenVols(vols)
        VolSurfaceHelper.updateGreek(vols)
        println "Done smoothen..."
        assert vols.find { it['instr_id'] == 'E2050C112' }['ref_iv'] == 90.38010165854116
        vols.each { println it }
    }

}
