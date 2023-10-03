package com.ngontro86.algo.gui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PNLColorRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row < 0) return c;
        Object valObj = table.getValueAt(row, table.getColumnCount() - 1);
        if (valObj == null || !(valObj instanceof Double)) return c;
        double val = (Double) valObj;

        if (val < 0.0) c.setBackground(Color.RED);
        else if (val > 0.0) c.setBackground(Color.GREEN);

        return c;
    }
}
