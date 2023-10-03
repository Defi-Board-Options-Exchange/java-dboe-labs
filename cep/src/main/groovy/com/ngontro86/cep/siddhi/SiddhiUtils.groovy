package com.ngontro86.cep.siddhi

import io.siddhi.core.event.Event

import javax.management.Attribute

import static com.ngontro86.utils.ResourcesUtils.content

class SiddhiUtils {

    static Map toMap(Attribute[] attributes, Event event) {
        def map = [:]
        attributes.eachWithIndex { a, i -> map[a.name] = event.getData(i) }
        map
    }

    static String constructSiddhi(boolean backtest, String serverId) {
        final StringBuilder sb = new StringBuilder()
        sb.append(content("siddhi/server.siddhi"))
        .append("\n")
        .append(content("siddhi/serverCore.siddhi"))

        /*
        if (backtest) {
            sb.append(content("siddhi/playback.siddhi"))
        }
        def siddhiFiles = listResources("siddhi")
        siddhiFiles
                .findAll {it.endsWith("siddhi") && !it.startsWith("playback")}
                .each {sb.append('\n').append(content(it))}
        */
        return sb.toString()
    }

}
