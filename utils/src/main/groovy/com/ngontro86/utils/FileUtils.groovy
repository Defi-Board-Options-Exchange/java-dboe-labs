package com.ngontro86.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class FileUtils {
    private static Logger logger = LogManager.getLogger(FileUtils)

    static Collection<String> lines(String name) {
        try {
            return new File(name).readLines()
        } catch (Exception e) {
            logger.error("Error while reading: ${name} as file.", e)
            return Collections.emptyList()
        }
    }

}
