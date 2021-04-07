package Analysis;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReviewGraph extends JPanel {

    private static class SETTINGS {
        private static final int BORDER_GAP = 80;
        private static final int TICK_SIZE = 6;
        private static final int PREF_W = 600;
        private static final int PREF_H = 600;
    }

    private final ArrayList<ArrayList<ArrayList<Float>>> data;
    private String title;
    private String xLabel;
    private String yLabel;
    private boolean localised;
    private int[] timeLimit;
    private int[] outliers;
    private float xscale;
    private float yscale;
    private int graphHeight;
    private int graphWidth;
    private int[] yLimit;

    public ReviewGraph(boolean localised, int[] timeLimit, int[] outLimits, String title, String xLabel, String yLabel, ArrayList<ArrayList<Float>>... data){
        this.data = new ArrayList<>();
        for(ArrayList<ArrayList<Float>> dat:data){
            this.data.add(dat);
        }
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.localised = localised;
        this.timeLimit = timeLimit;
        this.outliers = outLimits;
        yLimit = minMaxYValue();
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

        settings();
        List<List<Point>> graphPoints = getPoints();

        drawAxes(g2, g3);
        writeTitles(g2);
        Random rand = new Random();
        g2.setStroke(new BasicStroke(3f));
        for(List<Point> points: graphPoints) {
            g2.setColor(new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat()));
            for (int i = 0; i < points.size() - 1; i++) {
                int x1 = points.get(i).x;
                int y1 = points.get(i).y;
                int x2 = points.get(i + 1).x;
                int y2 = points.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }
        }
        // TODO: draw points and outliers
    }




    public void setLocalised(boolean local){
        this.localised = local;
        // TODO: reprint
    }

    public void setTitle(String title){
        this.title = title;
        // TODO: reprint
    }

    public void setXLabel(String XLabel){
        this.xLabel=XLabel;
        // TODO: reprint
    }

    public void setYLabel(String YLabel){
        this.yLabel=YLabel;
        // TODO: reprint
    }

    public void setTimeLimit(int[] timeLimit) {
        this.timeLimit = timeLimit;
        // TODO: reprint
    }

    public void setOutliers(int[] out){
        this.outliers=out;
        // TODO: reprint
    }

    private int[] minMaxYValue(){
        int[] output = {1000, 0}; // TODO: shouldn't be 10000
        for(ArrayList<ArrayList<Float>> dat:data){
            for(int i=0; i<dat.get(1).size(); i++) {
                if(dat.get(1).get(i)>timeLimit[1]){
                    break;
                }
                if(dat.get(1).get(i)>=timeLimit[0]){
                    output[0]= (int) (10*Math.floor((Math.min(dat.get(0).get(i), output[0]))/10));
                    output[1]= (int) (10*Math.ceil(Math.max(dat.get(0).get(i), output[1])/10));
                }
            }
        }
        return output;
    }

    private List<List<Point>> getPoints(){
        List<List<Point>> output = new ArrayList<>();
        for(ArrayList<ArrayList<Float>> dat:data){
            List<Point> points = new ArrayList<>();
            for(int i=0; i<dat.get(1).size(); i++) {
                if(dat.get(1).get(i)>timeLimit[1]){
                    break;
                }
                if(dat.get(1).get(i)>=timeLimit[0]){
                    float x = SETTINGS.BORDER_GAP+(dat.get(1).get(i)-timeLimit[0])*xscale;
                    float y = SETTINGS.BORDER_GAP+graphHeight-(dat.get(0).get(i)-yLimit[0])*yscale;
                    points.add(new Point((int)x,(int)y));
                }
            }
            output.add(points);
        }
        return output;
    }

    private void drawAxes(Graphics2D g2, Graphics g3){
        int originx = SETTINGS.BORDER_GAP;
        int originy = SETTINGS.BORDER_GAP+graphHeight;
        int yaxisMaxy = SETTINGS.BORDER_GAP;
        int xaxisMaxx = SETTINGS.BORDER_GAP+graphWidth;
        g2.setColor(new Color(95, 95, 95));
        g3.setColor(new Color(0,0,0));

        int xdiv = graphWidth/10;
        int ydiv = graphHeight/10; // TODO: algorithm for best position of grid (such that grid is integers)

        float xdivVal = (timeLimit[1]-timeLimit[0])/10;// TODO: changing hatch number?
        float ydivVal = (yLimit[1]-yLimit[0])/10;

        for(int i=0; i<11; i++){
            int yticky=originy-i*ydiv;
            int xtickx=originx+i*xdiv;
            g2.drawLine(originx-SETTINGS.TICK_SIZE,yticky,xaxisMaxx,yticky);// Draw horizontal lines
            g2.drawLine(xtickx,originy+SETTINGS.TICK_SIZE,xtickx,yaxisMaxy);// Draw vertical lines
            g3.drawString(String.valueOf(timeLimit[0]+i*xdivVal), xtickx-10, originy+20);// Write x tick values
            g3.drawString(String.valueOf(yLimit[0]+i*ydivVal), originx-60, yticky+5);// Write y tick values
        }
    }

    private void writeTitles(Graphics2D g2){
        g2.setColor(new Color(0, 0, 0));

        // Transformation that rotates text
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90), 0, 0);

        // Fonts for the titles
        Font titleFont = new Font(null, Font.PLAIN, 30);
        Font xFont = new Font(null, Font.PLAIN, 20);
        Font yFont = xFont.deriveFont(affineTransform);

        // Sizes of the fonts
        FontMetrics titMet = g2.getFontMetrics(titleFont);
        FontMetrics xMet = g2.getFontMetrics(xFont);
        FontMetrics yMet = g2.getFontMetrics(yFont);

        // Size of the text
        int titleWidth = titMet.stringWidth(title);
        int xWidth = xMet.stringWidth(xLabel);
        int yWidth = xMet.stringWidth(yLabel);

        // Positions of the Titles
        int titX = (getWidth()-titleWidth)/2;
        int titY = SETTINGS.BORDER_GAP/2;
        int xX = (getWidth()-xWidth)/2;
        int xY = getHeight()-SETTINGS.BORDER_GAP/2;
        int yX = SETTINGS.BORDER_GAP/2;
        int yY = (getHeight()+yWidth)/2;

        // Draw them all
        g2.setFont(titleFont);
        g2.drawString(title, titX, titY);
        g2.setFont(xFont);
        g2.drawString(xLabel, xX, xY);
        g2.setFont(yFont);
        g2.drawString(yLabel, yX, yY);

    }

    private void settings(){
        graphHeight=getHeight()-2*SETTINGS.BORDER_GAP;
        graphWidth=getWidth()-2*SETTINGS.BORDER_GAP;
        xscale = graphWidth/(timeLimit[1]-timeLimit[0]);
        yscale = graphHeight/(yLimit[1]-yLimit[0]);
    }


}
