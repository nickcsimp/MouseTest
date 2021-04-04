import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.lang.Math;

public class RawGraph extends JPanel {

    private static class SETTINGS {
        private static final int PREF_W = 600;
        private static final int PREF_H = 600;
        private static final int BORDER_GAP = 80;
        private static final Color GRAPH_COLOR = Color.BLACK;
        private static final Color GRAPH_POINT_COLOR = new Color(50, 50, 50, 180);
        private static final Color OUTLIER_POINT_COLOR = new Color(255, 0, 0, 180);
        private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
        private static final int GRAPH_POINT_WIDTH = 6;
    }

    private final int timeLimit;
    private final List<Integer> scores;
    private final boolean localised;
    private final String title;
    private final String xaxis;
    private final String yaxis;
    private int maximumX;
    private int minimumX;
    private int hatchSizeX;
    private int xScale;
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
        scores = data;
        this.localised = localised;
        this.timeLimit = timeLimit*samplingFreq;
        this.title = title;
        this.yaxis=yaxis;
        this.xaxis=xaxis;
        this.outliers = outLimits;
    }

    // This is the bulk
    // TODO would be nice to edit current graph rather than make a new one but might be a lot of effort
    @Override
    protected void paintComponent(Graphics g) {
        // Setup
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        Graphics2D g3 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Uses frame dimensions to find how big the graph will be
        graphWidth = getWidth() - SETTINGS.BORDER_GAP * 2;
        graphHeight = getHeight() - SETTINGS.BORDER_GAP * 2;

        setGraphSizes();// Finds necessary dimensions
        outlierBool = new boolean[1+maximumX-minimumX]; // Instantiates outlier array
        List<Point> graphPoints = getGraphPoints(); // Calculates x and y coordinates

        drawYAxis(g2, g3); // Draws the yaxis
        drawXAxis(g2, g3); // Draws the xaxis
        writeLabels(g2); // Draws the words

        Stroke oldStroke = g2.getStroke(); // Saves current stroke (How the thing gets drawn)

        // Draws all lines between points
        g2.setColor(SETTINGS.GRAPH_COLOR);
        g2.setStroke(SETTINGS.GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        // Sets the points in the line ( -----o-------o-------)
        g2.setStroke(oldStroke); //Sets stroke back to how it was
        g2.setColor(SETTINGS.GRAPH_POINT_COLOR);
        // Draws all points
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - SETTINGS.GRAPH_POINT_WIDTH / 2; // Moves the x coordinate by the graph point width
            if(outlierBool[i]){ // If there is an outlier, paint it red
                g2.setColor(SETTINGS.OUTLIER_POINT_COLOR);
                if(i==graphPoints.size()-1){ // If the outlier is the most recent one then alert the user
                    makeNoise();
                }
            }
            int y = graphPoints.get(i).y - SETTINGS.GRAPH_POINT_WIDTH / 2;; // Moves the y coordinate by the graph point width
            int ovalW = SETTINGS.GRAPH_POINT_WIDTH; // Sets oval width
            int ovalH = SETTINGS.GRAPH_POINT_WIDTH; // Sets oval height
            g2.fillOval(x, y, ovalW, ovalH); // Draws it
            if(outlierBool[i]){
                g2.setColor(SETTINGS.GRAPH_POINT_COLOR); // Changes it back to normal if red was used
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SETTINGS.PREF_W, SETTINGS.PREF_H);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(SETTINGS.PREF_W, SETTINGS.PREF_H);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(SETTINGS.PREF_W, SETTINGS.PREF_H);
    }

    // Finds the maximum number in the data to set the y axis maximum
    public int maximum(List<Integer> list, int upper, int lower){
        Integer output = 0;
        for(int i=lower; i<=upper; i++) {
            if (list.get(i) > output) {
                output = list.get(i);
            }
        }
        return output;
    }

    // Finds the minimum number in the data to set the y axis minimnum
    public int minimum(List<Integer> list, int upper, int lower){
        Integer output = maximum(list, upper, lower); // Uses max as a limit instead of eg. 1000000
        for(int i=lower; i<=upper; i++){
            if(list.get(i)<output){
                output=list.get(i);
            }
        }
        return output;
    }

    // Does some maths on the screen dimensions to find where each point should go on the graph
    private List<Point> getGraphPoints(){
        List<Point> graphPoints = new ArrayList<>();
        int cutoff = Math.min(maximumX, timeLimit);
        for (int i = 0; i <= cutoff; i++) {
            // Highlights points that should be red
            outlierBool[i]=isOutlier(scores.get(minimumX+i));
            // x values depend on screen size, border size and number of points to plot
            int x1 = ((i * graphWidth / xScale) + SETTINGS.BORDER_GAP);
            // y values depend on screen size, border size and difference between max and min y values
            int y1 = (graphHeight + SETTINGS.BORDER_GAP) - ((scores.get(minimumX+i)-yMin) * graphHeight) / yScale;
            graphPoints.add(new Point(x1, y1));
        }
        return graphPoints;
    }

    // Determines if a point lies outside of the designated acceptable region
    private boolean isOutlier(int i){
        return i < outliers[1] || i > outliers[0];
    }

    // This uses the screen dimensions to find where points can be put
    private void setGraphSizes(){
        maximumX= scores.size()-1; // Number of points that can be plotted
        minimumX =0; // Assumes a minimum of 0
        if (maximumX< timeLimit){ // Time limit is the user defined x axis size
            xMin = 0; // If we have fewer points than the user designates then we start from the first value in data
            xMax = (((maximumX-1) / 10) + 1) * 10; // This puts the rhs of the x axis at the next 10 seconds
        }
        else {
            minimumX=maximumX- timeLimit; // Minimum x value leaves only the user defined number of points on the graph
            xMin = minimumX;
            xMax = maximumX;
        }

        xScale = xMax-xMin; // The scale is the difference between the max and the min - used for plotting points
        hatchSizeX = xScale/10; // Hatch is where the lines are drawn on the graph

        int maximumY = maximum(scores, maximumX, minimumX); // Finds the maximum y value
        int minimumY = 0; // Assumes a minimum of 0
        if (localised) {
            minimumY = minimum(scores, maximumX, minimumX ); // If we are focusing on where the points are then the minimum value is found
        }
        yMin = (minimumY /10)*10; // Rounds down to nearest integer value
        yMax = ((maximumY /10)+1)*10; // Rounds up to nearest integer
        yScale=yMax-yMin; // Scales the graph
        hatchSizeY=yScale/10; // Where the gridlines sit
    }

    private void drawYAxis(Graphics2D g2, Graphics g3){
        //TODO not all in loop
        for (int i = 0; i < 11; i++) { // Draws 10 lines
            int x0 = SETTINGS.BORDER_GAP; //
            int x1 = SETTINGS.GRAPH_POINT_WIDTH + SETTINGS.BORDER_GAP;

            int division = graphHeight/10;
            int y0 = getHeight()-SETTINGS.BORDER_GAP-i*division;

            g2.setColor(new Color(95, 95, 95));
            g2.drawLine(x0, y0, x1, y0); // Not sure tbh
            g2.drawLine(SETTINGS.BORDER_GAP, y0, getWidth()-SETTINGS.BORDER_GAP, y0); // Draws the line from x_0 to x_n
            g3.setColor(new Color(0, 0, 0));
            g3.drawString(String.valueOf((hatchSizeY*i)+yMin), SETTINGS.BORDER_GAP-30, y0+5); // Writes the number on the x axis
        }
    }

    private void drawXAxis(Graphics2D g2, Graphics2D g3){
        for (int i = 0; i < 11; i++) {
            int y0 = getHeight()-SETTINGS.BORDER_GAP;
            // TODO: What is the variable y1 for?
            int y1 = y0-SETTINGS.GRAPH_POINT_WIDTH;

            int division = graphWidth/10;
            int x0 = SETTINGS.BORDER_GAP+i*division;

            DecimalFormat df = new DecimalFormat("#.#"); // Formats the x axis numbers

            g2.setColor(new Color(95, 95, 95)); // Sets color for axis lines (slightly lighter so signal is easy to see)
            g2.drawLine(x0, getHeight() - SETTINGS.BORDER_GAP, x0, SETTINGS.BORDER_GAP); // Draws the gridline
            g3.setColor(new Color(0, 0, 0)); // Sets color for numbers in the axis
            // Writes the numbers on the axis
            g3.drawString(String.valueOf(df.format((double)((hatchSizeX * i)+xMin)/ GlobalSettings.INSTANCE.getSamplingFrequency())), x0 - 5, getHeight() - SETTINGS.BORDER_GAP + 20);
        }
        xMin = ((hatchSizeX * 11)+xMin)/GlobalSettings.INSTANCE.getSamplingFrequency();
    }

    // Writes the title and axis titles
    private void writeLabels(Graphics2D g2){
        g2.setColor(new Color(0, 0, 0));
        // Transformation that rotates text
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        // Make a font for the axis
        Font axisFont = new Font(null, Font.PLAIN, 20);
        FontMetrics axisMet = g2.getFontMetrics(axisFont);
        int xAxisWidth = axisMet.stringWidth(xaxis);
        int xAxisY = getHeight()-SETTINGS.BORDER_GAP/2;
        int xAxisX = (getWidth()-xAxisWidth)/2;
        int yAxisWidth = axisMet.stringWidth(yaxis);
        int yAxisY = (getHeight()+yAxisWidth)/2;
        int yAxisX = SETTINGS.BORDER_GAP/2;
        // Make a font for the title
        Font titleFont = new Font(null, Font.PLAIN, 30);
        FontMetrics titleMet = g2.getFontMetrics(titleFont);
        int titleWidth = titleMet.stringWidth(title);
        int titleY = SETTINGS.BORDER_GAP/2;
        int titleX = (getWidth()-titleWidth)/2;
        // Make a font for the yaxis
        Font rotatedFont = axisFont.deriveFont(affineTransform);
        g2.setFont(titleFont);
        g2.drawString(title, titleX, titleY);
        g2.setFont(axisFont);
        g2.drawString(xaxis, xAxisX, xAxisY);
        g2.setFont(rotatedFont);
        g2.drawString(yaxis, yAxisX, yAxisY);
    }

    // Alerts the user
    // TODO this is horrible noise
    // TODO add in visuals such as vring to front of screen
    private void makeNoise() {
        // Copied from online so feel free to edit
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
