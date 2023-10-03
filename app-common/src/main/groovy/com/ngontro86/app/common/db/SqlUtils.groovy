package com.ngontro86.app.common.db

import org.springframework.core.NamedThreadLocal

import java.util.concurrent.ConcurrentHashMap


class SqlUtils {

    private static ThreadLocal<Map<String, String>> preparedStatementThreadLocal = new NamedThreadLocal("SqlUtils") {
        @Override
        protected Map<String, String> initialValue() {
            return new ConcurrentHashMap<String, String>()
        }
    }

    static String constructReplacePreparedStatement(String tableName, Map<String, Object> recordMap) {
        if (preparedStatementThreadLocal.get().containsKey(tableName)) {
            return preparedStatementThreadLocal.get().get(tableName)
        }
        final StringBuilder sb = new StringBuilder("replace into `$tableName` (")
        boolean firstEntry = true
        recordMap.entrySet().each { e ->
            if (!firstEntry) {
                sb.append(",")
            }
            sb.append("`${e.getKey()}`")
            firstEntry = false
        }
        sb.append(") values (")
        firstEntry = true
        recordMap.size().times {
            if (!firstEntry) {
                sb.append(",")
            }
            sb.append("?")
            firstEntry = false
        }
        sb.append(")")
        preparedStatementThreadLocal.get().put(tableName, sb.toString())
        return sb.toString()
    }

}
