package Analysis;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReviewGraph extends JPanel {

    private static class SETTINGS {
        private static final int BORDER_GAP = 40;
        private static final int TOP_GAP = 5;
        private static final int TICK_SIZE = 6;
        private static final int PREF_W = 1100;
        private static final int PREF_H = 600;
        private static final int GRAPH_POINT_WIDTH = 8;
    }

    private final ArrayList<ArrayList<ArrayList<Double>>> data;
    private boolean localised;
    private int[] timeLimit;
    private double xscale;
    private double yscale;
    private int graphHeight;
    private int graphWidth;
    private int[] yLimit;
    private Color[] colors;
    private ArrayList<int[]> outlimits;
    private ArrayList<ArrayList<Boolean>> outliers;

    public ReviewGraph(ArrayList<ArrayList<Double>>... data){
        Random rand = new Random();
        int count=0;
        this.data = new ArrayList<>();
        outlimits = new ArrayList<>();
        for(ArrayList<ArrayList<Double>> dat:data){
            this.data.add(dat);
            count++;
            int[] outlier = new int[]{0, 1000};
            outlimits.add(outlier);
        }
        colors = new Color[count];
        for(int i=0; i<count; i++){
            colors[i] = new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat()); //TODO: put this all in one loop
        }
        this.localised = true;
        this.timeLimit = new int[]{0, 10};
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

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        Graphics2D g3 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        yLimit = minMaxYValue();
        settings();
        List<List<Point>> graphPoints = getPoints();

        drawAxes(g2, g3);
        g2.setStroke(new BasicStroke(3f));
        int count=0;
        for(List<Point> points: graphPoints) {
            g2.setColor(colors[count]);
            for (int i = 0; i < points.size() - 1; i++) {
                int x1 = points.get(i).x;
                int y1 = points.get(i).y;
                int x2 = points.get(i + 1).x;
                int y2 = points.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
                if(outliers.get(count).get(i)){
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(1));
                    int y = points.get(i).y - SETTINGS.GRAPH_POINT_WIDTH / 2;; // Moves the y coordinate by the graph point width
                    int x = points.get(i).x - SETTINGS.GRAPH_POINT_WIDTH / 2;; // Moves the y coordinate by the graph point width
                    g2.drawOval(x, y, SETTINGS.GRAPH_POINT_WIDTH, SETTINGS.GRAPH_POINT_WIDTH); // Draws it
                    g2.setColor(colors[count]);
                    g2.setStroke(new BasicStroke(3f));
                }
            }
            count++;
        }
    }




    public void setLocalised(boolean local){
        this.localised = local;
        revalidate();
        repaint();
    }

    public void setOutliers(ArrayList<int[]> outlay){
        this.outlimits=outlay;
        revalidate();
        repaint();
    }

    public ArrayList<int[]> getOutliers(){
        return outlimits;
    }

    public Color[] getColors(){
        return colors;
    }



    public void setTimeLimit(int[] timeLimit) {
        this.timeLimit = timeLimit;
        revalidate();
        repaint();
    }

    private int[] minMaxYValue(){
        int[] output = {1000, 0}; // TODO: shouldn't be 10000
        for(ArrayList<ArrayList<Double>> dat:data){
            for(int i=0; i<dat.get(1).size(); i++) {
                if(dat.get(1).get(i)>timeLimit[1]){
                    break;
                }
                if(dat.get(1).get(i)>=timeLimit[0]){
                    output[0]= (int) (10*Math.floor((Math.min(dat.get(0).get(i), output[0]))/10));
                    output[1]= (int) (10*Math.ceil(Math.max(dat.get(0).get(i), output[1])/10));
                }
            }
            if(!localised){
                output[0]=0;
            }
        }
        return output;
    }

    private List<List<Point>> getPoints(){
        outliers = new ArrayList<>();
        List<List<Point>> output = new ArrayList<>();
        int count = 0;
        for(ArrayList<ArrayList<Double>> dat:data){
            List<Point> points = new ArrayList<>();
            ArrayList<Boolean> liers = new ArrayList<>();
            for(int i=0; i<dat.get(1).size(); i++) {
                if(dat.get(1).get(i)>timeLimit[1]){
                    break;
                }
                if(dat.get(1).get(i)>=timeLimit[0]){
                    double x = SETTINGS.BORDER_GAP+((dat.get(1).get(i)-timeLimit[0])*xscale)/100;
                    double y = SETTINGS.TOP_GAP+graphHeight-((dat.get(0).get(i)-yLimit[0])*yscale)/100;
                    points.add(new Point((int)x,(int)y));
                    if(dat.get(0).get(i)>outlimits.get(count)[1] || dat.get(0).get(i)<outlimits.get(count)[0]){
                        liers.add(true);
                    }
                    else {
                        liers.add(false);
                    }
                }
            }
            outliers.add(liers);
            output.add(points);
            count++;
        }
        return output;
    }

    private void drawAxes(Graphics2D g2, Graphics g3){
        double numberOfHatches=10; // TODO: algorithm for best position of grid (such that grid is integers)
        int originx = SETTINGS.BORDER_GAP;
        int originy = SETTINGS.TOP_GAP+graphHeight;
        int yaxisMaxy = SETTINGS.TOP_GAP;
        int xaxisMaxx = SETTINGS.BORDER_GAP+graphWidth;
        g2.setColor(new Color(95, 95, 95));
        g3.setColor(new Color(0,0,0));

        double xdivVal = (timeLimit[1]-timeLimit[0])/numberOfHatches;
        double ydivVal = (yLimit[1]-yLimit[0])/numberOfHatches;

        for(int i=0; i<11; i++){
            int xtick = (int) (SETTINGS.BORDER_GAP+graphWidth*i/numberOfHatches);
            int ytick = (int) (SETTINGS.TOP_GAP+graphHeight*i/numberOfHatches);
            g2.drawLine(originx-SETTINGS.TICK_SIZE,ytick,xaxisMaxx,ytick);// Draw horizontal lines
            g2.drawLine(xtick,originy+SETTINGS.TICK_SIZE,xtick,yaxisMaxy);// Draw vertical lines
            g3.drawString(String.valueOf(timeLimit[0]+i*xdivVal), xtick-10, originy+20);// Write x tick values
            g3.drawString(String.valueOf(yLimit[1]-i*ydivVal), originx-40, ytick+5);// Write y tick values
        }
    }

    private void settings(){
        graphHeight=getHeight()-SETTINGS.BORDER_GAP-SETTINGS.TOP_GAP;
        graphWidth=getWidth()-2*SETTINGS.BORDER_GAP;
        xscale = 100*graphWidth/(timeLimit[1]-timeLimit[0]);
        yscale = 100*graphHeight/(yLimit[1]-yLimit[0]);
    }

}
