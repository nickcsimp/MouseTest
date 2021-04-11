package Analysis;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ReviewFrame extends JFrame {

    public ReviewFrame(){
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationByPlatform(true);
        this.setVisible(true);
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints graphC = gridConstraints(0, 0, 8, 8, 0.8, 0.8, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        GridBagConstraints controlsC = gridConstraints(0, 8, 8, 2, 0.8, 0.2, GridBagConstraints.NONE, GridBagConstraints.SOUTH);
        GridBagConstraints legendC = gridConstraints(9, 0, 2, 10, 0.8, 1, GridBagConstraints.NONE, GridBagConstraints.EAST);
        ArrayList<Double> time = new ArrayList<>();
        ArrayList<Double> BR = new ArrayList<>();
        ArrayList<Double> HR = new ArrayList<>();
        Random rand = new Random();
        for(int i=0; i<=100; i++){
            time.add((double) i);
            BR.add((double) (rand.nextFloat()*100));
            HR.add((double) (rand.nextFloat()*10+50));
        }
        ArrayList<ArrayList<Double>> input = new ArrayList<>();
        input.add(BR);
        input.add(time);
        ArrayList<ArrayList<Double>> inputTwo = new ArrayList<>();
        inputTwo.add(HR);
        inputTwo.add(time);
        ReviewGraph graph = new ReviewGraph(input, inputTwo);
        ReviewPanel panel = new ReviewPanel(graph);
        ReviewControls controls = new ReviewControls(graph);
        ReviewLegend legend = new ReviewLegend(graph);

        add(legend, legendC);
        add(panel, graphC);
        add(controls, controlsC);

        pack();
        revalidate();
        repaint();
    }


    // Quicker way of defining gridbag constraints
    //TODO copied from home
    private GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy, int fill, int anchor){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        c.fill = fill;
        c.anchor=anchor;
        return c;
    }
}
