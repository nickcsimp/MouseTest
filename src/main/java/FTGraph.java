import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
public class FTGraph extends JPanel {
    private static final int PREF_W = 500;
    private static final int PREF_H = 500;
    private static final int BORDER_GAP = 80;
    private static final Color GRAPH_COLOR = Color.BLACK;
    private static final Color GRAPH_POINT_COLOR = new Color(50, 50, 50, 180);
    private static final Color OUTLIER_POINT_COLOR = new Color(255, 0, 0, 180);
    private static final Stroke GRAPH_STROKE = new BasicStroke(1f);
    private static final int GRAPH_POINT_WIDTH = 6;


    private double[] scores;

    private String title;
    private String xaxis;
    private String yaxis;

    private double hatchSizeX;
    private double xScale;
    private double hatchSizeY;
    private double yScale;
    private int graphWidth;
    private int graphHeight;
    double yMin;
    double yMax;
    double xMin;
    double xMax;
    double samplingFreq;

    public FTGraph(double[] data, double samplingFreq) {
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        scores = data;
        this.title = "Fourier Transform";
        this.yaxis="AU";
        this.xaxis="Frequency";
        this.samplingFreq=samplingFreq;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphWidth = getWidth()- BORDER_GAP * 2;
        graphHeight = getHeight()- BORDER_GAP * 2;

        doGraphSizes();

        List<Point> graphPoints = getPoints();

        yAxis(g2);
        xAxis(g2);
        labels(g2);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(GRAPH_COLOR);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        // sets the points in the line ( -----o-------o-------)
        g2.setStroke(oldStroke);
        g2.setColor(GRAPH_POINT_COLOR);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
            int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
            int ovalW = GRAPH_POINT_WIDTH;
            int ovalH = GRAPH_POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(PREF_W, PREF_H);
    }


    public double maximum(double[] list){
        double output = 0;
        for(int i=0; i<scores.length; i++) {
            if (list[i] > output) {
                output = list[i];
            }
        }
        return output;
    }

    public double minimum(double[] list){
        double output = maximum(list);
        for(int i=0; i<scores.length; i++){
            if(list[i]<output){
                output=list[i];
            }
        }
        return output;
    }

    private List<Point> getPoints(){
        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            int x1 = ((i * graphWidth / scores.length) + BORDER_GAP);
            int y1 = (int)((graphHeight + BORDER_GAP) - ((scores[i]-yMin) * graphHeight) / yScale);
            graphPoints.add(new Point(x1, y1));
        }

        return graphPoints;
    }

    private void doGraphSizes(){
        xMax = Math.PI;
        xMin = -Math.PI;

        xScale = xMax-xMin;
        hatchSizeX = samplingFreq/20;

        yMin = minimum(scores);
        yMax = maximum(scores);
        yScale=yMax-yMin;
        hatchSizeY=yScale/10;
    }

    private void yAxis(Graphics2D g2){
        for (int i = 0; i < 11; i++) {
            int x0 = BORDER_GAP;
            int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;

            int division = graphHeight/10;
            int y0 = getHeight()-BORDER_GAP-i*division;

            g2.drawLine(x0, y0, x1, y0);
            //g2.drawString(String.valueOf((hatchSizeY*i)+yMin), BORDER_GAP-30, y0+5);
            g2.drawLine(BORDER_GAP, y0, getWidth()-BORDER_GAP, y0);
        }
    }

    private void xAxis(Graphics2D g2){
        for (int i = 0; i < 11; i++) {
            int y0 = getHeight()-BORDER_GAP;
            int y1 = y0-GRAPH_POINT_WIDTH;

            int division = graphWidth/10;
            int x0 = BORDER_GAP+i*division;

            g2.drawLine(x0, y0, x0, y1);
            DecimalFormat df = new DecimalFormat("#.#");

            g2.drawString(String.valueOf(df.format(hatchSizeX * i)), x0 - 5, getHeight() - BORDER_GAP + 20);
            g2.drawLine(x0, getHeight() - BORDER_GAP, x0, BORDER_GAP);
        }
    }

    private void labels(Graphics2D g2){
        //Transformation that rotates text
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        //Make a font for the axis
        Font axisFont = new Font(null, Font.PLAIN, 20);
        FontMetrics axisMet = g2.getFontMetrics(axisFont);
        int xAxisWidth = axisMet.stringWidth(xaxis);
        int xAxisY = getHeight()-BORDER_GAP/2;
        int xAxisX = (getWidth()-xAxisWidth)/2;
        int yAxisWidth = axisMet.stringWidth(yaxis);
        int yAxisY = (getHeight()+yAxisWidth)/2;
        int yAxisX = BORDER_GAP/2;
        //Make a font for the title
        Font titleFont = new Font(null, Font.PLAIN, 30);
        FontMetrics titleMet = g2.getFontMetrics(titleFont);
        int titleWidth = titleMet.stringWidth(title);
        int titleY = BORDER_GAP/2;
        int titleX = (getWidth()-titleWidth)/2;
        //Make a font for the yaxis
        Font rotatedFont = axisFont.deriveFont(affineTransform);


        g2.setFont(titleFont);
        g2.drawString(title, titleX, titleY);
        g2.setFont(axisFont);
        g2.drawString(xaxis, xAxisX, xAxisY);
        g2.setFont(rotatedFont);
        g2.drawString(yaxis, yAxisX, yAxisY);
    }

}
