package com.ngontro86.algo.gui.table

import com.ngontro86.common.gui.table.ArrayTableModel

class OrderbookTableModel extends ArrayTableModel {

    private Map<Integer, Integer> rowMap = [:]

    OrderbookTableModel() {
        super(15, [Double, Integer, Double, Integer, Double],
                ['Bid Qty', 'Bid Lvl', 'Price', 'Ask Level', 'Ask Qty'],
                [false, false, false, false, false, false, false, false, false, false, false, false])
    }

    void update(Map<String, Object> m) {
        try {
            Integer id = key(m['price_level'], m['buy_sell'])
            int nextIdx = rowMap.containsKey(id) ? rowMap.get(id) : rowMap.size()
            if (m['buy_sell'] == 1) {
                setRowValue([m['size'], m['price_level'], m['price'], null, null] as Object[], nextIdx)
            } else {
                setRowValue([null, null, m['price_level'], m['price'], m['size']] as Object[], nextIdx)
            }
            rowMap.put(id, nextIdx)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private static Integer key(int lvl, int bidAsk) {
        return 53 * bidAsk + lvl
    }
}
