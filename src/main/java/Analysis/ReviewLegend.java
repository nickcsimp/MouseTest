package Analysis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ReviewLegend extends JPanel {
    private int numberOfLines;
    private ArrayList<String> names;
    Color[] colors;
    ArrayList<int[]> outliers;
    ReviewGraph graph;

    public ReviewLegend(ReviewGraph graph){
        this.addMouseListener(new legendClicked(this));
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        this.setLayout(new GridBagLayout());
        this.graph=graph;
        names = new ArrayList<>();
        colors=graph.getColors();
        outliers = graph.getOutliers();
        numberOfLines=colors.length;
        for(int i=0; i<numberOfLines; i++){
            names.add("Dataset "+i);
            JLabel name = new JLabel(names.get(i));
            LegendLine line = new LegendLine(colors[i]);
            JLabel outLabel = new JLabel("Outliers - Min: "+outliers.get(i)[0]+" Max: "+outliers.get(i)[1]);

            GridBagConstraints nameC = gridConstraints(0, 0+2*i, 2, 1, 0.4, 0.5);
            GridBagConstraints lineC = gridConstraints(2, 0+2*i, 3, 1, 0.6, 0.5);
            GridBagConstraints outLabC = gridConstraints(0, 1+2*i, 5, 1, 1, 0.5);

            add(name, nameC);
            add(line, lineC);
            add(outLabel, outLabC);
        }
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, numberOfLines*75);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(200, numberOfLines*75);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200, numberOfLines*75);
    }

    public void refresh(){
        this.removeAll();
        for(int i=0; i<numberOfLines; i++){
            names.add("Dataset "+i);
            JLabel name = new JLabel(names.get(i));
            LegendLine line = new LegendLine(colors[i]);
            JLabel outLabel = new JLabel("Outliers - Min: "+outliers.get(i)[0]+" Max: "+outliers.get(i)[1]);

            GridBagConstraints nameC = gridConstraints(0, 0+2*i, 2, 1, 0.4, 0.5);
            GridBagConstraints lineC = gridConstraints(2, 0+2*i, 3, 1, 0.6, 0.5);
            GridBagConstraints outLabC = gridConstraints(0, 1+2*i, 5, 1, 1, 0.5);

            add(name, nameC);
            add(line, lineC);
            add(outLabel, outLabC);
        }
        this.revalidate();
        this.repaint();
        graph.revalidate();
        graph.repaint();
    }

    // Quicker way of defining gridbag constraints
    //TODO copied from home
    private GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        c.fill= GridBagConstraints.HORIZONTAL;
        return c;
    }

    public void setNames(ArrayList<String> name){
        names=name;
        repaint();
    }

    public ArrayList<String> getNames(){
        return names;
    }

    public void setOutliers(ArrayList<int[]> out){
        outliers=out;
        repaint();
    }

    public ArrayList<int[]> getOutliers(){
        return outliers;
    }

    public void setColors(Color[] colors){
        this.colors=colors;
        repaint();
    }

    public Color[] getColors(){
        return colors;
    }

    public int getNumberOfLines(){
        return numberOfLines;
    }

    public class legendClicked implements MouseListener {
        ReviewLegend legend;
        public legendClicked(ReviewLegend leg){
            legend = leg;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            new LegendPopUp(legend);
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }
}
