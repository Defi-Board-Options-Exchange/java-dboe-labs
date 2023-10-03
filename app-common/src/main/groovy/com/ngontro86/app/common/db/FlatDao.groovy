package com.ngontro86.app.common.db

import org.apache.commons.beanutils.BeanUtils


class FlatDao {
    private DataSource dataSource
    FlatDao(DataSource ds) {
        this.dataSource = ds
    }
    Collection<Map> queryList(String query) {
        return dataSource.queryList(query)
    }
    def <T> Collection<T> queryList(String query, Class<T> aClass) {
        return queryList(query).collect { map ->
            T oneBean = aClass.newInstance()
            BeanUtils.populate(oneBean, map)
            return oneBean
        }
    }
    Collection<String> queryStringList(String query) {
        return queryList(query).collect { it.entrySet().collect { it.value } }.flatten()
    }
    Collection<Integer> queryIntList(String query) {
        return queryList(query).collect { it.entrySet().collect { it.value } }.flatten()
    }
    void persist(String table, Collection<Map<String, Object>> data) throws Exception {
        dataSource.persist(table, data)
    }
    void updateQuery(String query) throws Exception {
        dataSource.updateQuery(query)
    }
}
