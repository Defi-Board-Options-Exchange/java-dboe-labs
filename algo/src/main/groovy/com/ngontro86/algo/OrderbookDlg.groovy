package com.ngontro86.algo


import com.ngontro86.algo.gui.table.OrderbookColorRenderer
import com.ngontro86.algo.gui.table.OrderbookTableModel
import com.ngontro86.common.annotations.DBOEComponent

import javax.annotation.PostConstruct
import javax.swing.*
import java.awt.*

@DBOEComponent
class OrderbookDlg extends JDialog {

    private OrderbookTableModel table

    @PostConstruct
    void initGUI() {
        setTitle("DBOE - Order Book")
        this.setLayout(new BorderLayout())
        this.setSize(900, 600)
        this.table = new OrderbookTableModel()
        JTable table = new JTable(table)
        table.setDefaultRenderer(String.class, new OrderbookColorRenderer())
        this.add(new JScrollPane(table), BorderLayout.CENTER)
    }
}
