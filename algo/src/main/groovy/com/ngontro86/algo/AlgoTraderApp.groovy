package com.ngontro86.algo


import com.ngontro86.algo.gui.OptionChainDlg
import com.ngontro86.algo.gui.OrderbookDlg
import com.ngontro86.common.annotations.EntryPoint
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.gui.chart.CommonGUI
import org.apache.logging.log4j.Logger

import javax.inject.Inject
import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class AlgoTraderApp {

    @Logging
    protected Logger logger

    private JFrame dBfrm

    @Inject
    private OptionChainDlg optionChainDlg

    @Inject
    private OrderbookDlg obDlg

    @EntryPoint
    void goTrading() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            void run() {
                initGUI()
            }
        })
        new java.util.Timer().schedule(loggingTask, 1000, 5 * 60000)
    }

    def buttonActionListener = new ActionListener() {
        @Override
        void actionPerformed(ActionEvent e) {
            displayMenu((JMenuItem) e.source)
        }
    }

    private void initGUI() {
        CommonGUI.setLookAndFeel()
        this.dBfrm = new JFrame("DBOE Algo Trading - ${new Date().toString()}")
        this.dBfrm.setVisible(true)
        this.dBfrm.setIconImage(new ImageIcon("assets/dboe-icon.png").getImage())

        dBfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        dBfrm.setBounds(100, 100, 600, 150)
        dBfrm.setLayout(new BoxLayout(dBfrm.getContentPane(), BoxLayout.X_AXIS))

        def menuBar = new JMenuBar()
        this.dBfrm.setJMenuBar(menuBar)

        def settingMenu = new JMenu("Settings")
        menuBar.add(settingMenu)

        def marketMenu = new JMenu("Market")
        menuBar.add(marketMenu)
        [new JMenuItem("OptionChain"), new JMenuItem("Orderbook")].each { itm ->
            itm.addActionListener(buttonActionListener)
            marketMenu.add(itm)
        }

        def tradingMenu = new JMenu("Algo Strategy")
        menuBar.add(tradingMenu)
        [new JMenuItem("Market Making"), new JMenuItem("Sniping")].each { itm ->
            itm.addActionListener(buttonActionListener)
            tradingMenu.add(itm)
        }

        def reportMenu = new JMenu("Report")
        menuBar.add(reportMenu)
        [new JMenuItem("Portfolio"), new JMenuItem("PL")].each { itm ->
            reportMenu.add(itm)
            itm.addActionListener(buttonActionListener)
        }

        JMenu helpMenu = new JMenu("Help")
        menuBar.add(helpMenu)
    }

    private void displayMenu(JMenuItem bttn) {
        if (bttn.getText() == "OptionChain") {
            optionChainDlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE)
            optionChainDlg.setVisible(true)
        } else if (bttn.getText() == 'Orderbook') {
            obDlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE)
            obDlg.setVisible(true)
        }
    }

    private TimerTask loggingTask = new TimerTask() {
        @Override
        void run() {
            for (JDialog dlg : [optionChainDlg, obDlg]) {
                logger.info(dlg.toString())
            }
        }
    }
}
