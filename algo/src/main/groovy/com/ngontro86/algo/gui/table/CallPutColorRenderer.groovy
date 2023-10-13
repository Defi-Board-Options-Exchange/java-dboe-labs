package com.ngontro86.algo.gui.table

import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import java.awt.*

class CallPutColorRenderer extends DefaultTableCellRenderer {

    @Override
    Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        if (row < 0) {
            return c
        }
        Object valObj = table.getValueAt(row, column)
        if (valObj == null || !(valObj instanceof String)) {
            return c
        }
        def val = (String) valObj
        if (val.contains("C")) {
            c.setBackground(Color.CYAN)
        } else if (val.contains("P")) {
            c.setBackground(Color.ORANGE)
        }

        return c
    }
}
