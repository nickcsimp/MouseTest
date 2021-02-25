import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;

@SuppressWarnings("serial")
public class RawGraph extends JPanel {
    private static final int PREF_W = 600;
    private static final int PREF_H = 600;
    private static final int BORDER_GAP = 80;
    private static final Color GRAPH_COLOR = Color.BLACK;
    private static final Color GRAPH_POINT_COLOR = new Color(50, 50, 50, 180);
    private static final Color OUTLIER_POINT_COLOR = new Color(255, 0, 0, 180);
    private static final Stroke GRAPH_STROKE = new BasicStroke(1f);
    private static final int GRAPH_POINT_WIDTH = 6;

    private int samplingFreq;
    private int TIME_LIMIT;
    private List<Integer> scores;
    private boolean localised;
    private String title;
    private String xaxis;
    private String yaxis;
    private int maximumX;
    private int minimumX;
    private int hatchSizeX;
    private int xScale;
    private int maximumY;
    private int minimumY;
    private int hatchSizeY;
    private int yScale;
    private int graphWidth;
    private int graphHeight;
    int yMin;
    int yMax;
    int xMin;
    int xMax;
    int[] outliers;
    private boolean[] outlierBool;

    public RawGraph(ArrayList<Integer> data, String title, String yaxis, String xaxis, boolean localised, int timeLimit, int[] outLimits, int samplingFreq) {
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        this.samplingFreq=samplingFreq;
        scores = data;
        this.localised = localised;
        TIME_LIMIT = timeLimit*samplingFreq;
        this.title = title;
        this.yaxis=yaxis;
        this.xaxis=xaxis;
        this.outliers = outLimits;
    }

    //TODO think about sampling frequency

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphWidth = getWidth()- BORDER_GAP * 2;
        graphHeight = getHeight()- BORDER_GAP * 2;

        doGraphSizes();
        outlierBool = new boolean[1+maximumX-minimumX];
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
            if(outlierBool[i]){
                g2.setColor(OUTLIER_POINT_COLOR);
                if(i==graphPoints.size()-1){
                    makeNoise();
                }
            }
            int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
            int ovalW = GRAPH_POINT_WIDTH;
            int ovalH = GRAPH_POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
            if(outlierBool[i]){
                g2.setColor(GRAPH_POINT_COLOR);
            }
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


    public int maximum(List<Integer> list, int upper, int lower){
        Integer output = 0;
        for(int i=lower; i<=upper; i++) {
            if (list.get(i) > output) {
                output = list.get(i);
            }
        }
        return output;
    }

    public int minimum(List<Integer> list, int upper, int lower){
        Integer output = maximum(list, upper, lower);
        for(int i=lower; i<=upper; i++){
            if(list.get(i)<output){
                output=list.get(i);
            }
        }
        return output;
    }

    private List<Point> getPoints(){
        List<Point> graphPoints = new ArrayList<>();
        if(maximumX<TIME_LIMIT) {
            for (int i = 0; i <= maximumX; i++) {
                outlierBool[i]=isOutlier(scores.get(minimumX+i));
                int x1 = ((i * graphWidth / xScale) + BORDER_GAP);
                int y1 = (graphHeight + BORDER_GAP) - ((scores.get(minimumX+i)-yMin) * graphHeight) / yScale;
                graphPoints.add(new Point(x1, y1));
            }
        } else {//TODO two identical loops!
            for (int i = 0; i <= TIME_LIMIT; i++) {
                outlierBool[i]=isOutlier(scores.get(minimumX+i));
                int x1 = ((i * graphWidth / xScale) + BORDER_GAP);
                int y1 = (graphHeight + BORDER_GAP) - ((scores.get(minimumX+i)-yMin) * graphHeight) / yScale;
                graphPoints.add(new Point(x1, y1));
            }
        }
        return graphPoints;
    }

    private boolean isOutlier(int i){
        if(i<outliers[1] || i>outliers[0]){
            return true;
        }
        return false;
    }

    private void doGraphSizes(){
        maximumX= scores.size()-1;
        minimumX =0;
        if(maximumX<TIME_LIMIT){
            xMin = 0;
            xMax = (((maximumX-1) / 10) + 1) * 10;
        }
        else{
            minimumX=maximumX-TIME_LIMIT;
            xMin = minimumX;
            xMax = maximumX;
        }

        xScale = xMax-xMin;
        hatchSizeX = xScale/10;

        maximumY= maximum(scores, maximumX, minimumX);
        minimumY = 0;
        if(localised){
            minimumY= minimum(scores, maximumX, minimumX );
        }
        yMin = (minimumY/10)*10;
        yMax = ((maximumY/10)+1)*10;
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
            g2.drawString(String.valueOf((hatchSizeY*i)+yMin), BORDER_GAP-30, y0+5);
            g2.drawLine(BORDER_GAP, y0, getWidth()-BORDER_GAP, y0);
        }
    }

    private void xAxis(Graphics2D g2){
        for (int i = 0; i < 11; i++) {
            int y0 = getHeight()-BORDER_GAP;
            int y1 = y0-GRAPH_POINT_WIDTH;

            int division = graphWidth/10;
            int x0 = BORDER_GAP+i*division;

            DecimalFormat df = new DecimalFormat("#.#");

            g2.drawLine(x0, y0, x0, y1);
            g2.drawString(String.valueOf(df.format((double)((hatchSizeX * i)+xMin)/samplingFreq)), x0 - 5, getHeight() - BORDER_GAP + 20);
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

    void makeNoise() {
        byte[] buf = new byte[ 1 ];;
        AudioFormat af = new AudioFormat( (float )44100, 8, 1, true, false );
        SourceDataLine sdl = null;
        try {
            sdl = AudioSystem.getSourceDataLine( af );
            sdl.open();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        assert sdl != null;
        sdl.start();
        for( int i = 0; i < 1000 * (float )44100 / 1000; i++ ) {
            double angle = i / ( (float )44100 / 440 ) * 2.0 * Math.PI;
            buf[ 0 ] = (byte )( Math.sin( angle ) * 100 );
            sdl.write( buf, 0, 1 );
        }
        sdl.drain();
        sdl.stop();
    }

}