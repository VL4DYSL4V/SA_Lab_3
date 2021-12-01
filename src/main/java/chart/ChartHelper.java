package chart;

import chart.dto.ChartDto;
import command.dto.ResultDto;
import org.apache.commons.math3.linear.RealVector;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    private static final ChartHelper INSTANCE = new ChartHelper();

    private static final Color BACKGROUND_COLOR = new Color(10, 15, 3);

    private static final Color FOREGROUND_COLOR = new Color(151, 232, 39);

    private JFrame previousFrameY;

    private JFrame previousFrameUk;

    private ChartHelper() {
    }

    public static ChartHelper getInstance() {
        return INSTANCE;
    }

    public void showNextChart(ResultDto result, double T) {
        if (this.previousFrameY != null) {
            this.previousFrameY.dispose();
        }
        if (this.previousFrameUk != null) {
            this.previousFrameUk.dispose();
        }
        this.previousFrameY = showNextChart(result.getY(), T, "y");
        this.previousFrameUk = showNextChart(result.getListOfUk(), T, "u");
    }

    private JFrame showNextChart(List<RealVector> vectors, double T, String seriesName) {
        XYChart chart = getChart(seriesName);

        ChartDto optimalDto = getChartDto(vectors, T);
        XYSeries optimalSeries = chart.addSeries(seriesName, optimalDto.getXData(), optimalDto.getYData());
        optimalSeries.setMarker(SeriesMarkers.NONE);
//        optimalSeries.setLineColor(FOREGROUND_COLOR);

        JFrame frame = new SwingWrapper<>(chart).displayChart();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        return frame;
    }

    private XYChart getChart(String seriesName) {
        XYChart chart = new XYChart(800, 450);
        chart.setTitle("Chart");
        chart.setXAxisTitle("t");
        chart.setYAxisTitle(String.format("%s(t)", seriesName));
//        chart.getStyler().setChartBackgroundColor(BACKGROUND_COLOR);
//        chart.getStyler().setLegendBackgroundColor(BACKGROUND_COLOR);
//        chart.getStyler().setPlotBackgroundColor(BACKGROUND_COLOR);
//
//        chart.getStyler().setChartFontColor(FOREGROUND_COLOR);
//        chart.getStyler().setAxisTickMarksColor(FOREGROUND_COLOR);
//        chart.getStyler().setAxisTickLabelsColor(FOREGROUND_COLOR);
//        chart.getStyler().setPlotGridLinesColor(FOREGROUND_COLOR);
        return chart;
    }

    private ChartDto getChartDto(List<RealVector> sequenceY, double T) {
        int factor = 1000;
        int step = Math.max(sequenceY.size() / factor, 1);
        List<Double> yData = new ArrayList<>(step);
        List<Double> xData = new ArrayList<>(step);
        for (int i = 0; i < sequenceY.size(); i += step) {
            RealVector vector = sequenceY.get(i);
            if (vector.getDimension() == 1) {
                yData.add(vector.getEntry(0));
                xData.add(i * T);
            }
        }
        return new ChartDto(xData, yData);
    }
}