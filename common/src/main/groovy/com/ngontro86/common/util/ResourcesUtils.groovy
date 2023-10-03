package com.ngontro86.common.util


import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.core.io.ClassPathResource

import java.nio.charset.Charset
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ResourcesUtils {

    private static Logger logger = LogManager.getLogger(ResourcesUtils)

    static Collection<String> listResources(String name) {
        final def fileNames = regularResources(name)
        if (!fileNames.isEmpty()) {
            return fileNames
        }
        resourceLookup(name).each { path ->
            fileNames << path.toString()
        }
        return fileNames
    }

    private static Collection<Path> resourceLookup(String name) {
        def fileNames = []
        def uri = Thread.currentThread().getContextClassLoader().getResource(name).toURI()
        def myPath = uri.getScheme().equals("jar") ?
                FileSystems.newFileSystem(uri, [:]).getPath(name) :
                Paths.get(uri)
        Files.walk(myPath, 10).iterator().each {
            fileNames << it
        }
        return fileNames
    }

    private static Collection<String> regularResources(String name) {
        def fileNames = []
        try {
            final def file = new ClassPathResource(name).file
            if (file.isDirectory()) {
                file.listFiles().each { oneFile ->
                    fileNames.addAll(listResources(name + File.separator + oneFile.getName()))
                }
            } else {
                fileNames.add(name)
            }
        } catch (Exception e) {
            logger.info("Exception: ${e}")
        }
        return fileNames
    }

    static String content(String name) {
        try {
            def is = Thread.currentThread().getContextClassLoader().getClass().getResourceAsStream(name)
            return is.getText(Charset.defaultCharset().toString())
        } catch (Exception e) {
            logger.error("Content of ${name} got exception")
            return null
        }
    }

}
