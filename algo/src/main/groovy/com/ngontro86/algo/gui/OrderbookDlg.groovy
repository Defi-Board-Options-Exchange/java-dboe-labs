package com.ngontro86.algo.gui


import com.ngontro86.algo.gui.table.OrderbookColorRenderer
import com.ngontro86.algo.gui.table.OrderbookTableModel
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.annotations.Logging
import com.ngontro86.market.instruments.ExchangeSpecsLoader
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

@DBOEComponent
class OrderbookDlg extends JDialog {

    @Logging
    private Logger logger

    @Inject
    private ExchangeSpecsLoader exchangeSpecsLoader

    private OrderbookTableModel table

    private JComboBox<Double> defaultQtyCbo = new JComboBox<Double>()
    private double defaultQty
    private String chain, instrId

    @PostConstruct
    void init() {
        initGUI()

        defaultQtyCbo.setEditable(true)
        defaultQtyCbo.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                defaultQty = (Double) defaultQtyCbo.getSelectedItem()
            }
        })
    }

    void setOption(String chain, String instrId) {
        this.chain = chain
        this.instrId = instrId
        setTitle("DBOE On-Chain Order Book: ${instrId} on ${chain}")
        refreshOrderBook()
    }

    void refreshOrderBook() {
        if (chain != null && instrId != null) {
            table.updateAll(exchangeSpecsLoader.orderbook(chain, instrId))
        }
    }

    private void initGUI() {
        setTitle("DBOE On-chain Order Book")
        this.setLayout(new BorderLayout())
        this.setSize(900, 600)
        this.table = new OrderbookTableModel()
        def obTable = new JTable(table)
        obTable.setDefaultRenderer(String.class, new OrderbookColorRenderer())
        obTable.addMouseListener(new MouseAdapter() {
            @Override
            void mousePressed(MouseEvent e) {
                println "Mouse pressed: ..."
            }
        })

        this.add(obTable, BorderLayout.CENTER)

        def selectionPanel = new JPanel(new GridLayout(0, 2))
        this.add(selectionPanel, BorderLayout.SOUTH)
        setupSelectionPanel(selectionPanel)
    }

    private void setupSelectionPanel(JPanel jPanel) {
        jPanel.add(new JLabel("<html></b>Set Default Order Qty:</b></html>"))
        jPanel.add(defaultQtyCbo)
        [1d, 2d, 3d, 4d, 5d, 10d].each { defaultQtyCbo.addItem(it) }
    }
}
