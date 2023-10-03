package com.ngontro86.algo.gui

import com.ngontro86.algo.gui.table.OptionChainColorRenderer
import com.ngontro86.algo.gui.table.OptionChainTableModel
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.market.instruments.ExchangeSpecsLoader

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.swing.*
import java.awt.*

@DBOEComponent
class OptionChainDlg extends JDialog {

    private OptionChainTableModel table

    @Inject
    private ExchangeSpecsLoader exchangeSpecsLoader

    @PostConstruct
    void initGUI() {
        setTitle("DBOE - Option Chain")
        this.setLayout(new BorderLayout())
        this.setSize(900, 600)
        this.table = new OptionChainTableModel()
        JTable table = new JTable(table)
        table.setDefaultRenderer(String.class, new OptionChainColorRenderer())
        this.add(new JScrollPane(table), BorderLayout.CENTER)
    }

}
