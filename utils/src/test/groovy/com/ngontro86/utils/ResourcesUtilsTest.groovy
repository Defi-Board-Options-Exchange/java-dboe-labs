package com.ngontro86.utils

import org.junit.Test

import static ResourcesUtils.content
import static ResourcesUtils.listResources
import static com.ngontro86.utils.ResourcesUtils.lines

class ResourcesUtilsTest {

    @Test
    void "should load resource content"() {
        assert content('testResource') == 'one'
    }

    @Test
    void "should load resource content lines"() {
        assert lines('testResource') == ['one']
    }

    @Test
    void "should list files"() {
        assert listResources('testDir') == ["testDir${File.separator}oneResource", "testDir${File.separator}subDir${File.separator}secondResource"]
    }

}
