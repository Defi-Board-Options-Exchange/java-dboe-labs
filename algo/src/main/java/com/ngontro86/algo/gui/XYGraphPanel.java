package com.ngontro86.algo.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ngontro86.common.Handler;
import com.ngontro86.common.gui.chart.XYSeriesChartPanel;

public abstract class XYGraphPanel extends XYSeriesChartPanel implements Handler<Map<String, Object>> {

	private static final long serialVersionUID = 1L;

	protected List<Map<String, Object>> data = new LinkedList<Map<String,Object>>();
	
	public XYGraphPanel(String title, String categoryLabel, String valueLabel, double yMin, double yMax) {
		super(title, categoryLabel, valueLabel, yMin, yMax);
	}

	public abstract void refreshGUI();
	
	public void clearData() { data.clear(); }
	
	@Override
	public boolean handle(Map<String, Object> obj) { data.add(obj); return true; }

}
