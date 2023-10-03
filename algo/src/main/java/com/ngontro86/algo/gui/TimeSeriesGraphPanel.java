package com.ngontro86.algo.gui;

import java.util.Timer;
import java.util.TimerTask;

import com.ngontro86.common.Handler;
import com.ngontro86.common.biz.entity.PriceMA;
import com.ngontro86.common.gui.chart.TimeSeriesChartPanel;

public abstract class TimeSeriesGraphPanel<T> extends TimeSeriesChartPanel implements Handler<T> {

    private static final long serialVersionUID = 1L;

    protected T lastSignal;

    public TimeSeriesGraphPanel(String title, String categoryLabel, String valueLabel) {
        super(title, categoryLabel, valueLabel);
        (new Timer()).schedule(new CSPRefreshTask(), 1000, 1000);
    }

    @Override
    public boolean handle(T obj) {
        lastSignal = obj;
        return true;
    }

    protected abstract void displayGraph();

    private class CSPRefreshTask extends TimerTask {
        @Override
        public void run() {
            if (lastSignal == null) {
                return;
            }
            synchronized (this) {
                displayGraph();
                refreshChart();
                lastSignal = null;
            }
        }
    }
}
