package com.ngontro86.algo.gui

import com.ngontro86.common.biz.entity.PairBasis
import org.jfree.data.time.Second
import org.jfree.data.time.TimeSeries

class PairBasisPanel extends TimeSeriesGraphPanel<PairBasis> {

    PairBasisPanel(String title, String categoryLabel, String valueLabel) {
        super(title, categoryLabel, valueLabel)
        this.graphMinuteLen = 60
    }

    @Override
    protected void displayGraph() {
        TimeSeries series = getTimeSeries('Statistical Basis', true)
        if (series != null) {
            series.addOrUpdate(new Second(new Date(lastSignal.timestamp)), lastSignal.basis - lastSignal.fairBasis)
        }
    }
}
