package com.ngontro86.server.copyq;

import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.common.copyq.QFilter;
import com.ngontro86.common.writer.ObjectWriter;
import com.ngontro86.utils.Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static java.lang.Thread.MIN_PRIORITY;

@DBOEComponent
public class QWriter {

    @ConfigValue
    private String copyqPrefix;

    @ConfigValue(config = "copyq")
    private String copyqRepoDestination;

    @Inject
    private QFilter qFilter;

    @ConfigValue(config = "copyQDuration")
    private Integer copyQDuration = 60;

    @PostConstruct
    private void init() {
        final ObjectWriter objectWriter = new ObjectWriter(copyqPrefix, copyqRepoDestination, copyQDuration, qFilter);
        Utils.startThread(objectWriter, MIN_PRIORITY);
    }
}
