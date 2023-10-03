package com.ngontro86.common.gui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class TimeSeriesChartPanel extends JPanel implements ComponentListener {
    private static final long serialVersionUID = 1L;
    public JLabel chartLabel = null;
    public JFreeChart chart;
    public double yMin = 0.0, yMax = 0.0, graphStrokeWidth = 1.0, graphMinuteLen = 15.0;
    public boolean graphShapesVisible = true, graphShapesFilled = true;

    private TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();

    public TimeSeriesChartPanel(String title, String categoryLabel, String valueLabel) {
        this.addComponentListener(this);
        this.setMinimumSize(new Dimension(100, 100));
        chart = ChartFactory.createTimeSeriesChart(title, categoryLabel, valueLabel, this.timeSeriesCollection, true, true, false);
        init();
    }

    public void init() {
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        chartLabel = new JLabel();
        createChartLabel(200, 200);
        add(chartLabel);
        this.chart.getXYPlot().getDomainAxis().setAutoRange(true);
        this.chart.getXYPlot().getDomainAxis().setFixedAutoRange(this.graphMinuteLen * 60000D);
        if (this.yMin != 0.0 || this.yMax != 0.0) chart.getXYPlot().getRangeAxis().setRange(this.yMin, this.yMax);
        ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setBaseShapesVisible(this.graphShapesVisible);
        ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setBaseShapesFilled(this.graphShapesFilled);
    }

    public void refreshChart() {
        for (Object series : timeSeriesCollection.getSeries()) {
            TimeSeries ts = (TimeSeries) series;
            ts.removeAgedItems(false);
            if (ts.getItemCount() == 0) timeSeriesCollection.removeSeries(ts);
        }
        createChartLabel(getWidth(), getHeight());
    }

    protected void createChartLabel(int w, int h) {
        synchronized (this) {
            chartLabel.setIcon(new ImageIcon(chart.createBufferedImage(Math.max(w, 20), Math.max(h, 20))));
        }
    }

    public TimeSeries getTimeSeries(String category, boolean createIfMissing) {
        TimeSeries series = timeSeriesCollection.getSeries(category);
        if ((series == null) && createIfMissing) {
            series = new TimeSeries(category);
            series.setMaximumItemAge((long) (graphMinuteLen * 1.2 * 60));
            timeSeriesCollection.addSeries(series);
            for (int i = 0; i < timeSeriesCollection.getSeriesCount(); i++) {
                chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke((float) this.graphStrokeWidth));

            }
        }
        return series;
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
    }
}
