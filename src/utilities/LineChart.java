
package utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class LineChart extends JComponent {

    Dimension d;
    Color fg = new Color( 20, 20, 150 );
    Color bg = Color.black; // new Color( 200, 200, 255 );
    ArrayList<LinePlot> lines;

    public static void main(String[] args) {
        LineChart lineChart = new LineChart();
        double[] y = new double[101];
        double[] yy = new double[101];
        for (int i=0; i<y.length; i++) {
            y[i] = Math.abs(50 - i);
            yy[i] = i;
        }
        lineChart.addLine(LinePlot.randomLine());
        lineChart.addLine(LinePlot.randomLine());
        lineChart.addLine(new LinePlot().setData(y).setColor(Color.blue));
        lineChart.addLine(new LinePlot().setData(yy).setColor(Color.red));
        new JEasyFrame( lineChart , "Line Chart Test");
        lineChart.xAxis = new LineChartAxis(new double[]{0, 20, 40, 60, 80, 100});
        // lineChart.yAxis = new LineChartAxis(new double[]{-1, 0, 1, 2, 3});
    }

    // set these up in ratios
    // right and top do not need as much space
    // as left and bottom due to the labelling
    // on the latter ones

    double leftMargin = 0.06;
    double rightMargin = leftMargin / 2;

    double bottomMargin = 0.1;
    double topMargin = bottomMargin / 2;

    public LineChartAxis xAxis, yAxis;

    public LineChart() {
        this(new Dimension(600, 350));
    }

    public LineChart(Dimension d) {

        this.d = d;
        lines = new ArrayList<>();

    }

//    public static void display(ArrayList<Double> arrayList, String title) {
//        LineChart lineChart = new LineChart();
//        lineChart.addLine(new LinePlot(arrayList));
//        new JEasyFrame(lineChart, title);
//    }
//
//    public static void display(double[] x, String title) {
//        LineChart lineChart = new LineChart();
//        lineChart.addLine(new LinePlot(x));
//        new JEasyFrame(lineChart, title);
//    }
//
//    public void addLine(double[] x) {
//        addLine(new LinePlot(x));
//    }

    public void addLine(LinePlot line) {
        lines.add(line);
    }


    public void paintComponent(Graphics go) {
        Graphics2D g = (Graphics2D) go;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension size = getSize();
        g.setColor( bg );
        g.translate(0, getHeight());
        g.scale(1,-1);
        g.fillRect( 0, 0, size.width, size.height );
        StatSummary sx = new StatSummary(), sy = new StatSummary();

        // first of all find the limits of the data

        if (xAxis != null) {
            sx = xAxis.range;
        } else {
            for (LinePlot line : lines) {
                // for sx we are only interested in the number of points on each line
                sx.add(line.getData().size());
            }
        }

        if (yAxis != null) {
            sy = yAxis.range;
        } else {
            for (LinePlot line : lines) {
                sy.add(line.sy);
            }
        }

        // now plot each of the lines, as a new Path2D
        double plotLeft = getWidth() * leftMargin;
        double plotRight = getWidth() * (1 - rightMargin);
        double plotTop = getHeight() * (1 - topMargin);
        double plotBottom = getHeight() * bottomMargin;

//        RangeMapper xMap = new RangeMapper(0, sx.max(), 0, getWidth());
//        RangeMapper yMap = new RangeMapper(sy.min(), sy.max(), getHeight(), 0);

//        double rw = getWidth() * (1 - (leftMargin + rightMargin));
//        double rh = getHeight() * (1 - (topMargin + bottomMargin));

        RangeMapper xMap = new RangeMapper(0, sx.max(), plotLeft, plotRight);
        RangeMapper yMap = new RangeMapper(sy.min(), sy.max(), plotBottom, plotTop);


        double rw = plotRight - plotLeft;
        double rh = plotTop - plotBottom;

        Rectangle2D.Double plotRegion = new Rectangle2D.Double(plotLeft, plotBottom, rw, rh);
        g.setColor(new Color(50, 50, 50));
        g.fill(plotRegion);
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.white);
        g.draw(plotRegion);
        System.out.println(plotRegion);


        for (LinePlot line : lines) {

            // for each line create a Path2D, then draw it

            ArrayList<Double> data = line.getData();
            Path2D.Double path = new Path2D.Double();
            path.moveTo(xMap.map(0), yMap.map(data.get(0)));

            // now draw lines to the other points
            for (int i=1; i<data.size(); i++) {
                path.lineTo(xMap.map(i), yMap.map(data.get(i)));
            }

            // now render the path on the component
            // by setting the color and then drawing it
            g.setColor(line.color);
            g.setStroke(new BasicStroke(3));
            g.draw(path);
        }
    }

    public Dimension getPreferredSize() {
        return d;
    }

    public LineChart addLines(List<LinePlot> linePlots) {

        for (LinePlot linePlot : linePlots) addLine(linePlot);
        return this;

    }
}

