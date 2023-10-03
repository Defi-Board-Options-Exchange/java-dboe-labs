package com.ngontro86.algo.gui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StratColorRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {  
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);  
		if(row < 0) return c;
		Object valObj = table.getValueAt(row, table.getColumnCount() - 2);
		if(StratTableModel.RUNNING.equals(valObj)) {
			c.setBackground(Color.RED);
			return c;
		} 
		c.setBackground(Color.WHITE);
		return c;  
	}  
}
