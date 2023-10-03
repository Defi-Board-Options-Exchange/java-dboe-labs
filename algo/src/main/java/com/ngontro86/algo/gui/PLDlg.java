package com.ngontro86.algo.gui;

import com.ngontro86.algo.gui.table.PNLColorRenderer;
import com.ngontro86.algo.gui.table.PNLTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PLDlg extends AbstractDlg {
    private PNLTableModel plTableModel;

    public PLDlg() {
        super("PL Dlg");
        initGUI(null);
    }

    @Override
    public void initGUI(String[] args) {
        this.setLayout(new BorderLayout());
        this.setSize(300, 200);
        this.plTableModel = new PNLTableModel(20);
        JTable table = new JTable(plTableModel);
        table.setDefaultRenderer(String.class, new PNLColorRenderer());
        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void processMapEvent(String eventId, Map<String, Object> event) {
        if ("PL".equals(eventId)) {
            this.plTableModel.update(event);
        }
    }

    @Override
    public void update(String stype, Object newObj, Object oldObj) {
    }
    private static final long serialVersionUID = 1L;


}
