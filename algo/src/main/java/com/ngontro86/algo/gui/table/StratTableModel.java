package com.ngontro86.algo.gui.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.ngontro86.common.gui.table.ArrayTableModel;

public class StratTableModel extends ArrayTableModel {
	private static final long serialVersionUID = 1L;
	
	public final static String RUNNING = "Running", IDLE = "Idle";
	
	private Map<String, Integer> rowMap = new HashMap<String, Integer>();
	
	public StratTableModel(int max) {
		super(max, 
				new Class[]{String.class, Boolean.class,String.class, ImageIcon.class}, 
				"Strat,Signal,State,Stop".split(","), 
				new Boolean[]{true, true, false, false});
	}

	public void update(String strategyName) {
		ImageIcon stopIcon = new ImageIcon("resources/stop.png");
		Object[] rowData = new Object[]{ strategyName, false, IDLE, stopIcon };
		int nextRowIdx = rowMap.containsKey(strategyName)? rowMap.get(strategyName) : rowIdx++;
		setRowValue(rowData, nextRowIdx);
		rowMap.put(strategyName, nextRowIdx);
	}
	
	public void setState(String strategyName, boolean isRunning) {
		if(!rowMap.containsKey(strategyName)) return;
		int rowIdex = rowMap.get(strategyName);
		setValueAt(isRunning? RUNNING : IDLE, rowIdex, 2);
	}
	
}
