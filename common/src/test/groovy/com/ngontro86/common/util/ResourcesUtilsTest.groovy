package com.ngontro86.common.util

import org.junit.Test

import static com.ngontro86.common.util.ResourcesUtils.content
import static com.ngontro86.common.util.ResourcesUtils.listResources

class ResourcesUtilsTest {

    @Test
    void "should list files"() {
        assert listResources('testDir') == ["testDir\\oneResource", "testDir\\subDir\\secondResource"]
    }

    @Test
    void "should read content from a resource"() {
        println  content("testConfig")
    }


}
