package com.ngontro86.algo.gui

import com.ngontro86.algo.gui.table.CallPutColorRenderer
import com.ngontro86.algo.gui.table.OptionChainDoubleColorRenderer
import com.ngontro86.algo.gui.table.OptionChainTableModel
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

@DBOEComponent
class OptionChainDlg extends JDialog {

    @Logging
    private Logger logger

    private OptionChainTableModel optionTableModel

    private JComboBox<String> chainCbo = new JComboBox<String>(), undCbo = new JComboBox<String>()
    private JComboBox<Integer> expCbo = new JComboBox<Integer>()

    @Inject
    private ExchangeSpecsLoader exchangeSpecsLoader

    @Inject
    private OrderbookDlg obDlg

    @PostConstruct
    void init() {
        initGUI()

        chainCbo.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                populateUnderlyingCbo()
            }
        })

        undCbo.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                populateExpiryCbo()
            }
        })

        expCbo.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                refreshOptionChain()
            }

        })
    }

    private void populateExpiryCbo() {
        def options = exchangeSpecsLoader.loadOptions((String) chainCbo.getSelectedItem())
        def exps = options.findAll { it['underlying'] == (String) undCbo.getSelectedItem() }.collect {
            it['expiry']
        } as Set
        expCbo.removeAllItems()
        println "Exp: ${exps}"
        exps.each { expCbo.addItem(it) }
    }

    private void populateUnderlyingCbo() {
        def options = exchangeSpecsLoader.loadOptions((String) chainCbo.getSelectedItem())
        def unds = options.collect { it['underlying'] } as Set

        undCbo.removeAllItems()
        unds.each { undCbo.addItem(it) }
    }

    void refreshOptionChain() {
        if ((Integer) expCbo.getSelectedItem() != null) {
            def mkt = exchangeSpecsLoader.loadOptionChainMarket((String) chainCbo.getSelectedItem(), (String) undCbo.getSelectedItem(), (Integer) expCbo.getSelectedItem())

            optionTableModel.updateAll(mkt)
        }
    }

    private void initGUI() {
        setTitle("DBOE - Options Market")
        this.setLayout(new BorderLayout())
        this.setSize(900, 600)
        this.setDefaultCloseOperation(HIDE_ON_CLOSE)

        def tabbedPane = new JTabbedPane()
        this.add(tabbedPane, BorderLayout.CENTER)

        this.optionTableModel = new OptionChainTableModel()
        def optionTable = new JTable(optionTableModel)
        optionTable.setDefaultRenderer(String.class, new CallPutColorRenderer())
        optionTable.setDefaultRenderer(Double.class, new OptionChainDoubleColorRenderer())

        tabbedPane.add("Options", new JScrollPane(optionTable))

        tabbedPane.add("3D Volatility", new JTable())

        def selectionPanel = new JPanel(new GridLayout(0, 6))
        this.add(selectionPanel, BorderLayout.SOUTH)
        setupSelectionPanel(selectionPanel)
    }

    private void setupSelectionPanel(JPanel jPanel) {
        jPanel.add(new JLabel("<html></b>Step 1: Select Chain:</b></html>"))
        jPanel.add(chainCbo)

        jPanel.add(new JLabel("<html></b>Step 2: Select Underlying:</b></html>"))
        jPanel.add(undCbo)

        jPanel.add(new JLabel("<html></b>Step 3: Select Expiry:</b></html>"))
        jPanel.add(expCbo)

        populateChainDropdown()
    }

    private void populateChainDropdown() {
        def chains = exchangeSpecsLoader.loadChains().collect { it['chain'] } as Set
        chains.each {
            chainCbo.addItem(it)
        }
    }

}
