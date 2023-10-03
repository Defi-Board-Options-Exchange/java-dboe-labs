package com.ngontro86.utils;

import com.google.common.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ResourcesUtils {
    private static Logger logger = LogManager.getLogger(ResourcesUtils.class);

    public static String content(String name) {
        try {
            return Resources.toString(Resources.getResource(name), Charset.defaultCharset());
        } catch (Exception e) {
            logger.error(String.format("Content of %s got exception", name), e);
            return null;
        }
    }

    public static Collection<String> lines(String name) {
        try {
            return Resources.readLines(Resources.getResource(name), Charset.defaultCharset());
        } catch (Exception e1) {
            logger.error("Error while reading " + name + " as resource. Will continue as file");
            try {
                return FileUtils.lines(name);
            } catch (Exception e2) {
                return Collections.emptyList();
            }
        }
    }

    public static Collection<String> listResources(String name) {
        final Collection fileNames = new ArrayList<>();
        try {
            final URL url = Resources.getResource(name);

            final File file = Paths.get(url.toURI()).toFile();
            if (file.isDirectory()) {
                for (File oneFile : file.listFiles()) {
                    fileNames.addAll(listResources(name + File.separator + oneFile.getName()));
                }
            } else {
                fileNames.add(name);
            }
        } catch (Exception e) {
            logger.error("Exception occurred: " + name, e);
        }
        return fileNames;
    }


}
