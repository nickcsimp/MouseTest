package Analysis;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReviewLegend extends JPanel {
    private int numberOfLines;
    public ReviewLegend(ReviewGraph graph){
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        this.setLayout(new GridBagLayout());
        Color[] colors=graph.getColors();
        ArrayList<int[]> outliers = graph.getOutliers();
        numberOfLines=colors.length;
        for(int i=0; i<numberOfLines; i++){
            JLabel name = new JLabel(String.valueOf(i+1));
            LegendLine line = new LegendLine(colors[i]);
            JLabel outLabel = new JLabel("Outliers - ");
            JLabel minLabel = new JLabel("Min:");
            JLabel maxLabel = new JLabel("Max:");
            JFormattedTextField minOut = new JFormattedTextField(String.valueOf(outliers.get(i)[0]));
            JFormattedTextField maxOut = new JFormattedTextField(String.valueOf(outliers.get(i)[1]));
            int finalI = i;
            minOut.addActionListener(evt -> {
                outliers.get(finalI)[0]=Double.valueOf((String)minOut.getValue()).intValue();
                graph.setOutliers(outliers);
            });
            maxOut.addActionListener(evt -> {
                outliers.get(finalI)[1]=Double.valueOf((String)maxOut.getValue()).intValue();
                graph.setOutliers(outliers);
            });

            GridBagConstraints nameC = gridConstraints(0, 0+2*i, 2, 1, 0.4, 0.5);
            GridBagConstraints lineC = gridConstraints(2, 0+2*i, 3, 1, 0.6, 0.5);
            GridBagConstraints outLabC = gridConstraints(0, 1+2*i, 1, 1, 0.2, 0.5);
            GridBagConstraints outMinLabC = gridConstraints(1, 1+2*i, 1, 1, 0.2, 0.5);
            GridBagConstraints outMinC = gridConstraints(2, 1+2*i, 1, 1, 0.2, 0.5);
            GridBagConstraints outMaxLabC = gridConstraints(3, 1+2*i, 1, 1, 0.2, 0.5);
            GridBagConstraints outMaxC = gridConstraints(4, 1+2*i, 1, 1, 0.2, 0.5);

            add(name, nameC);
            add(line, lineC);
            add(outLabel, outLabC);
            add(minLabel, outMinLabC);
            add(minOut, outMinC);
            add(maxLabel, outMaxLabC);
            add(maxOut, outMaxC);
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
}
