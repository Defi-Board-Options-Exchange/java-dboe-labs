package com.ngontro86.app.common.db


import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.*

import static com.ngontro86.common.config.Configuration.config

class NonTxDataSource implements DataSource {

    private Logger logger = LogManager.getLogger(NonTxDataSource)

    private Connection connection

    private Timer timer

    NonTxDataSource() {
        try {
            def host = config().getConfig('datasource.host')
            def database = config().getConfig('datasource.name')
            def port = config().getIntConfig('datasource.port', 3306)
            def user = config().getConfig('datasource.username')
            def pswd = config().getConfig('datasource.password')
            def autoCommit = config().getBooleanConfig('datasource.autocommit', true)
            logger.info("Host: ${host}, database: ${database}, port: ${port}, user: ${user}")
            if (autoCommit) {
                connection = DriverManager.getConnection("jdbc:mysql://$host:$port/$database?useSSL=false&autoReconnect=true&serverTimezone=UTC", user, pswd)
            } else {
                connection = DriverManager.getConnection("jdbc:mysql://$host:$port/$database?rewriteBatchedStatements=true&useSSL=false&autoReconnect=true&serverTimezone=UTC", user, pswd)
            }
            connection.setAutoCommit(autoCommit)
            timer = new Timer()
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                void run() {
                    logger.info("Keeping connection alive...")
                    queryList("select 1")
                }
            }, 30000 * 60, 30000 * 60)
        } catch (Throwable e) {
            e.printStackTrace()
            throw e
        }
    }

    @Override
    Collection<Map<String, Object>> queryList(String query) throws Exception {
        def ret = []
        Statement stmt
        try {
            stmt = connection.createStatement()
            ResultSet rs = stmt.executeQuery(query)
            ResultSetMetaData rsmd = rs.getMetaData()
            while (rs.next()) {
                def curr = new HashMap()
                for (int i = 1; i <= rsmd.columnCount; i++) {
                    curr.put(rsmd.getColumnLabel(i), rs.getObject(i))
                }
                ret += curr
            }
            return ret
        } finally {
            if (stmt != null) {
                stmt.close()
            }
        }
    }

    @Override
    void updateQuery(String query) throws Exception {
        def statement
        try {
            statement = connection.prepareStatement(query)
            statement.execute()
        } finally {
            if (statement != null) {
                statement.close()
            }
        }
    }

    @Override
    boolean persist(String table, Collection<Map<String, Object>> data) throws Exception {
        if (data == null || data.isEmpty()) {
            return true
        }
        def orderedMap = new TreeMap(data.first())
        def query = SqlUtils.constructReplacePreparedStatement(table, orderedMap)
        def statement = connection.prepareStatement(query)
        try {
            final Integer totalRes = 0
            data.each { oneRecord ->
                int idx = 1
                for (Object obj : new TreeMap<>(oneRecord).values()) {
                    statement.setObject(idx++, obj)
                }
                totalRes += statement.executeUpdate()
                statement.clearParameters()
            }
            return totalRes == 0
        } catch (Exception e) {
            println "${table} - ${data}"
            println e
        } finally {
            statement.close()
        }
    }

}
