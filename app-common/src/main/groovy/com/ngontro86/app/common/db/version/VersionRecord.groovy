package com.ngontro86.app.common.db.version

class VersionRecord {

    private Map origRecord = [:]
    private Set primaryKeys = [] as TreeSet
    private Map amendedProps = [:]
    VersionRecord(Map record, Set keys) {
        origRecord.putAll(record)
        primaryKeys.addAll(keys)
    }
    void update(String key, Object value) {
        amendedProps.put(key, value)
    }
}
