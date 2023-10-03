package com.ngontro86.common.gui.table;

import javax.swing.table.AbstractTableModel;

public class ArrayTableModel extends AbstractTableModel {

    protected static final long serialVersionUID = 1L;

    @SuppressWarnings("rawtypes")
    protected Class[] colClasses;
    protected String[] colNames;
    protected Boolean[] colEditables;

    protected Object[][] data;
    protected int columnCount, rowCount;

    protected volatile int rowIdx;

    @SuppressWarnings("rawtypes")
    public ArrayTableModel(int max, Class[] colClasses, String[] colNames, Boolean[] colEditables) {
        super();
        init(max, colClasses, colNames, colEditables);
    }

    @SuppressWarnings("rawtypes")
    protected void init(int max, Class[] columnClasses, String[] columnNames, Boolean[] columnEditables) {
        rowCount = max;
        columnCount = columnClasses.length;

        data = new Object[rowCount][columnCount];

        this.colClasses = columnClasses;
        this.colNames = columnNames;
        this.colEditables = columnEditables;
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        if (data != null && data.length > arg0 && data[0].length > arg1) return data[arg0][arg1];
        return null;
    }

    public synchronized void clear() {
        this.rowIdx = 1;
        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                setValueAt(null, i, j);
            }
        }
    }

    public Object[] getRowValues(int rowIdx) {
        return rowIdx < rowCount ? data[rowIdx] : null;
    }

    protected void setRowValue(Object[] oneRowVals, int row) {
        for (int col = 0; col < oneRowVals.length; col++) setValueAt(oneRowVals[col], row, col);
    }

    public boolean isCellEditable(int row, int col) {
        return colEditables[col];
    }

    public void clearOneRow(int row) {
        for (int i = 0; i < getColumnCount(); i++) setValueAt(null, row, i);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (row >= rowCount || col >= colNames.length) return;
        if (data == null) return;
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

    @Override
    public String getColumnName(int col) {
        return colNames[col];
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Class getColumnClass(int c) {
        return colClasses[c];
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }
}
