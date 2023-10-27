package com.ngontro86.algo.gui.table

import com.ngontro86.common.gui.table.ArrayTableModel

class OrderbookTableModel extends ArrayTableModel {

    OrderbookTableModel() {
        super(15, [Double, Integer, Double, Integer, Double],
                ['Bid Qty', 'Bid Lvl', 'Price', 'Ask Level', 'Ask Qty'],
                [false, false, false, false, false, false, false, false, false, false, false, false])
    }

    void updateAll(Collection ob) {
        int totalRow = ob.size()
        ob.findAll { it['buy_sell'] == 2 }
                .sort { a, b -> b['price_level'] <=> a['price_level'] }
                .each { update(it, totalRow) }

        ob.findAll { it['buy_sell'] == 1 }
                .sort { a, b -> a['price_level'] <=> b['price_level'] }
                .each { update(it, totalRow) }
    }

    private void update(Map<String, Object> m, int totalRow) {
        try {
            Integer id = m['price_level'] + (m['buy_sell'] == 1 ? (totalRow / 2) : 0)
            if (m['buy_sell'] == 1) {
                setRowValue([m['size'], m['price_level'], m['price'], null, null] as Object[], id)
            } else if (m['buy_sell'] == 2) {
                setRowValue([null, null, m['price'], m['price_level'], m['size']] as Object[], id)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}
