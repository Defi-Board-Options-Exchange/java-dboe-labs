package com.ngontro86.component.testing.defaultEnvs

import com.ngontro86.app.common.db.DataSource


class HsqlDataSource implements DataSource{

    @Override
    void updateQuery(String query) {

    }

    @Override
    Collection<Map> queryList(String query) {
        return null
    }

    @Override
    boolean persist(String tableName, Collection<Map<String, Object>> maps) {
        return false
    }
}
