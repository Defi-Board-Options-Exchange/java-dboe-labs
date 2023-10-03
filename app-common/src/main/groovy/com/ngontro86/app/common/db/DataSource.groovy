package com.ngontro86.app.common.db


interface DataSource {
    void updateQuery(String query)
    Collection<Map> queryList(String query)
    boolean persist(String tableName, Collection<Map<String,Object>> maps)
}