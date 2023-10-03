package com.ngontro86.algo.gui

import com.ngontro86.common.biz.entity.PairBasis
import org.jfree.chart.title.TextTitle

import java.awt.*
import java.awt.event.WindowEvent
import java.awt.event.WindowListener

class PairSignalDlg extends AbstractDlg {
    private String pairName
    private PairBasisPanel basisPanel

    PairSignalDlg(String title, String pairName) {
        super(title)
        this.pairName = pairName
        initGUI(null)
    }

    @Override
    void initGUI(String[] args) {
        this.setLayout(new GridLayout(0, 1))
        this.setMinimumSize(new Dimension(500, 400))
        basisPanel = new PairBasisPanel("Pair Basis", "Time", "Basis")
        this.add(basisPanel)

        this.addWindowListener(new WindowListener() {
            @Override
            void windowOpened(WindowEvent arg0) {
            }

            @Override
            void windowIconified(WindowEvent arg0) {
            }

            @Override
            void windowDeiconified(WindowEvent arg0) {
            }

            @Override
            void windowDeactivated(WindowEvent arg0) {
            }

            @Override
            void windowClosing(WindowEvent arg0) {
                stopObserving()
            }

            @Override
            void windowClosed(WindowEvent arg0) {
            }

            @Override
            void windowActivated(WindowEvent arg0) {
            }
        })
    }

    void colorTitle(boolean running) {
        def sigTitle = new TextTitle("Pair Name: " + this.pairName)
        if (running) {
            sigTitle.setBackgroundPaint(Color.GREEN)
        }
        basisPanel.chart.setTitle(sigTitle)
    }

    @Override
    void processMapEvent(String eventId, Map<String, Object> event) {
    }

    @Override
    void update(String stype, Object newObj, Object oldObj) {
        totalMsg++
        if (newObj instanceof PairBasis) {
            basisPanel.handle((PairBasis) newObj)
        }
    }

    private void stopObserving() {

    }
}
