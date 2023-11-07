package com.ngontro86.server.dboe.volsurface

import com.ngontro86.restful.common.json.JsonUtils
import com.ngontro86.utils.ResourcesUtils
import org.junit.Test

class VolSurfaceHelperTest {

    @Test
    void "should smoothen vol"() {
        def vols = JsonUtils.fromJson(ResourcesUtils.content('impliedVol.txt'), Collection) as Collection<Map>
        vols.each { println it }
        VolSurfaceHelper.smoothenVols(vols)
        println "Done smoothen..."
        vols.each { println it }
    }

}
