package com.ngontro86.algo.gui;

import com.ngontro86.algo.gui.table.PortfolioColorRenderer;
import com.ngontro86.algo.gui.table.PortfolioTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PortfolioDlg extends AbstractDlg {

    private static final long serialVersionUID = 1L;

    private PortfolioTableModel tableModel;

    public PortfolioDlg() {
        super("Portfolio Dlg");
        initGUI(null);
    }

    @Override
    public void initGUI(String[] args) {
        this.setLayout(new BorderLayout());
        this.setSize(300, 200);
        this.tableModel = new PortfolioTableModel(20);
        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(String.class, new PortfolioColorRenderer());
        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void processMapEvent(String eventId, Map<String, Object> event) {
        if ("Portfolio".equals(eventId)) {
            this.tableModel.update(event);
        }
    }

    @Override
    public void update(String stype, Object newObj, Object oldObj) {
    }

}
