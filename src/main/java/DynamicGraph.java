import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
public class DynamicGraph extends JPanel {
    private static final int PREF_W = 800;
    private static final int PREF_H = 650;
    private static final int BORDER_GAP = 40;
    private static final Color GRAPH_COLOR = Color.BLACK;
    private static final Color GRAPH_POINT_COLOR = new Color(50, 50, 50, 180);
    private static final Stroke GRAPH_STROKE = new BasicStroke(1f);
    private static final int GRAPH_POINT_WIDTH = 6;
    private int TIME_LIMIT = 300;
    private List<Integer> scores;
    private boolean localised;
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

    public DynamicGraph(ArrayList<Integer> list, boolean localised, int timeLimit) {
        scores = list;
        this.localised = localised;
        TIME_LIMIT = timeLimit;
    }

    //TODO Take arduino input
    //TODO red if below certain value


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

    public int maximum(List<Integer> list){
        Integer output = 0;
        for(Integer i: list){
            if(i>output){
                output=i;
            }
        }
        return output;
    }

    public int minimum(List<Integer> list){
        Integer output = maximum(list);
        for(Integer i: list){
            if(i<output){
                output=i;
            }
        }
        return output;
    }

    private List<Point> getPoints(){
        List<Point> graphPoints = new ArrayList<>();
        if(maximumX<TIME_LIMIT) {
            for (int i = 0; i <= maximumX; i++) {
                int x1 = ((i * graphWidth / xScale) + BORDER_GAP);
                int y1 = (graphHeight + BORDER_GAP) - ((scores.get(minimumX+i)-yMin) * graphHeight) / yScale;
                graphPoints.add(new Point(x1, y1));
            }
        } else {
            for (int i = 0; i <= TIME_LIMIT; i++) {
                int x1 = ((i * graphWidth / xScale) + BORDER_GAP);
                int y1 = (graphHeight + BORDER_GAP) - ((scores.get(minimumX+i)-yMin) * graphHeight) / yScale;
                graphPoints.add(new Point(x1, y1));
            }
        }
        return graphPoints;
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

        maximumY= maximum(scores);
        minimumY = 0;
        if(localised){
            minimumY= minimum(scores);
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

            g2.drawLine(x0, y0, x0, y1);
            g2.drawString(String.valueOf((hatchSizeX * i)+xMin), x0 - 5, getHeight() - BORDER_GAP + 20);
            g2.drawLine(x0, getHeight() - BORDER_GAP, x0, BORDER_GAP);
        }
    }

}
