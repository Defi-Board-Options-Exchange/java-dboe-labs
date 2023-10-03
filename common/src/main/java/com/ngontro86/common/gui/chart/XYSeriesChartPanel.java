package com.ngontro86.common.gui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;

public class XYSeriesChartPanel extends JPanel implements ComponentListener {
    private static final long serialVersionUID = 1L;

    public JLabel chartLabel = null;
    public JFreeChart chart;
    public double yMin = 0.0, yMax = 0.0, graphStrokeWidth = 1.0;
    public boolean graphShapesVisible = true, graphShapesFilled = true;

    private Map<String, XYSeries> xySeriesMap = new HashMap<String, XYSeries>();
    private XYSeriesCollection xySeriesCollection = new XYSeriesCollection();

    public XYSeriesChartPanel(String title, String categoryLabel, String valueLabel) {
        this(title, categoryLabel, valueLabel, 0.0, 0.0);
    }

    public XYSeriesChartPanel(String title, String categoryLabel, String valueLabel, double yMin, double yMax) {
        this.addComponentListener(this);
        this.setMinimumSize(new Dimension(100, 100));
        chart = ChartFactory.createXYLineChart(title, categoryLabel, valueLabel, xySeriesCollection, PlotOrientation.VERTICAL, true, true, true);
        this.yMin = yMin;
        this.yMax = yMax;
        init();
    }

    public void init() {
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        chartLabel = new JLabel();
        createChartLabel(200, 200);
        add(chartLabel);
        this.chart.getXYPlot().getDomainAxis().setAutoRange(true);
        ((NumberAxis) this.chart.getXYPlot().getDomainAxis()).setTickUnit(new NumberTickUnit(1.0));

        if (this.yMin != 0.0 || this.yMax != 0.0) chart.getXYPlot().getRangeAxis().setRange(this.yMin, this.yMax);
        ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setBaseShapesVisible(this.graphShapesVisible);
        ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setBaseShapesFilled(this.graphShapesFilled);
    }

    public void refreshChart() {
        for (Object series : xySeriesCollection.getSeries()) {
            XYSeries ts = (XYSeries) series;
            if (ts.getItemCount() == 0) xySeriesCollection.removeSeries(ts);
        }
        createChartLabel(getWidth(), getHeight());
    }

    public XYSeries getXYSeries(String category, boolean createIfMissing, boolean allowDuplicate) {
        XYSeries series = xySeriesMap.get(category);
        if ((series == null) && createIfMissing) {
            series = new XYSeries(category, true, allowDuplicate);
            xySeriesCollection.addSeries(series);
            xySeriesMap.put(category, series);
            for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++)
                chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke((float) this.graphStrokeWidth));
        }
        return series;
    }

    protected void createChartLabel(int w, int h) {
        synchronized (this) {
            chartLabel.setIcon(new ImageIcon(chart.createBufferedImage(Math.max(w, 20), Math.max(h, 20))));
        }
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
