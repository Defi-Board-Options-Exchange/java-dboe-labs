package com.ngontro86.app.common.db

import org.junit.Test

import static com.ngontro86.app.common.db.SqlUtils.constructReplacePreparedStatement


class SqlUtilsTest {
    @Test
    void "should construct replace statement properly"() {
        assert constructReplacePreparedStatement('Trades',
                [
                        'orderId'  : '1',
                        'qty'      : 5,
                        'side'     : 'BUY',
                        'timestamp': 190l
                ]) == "replace into `Trades` (`orderId`,`qty`,`side`,`timestamp`) values (?,?,?,?)"
    }
    
}
