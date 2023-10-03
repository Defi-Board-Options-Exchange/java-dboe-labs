package com.ngontro86.algo.gui

import com.ngontro86.algo.gui.table.StratColorRenderer
import com.ngontro86.algo.gui.table.StratTableModel
import com.ngontro86.common.gui.table.TableCellListener

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.ConcurrentHashMap

import static com.ngontro86.algo.utils.StrategyUtils.load
import static com.ngontro86.algo.utils.StrategyUtils.validatePairName
import static com.ngontro86.common.config.Configuration.config

class PairDlg extends AbstractDlg {
    private JComboBox firstCboInst, secondCboInst, thirdCboInst, cboBroker, cboAccount, cboPortfolio
    private JTextField textQty, textAbsoluteMaxLoss, textCloseoutTime, textRunningTime
    private JComboBox cboPairNames
    private JTextField text2ncPcWeight, textBasisThreshold, textExitBasis

    private StratTableModel stratModel
    protected String selectedStrat
    private JButton saveBttn, runBttn, closeOutBttn, stopAllBttn
    private ConcurrentHashMap<String, Object> strategies = [:] as ConcurrentHashMap
    private ConcurrentHashMap<String, PairSignalDlg> signals = [:] as ConcurrentHashMap

    PairDlg() {
        super("PairDlg")
        initGUI(null)
    }

    @Override
    void initGUI(String[] args) {
        this.setLayout(new BorderLayout())
        this.setMinimumSize(new Dimension(600, 600))

        initDefault()

        JPanel setup = new JPanel()
        this.add(setup, BorderLayout.CENTER)
        setSetupPanel(setup)

        JPanel bottom = new JPanel()
        this.add(bottom, BorderLayout.PAGE_END)
        setBottomPanel(bottom)
    }

    private void initDefault() {
        firstCboInst = new JComboBox()
        secondCboInst = new JComboBox()
        thirdCboInst = new JComboBox()
        thirdCboInst.addItem("")
        serv.getAllInst().stream().each { inst ->
            firstCboInst.addItem(inst.instId)
            secondCboInst.addItem(inst.instId)
            thirdCboInst.addItem(inst.instId)
        }
        cboPairNames = new JComboBox()

        db.queryStringList("select distinct pairName from pair_signal_setup where active = 1")
                .each { freq -> cboPairNames.addItem(freq) }

        cboBroker = new JComboBox(config().getCollectionConfig("Algo.brokers") as String[])
        cboAccount = new JComboBox(config().getCollectionConfig("Algo.accounts") as String[])
        cboPortfolio = new JComboBox(config().getCollectionConfig("Algo.portfolios") as String[])

        textQty = new JTextField("1")
        textAbsoluteMaxLoss = new JTextField("200.0")
        text2ncPcWeight = new JTextField("20")
        textBasisThreshold = new JTextField("500")
        textExitBasis = new JTextField("500")

        textCloseoutTime = new JTextField("161000")
        textRunningTime = new JTextField("121500")
    }

    private void setSetupPanel(JPanel centerPanel) {
        centerPanel.setLayout(new GridLayout(0, 2))
        JPanel leftPanel = new JPanel(new BorderLayout())
        centerPanel.add(leftPanel)
        JPanel rightPanel = new JPanel(new GridLayout(0, 1))
        centerPanel.add(rightPanel)

        // Left panel
        stratModel = new StratTableModel(9)
        final JTable stratTable = new JTable(stratModel)
        stratTable.setDefaultRenderer(String.class, new StratColorRenderer())
        leftPanel.add(new JScrollPane(stratTable))

        stratTable.addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent mevt) {
                processMouseAdapter(stratTable)
            }
        })

        Action editAct = new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent arg0) {
                processTableCellAction(stratTable, arg0)
            }
        }
        new TableCellListener(stratTable, editAct)
        loadAllStrategies()

        // Right panel
        def staticPanel = new JPanel(new GridLayout(0, 2))
        staticPanel.setName("Static")
        JTabbedPane staticPane = new JTabbedPane()
        staticPane.add(staticPanel)
        rightPanel.add(staticPane)

        def tradingPanel = new JPanel(new GridLayout(0, 2))
        tradingPanel.setName("Trading")
        JTabbedPane tradingPane = new JTabbedPane()
        tradingPane.add(tradingPanel)
        rightPanel.add(tradingPane)

        def riskPanel = new JPanel(new GridLayout(0, 2))
        riskPanel.setName("Risk")
        JTabbedPane entryRiskPane = new JTabbedPane()
        entryRiskPane.add(riskPanel)
        rightPanel.add(entryRiskPane)

        // static
        staticPanel.add(new JLabel("<html></b>Pair Type: </b></html>"))
        staticPanel.add(cboPairTypes)
        staticPanel.add(new JLabel("<html></b>1st Leg Inst: </b></html>"))
        staticPanel.add(firstCboInst)
        staticPanel.add(new JLabel("<html></b>2nd Leg-1st Inst: </b></html>"))
        staticPanel.add(secondCboInst)
        staticPanel.add(new JLabel("<html></b>2nd Leg-2nd Inst: </b></html>"))
        staticPanel.add(thirdCboInst)

        staticPanel.add(new JLabel("<html></b>Broker: </b></html>"))
        staticPanel.add(cboBroker)
        staticPanel.add(new JLabel("<html></b>Account: </b></html>"))
        staticPanel.add(cboAccount)
        staticPanel.add(new JLabel("<html></b>Portfolio: </b></html>"))
        staticPanel.add(cboPortfolio)

        // trading panel
        tradingPanel.add(new JLabel("<html></b>Base Qty: </b></html>"))
        tradingPanel.add(textQty)

        tradingPanel.add(new JLabel("<html></b>Running time(HHmmss): </b></html>"))
        tradingPanel.add(textRunningTime)
        tradingPanel.add(new JLabel("<html></b>Close out time(HHmmss): </b></html>"))
        tradingPanel.add(textCloseoutTime)

        tradingPanel.add(new JLabel("<html></b>Max 2ndPC Weight (pct): </b></html>"))
        tradingPanel.add(text2ncPcWeight)

        tradingPanel.add(new JLabel("<html></b>Basis Threshold (Local Currency): </b></html>"))
        tradingPanel.add(textBasisThreshold)

        tradingPanel.add(new JLabel("<html></b>Exit Basis (Local Currency): </b></html>"))
        tradingPanel.add(textExitBasis)

        tradingPanel.add(new JLabel("<html></b>Pair Signal: </b></html>"))
        tradingPanel.add(cboPairNames)

        // Entry Risk panel
        riskPanel.add(new JLabel("<html></b>Abs daily DD (USD): </b></html>"))
        riskPanel.add(textAbsoluteMaxLoss)
    }

    private void loadAllStrategies() {
        Collection<Map> allPairStrats = db.queryList("select distinct strategy_name from algo_strategies where strategy_type = ${PAIR.ordinal()}")
        allPairStrats.each { map ->
            String strategyName = (String) map.get("strategy_name")
            def strat = load(db.queryList("select * from algo_strategies where strategy_name = '${strategyName}'"), PairStrategy)
            strat.init(db, serv)
            strategies.put(strategyName, strat)
            stratModel.update(strategyName)
        }
    }

    private void processTableCellAction(JTable table, ActionEvent ae) {
        TableCellListener cellListener = (TableCellListener) ae.getSource()
        int col = cellListener.getColumn()
        if (col == 0) {
            String nStratName = (String) cellListener.getNewValue()
            selectedStrat = nStratName
            validatePairName(selectedStrat)
            renameButtns(nStratName)
        }
    }

    private void renameButtns(String stratName) {
        saveBttn.setText("Save " + stratName)
        runBttn.setText("Run " + stratName)
        closeOutBttn.setText("Close out " + stratName)
    }

    private void setBottomPanel(JPanel bottomPanel) {
        bottomPanel.setLayout(new FlowLayout())
        saveBttn = new JButton("Save")
        runBttn = new JButton("Run")
        closeOutBttn = new JButton("Close out")
        stopAllBttn = new JButton("Stop all")

        [saveBttn, runBttn, closeOutBttn, stopAllBttn].each { bttn ->
            bottomPanel.add(bttn)
            bttn.addActionListener(new ActionListener() {
                @Override
                void actionPerformed(ActionEvent e) {
                    processBttnClick(bttn)
                }
            })
        }
    }

    private void processMouseAdapter(JTable stratTable) {
        int row = stratTable.getSelectedRow(), col = stratTable.getSelectedColumn()
        String strat = getStrategyName(row)
        if (col == 0) {
            if (strat == null) {
                int ret = JOptionPane.showConfirmDialog(null,
                        "Create new strategy?",
                        "New strategy",
                        JOptionPane.YES_NO_CANCEL_OPTION)

                if (ret == JOptionPane.YES_OPTION) {
                    String dumpStratName = "rename here"
                    selectedStrat = dumpStratName
                    stratModel.update(dumpStratName)
                    renameButtns(dumpStratName)
                }
            } else {

            }
        } else if (col == 1 && strat != null) {
            Boolean selSignal = (Boolean) stratModel.getValueAt(row, 1)
            selectedStrat = strat
            if (selSignal) {

            } else {

            }
        } else if (col == stratTable.getColumnCount() - 1 && strat != null) {

        }
    }

    private String getStrategyName(int row) {
        Object obj = stratModel.getValueAt(row, 0)
        return obj != null ? (String) obj : null
    }

    private void processBttnClick(JButton bttn) {
        if (("Save " + selectedStrat).equals(bttn.getText())) {
            boolean ok = initStrategy(selectedStrat)
            if (!ok) {
                JOptionPane.showInternalMessageDialog(null, "Please setup strategy properly", "Wrong Setup", JOptionPane.OK_OPTION)
            }
        } else if (("Run " + selectedStrat).equals(bttn.getText())) {

        } else if (("Close out " + selectedStrat).equals(bttn.getText())) {

        } else if ("Stop all".equals(bttn.getText())) {
            int userOp = JOptionPane.showConfirmDialog(null, "Stop all strats?", "Attention", JOptionPane.YES_NO_OPTION)

            if (userOp != JOptionPane.YES_OPTION) {
                return
            }
        }
    }

    private boolean initStrategy(String strategy) {

    }

    @Override
    void processMapEvent(String eventId, Map<String, Object> event) {
    }

    @Override
    void update(String stype, Object newObj, Object oldObj) {
    }
}
