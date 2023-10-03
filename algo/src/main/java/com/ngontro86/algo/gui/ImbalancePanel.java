package com.ngontro86.algo.gui;

import java.util.Date;

import com.ngontro86.common.biz.entity.PriceMA;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

public class ImbalancePanel extends TimeSeriesGraphPanel<PriceMA> {
    private static final long serialVersionUID = 1L;

    private double imblThreshold;

    public ImbalancePanel(String title, String categoryLabel, String valueLabel, double threshold) {
        super(title, categoryLabel, valueLabel);
        this.imblThreshold = threshold;
    }


    @Override
    protected void displayGraph() {
        String[] categories = "b_threshold,imbAvg,s_threshold".split(",");
        double[] vals = new double[]{imblThreshold, lastSignal.imbAvg, -imblThreshold};
        for (int idx = 0; idx < categories.length; idx++) {
            String cat = categories[idx];
            double val = vals[idx];
            TimeSeries series = getTimeSeries(cat, true);
            if (series != null) {
                series.addOrUpdate(new Second(new Date(lastSignal.timestamp)), val);
            }
        }
    }

}
