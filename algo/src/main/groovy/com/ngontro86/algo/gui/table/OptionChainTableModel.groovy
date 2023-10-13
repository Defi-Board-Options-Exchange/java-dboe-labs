package com.ngontro86.algo.gui.table

import com.ngontro86.common.gui.table.ArrayTableModel

class OptionChainTableModel extends ArrayTableModel {

    private Map<String, Integer> rowMap = [:]

    private static String[] keys = "instr_id,strike,cond_strike,open_interest,bid_iv,bid_size,bid,ask,ask_size,ask_iv,traded_value,buy_mwr".split(',')

    OptionChainTableModel() {
        super(25, [String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double],
                ['ID', 'Strike', 'Target', 'Open Interest ($)', 'Bid IV', 'Bid Qty', 'Bid', 'Ask', 'Ask Qty', 'Ask IV', 'Traded Value($)', 'MWR'],
                [false, false, false, false, false, false, false, false, false, false, false, false])

    }

    void updateAll(Collection<Map> maps) {
        rowMap.clear()
        maps.each { update(it) }
    }

    private void update(Map<String, Object> map) {
        try {
            String id = map['instr_id']
            int nextIdx = rowMap.containsKey(id) ? rowMap.get(id) : rowMap.size()
            setRowValue(keys.collect { map.get(it) } as Object[], nextIdx)
            rowMap.put(id, nextIdx)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}
