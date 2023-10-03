package com.ngontro86.algo.gui.table;

import java.util.HashMap;
import java.util.Map;

import com.ngontro86.common.gui.table.ArrayTableModel;

public class PNLTableModel extends ArrayTableModel {

	private static final long serialVersionUID = 1L;

	private Map<String, Integer> rowMap = new HashMap<String, Integer>();
	
	public PNLTableModel(int max) {
		super(max, 
				new Class[]{String.class, String.class, String.class, String.class, Double.class}, 
				"Broker,Account,Portfolio,Inst,PNL".split(","),
				new Boolean[]{false,false,false,false,false}
				);
	}

	public void update(Map<String, Object> map) {
		try {
			String broker = (String)map.get("broker"), account = (String)map.get("account"),
					portfolio = (String)map.get("portfolio"), instId = (String)map.get("inst_id");
			double val = (Double)map.get("val");
			String key = getKey(broker, account, portfolio, instId);
			int nextIdx = rowMap.containsKey(key)? rowMap.get(key) : rowMap.size();
			setRowValue(new Object[]{broker, account, portfolio, instId, val}, nextIdx);
			rowMap.put(key, nextIdx);
		} catch (Exception e) { e.printStackTrace();}
	}
	
	private String getKey(String broker, String account, String portfolio, String instId) { return String.format("%s%s%s%s", broker, account, portfolio, instId); }
	
}
