package com.ngontro86.algo.gui.table

import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import java.awt.*

class OptionChainDoubleColorRenderer extends DefaultTableCellRenderer {
    private Set<Integer> colorColumns = [3, 5, 7, 10, 11]

    @Override
    Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        if (row < 0) {
            return c
        }
        if (value instanceof Double && colorColumns.contains(column)) {
            c.setBackground(redGreen(minMax(table, column), (Double) value))
        }
        return c
    }

    private Color redGreen(Double[] minMax, double val) {
        int green = 255 * (val - minMax[0]) / (minMax[1] - minMax[0])
        return new Color(255 - green, green, 0)
    }

    private Double[] minMax(JTable table, int column) {
        def min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY

        for (int row = 0; row < table.getRowCount(); row++) {
            Object valObj = table.getValueAt(row, column)
            if (valObj == null || !(valObj instanceof Double)) {
                continue
            }
            double val = (Double) valObj
            min = Math.min(val, min)
            max = Math.max(val, max)
        }
        return [min, max] as Double[]
    }
}
