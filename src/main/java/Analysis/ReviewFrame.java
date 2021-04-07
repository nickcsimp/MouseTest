package Analysis;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ReviewFrame extends JFrame {
    ReviewGraph graph;
    ReviewControls controls;
    public ReviewFrame(){
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationByPlatform(true);
        this.setVisible(true);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1;
        c.weighty=0.5;
        c.gridheight=1;
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;

        controls = new ReviewControls();

        ArrayList<Float> time = new ArrayList<>();
        ArrayList<Float> BR = new ArrayList<>();
        ArrayList<Float> HR = new ArrayList<>();
        Random rand = new Random();
        for(int i=0; i<=100; i++){
            time.add((float) i);
            BR.add(rand.nextFloat()*100);
            HR.add(rand.nextFloat()*10+50);
        }
        ArrayList<ArrayList<Float>> input = new ArrayList<>();
        input.add(BR);
        input.add(time);
        ArrayList<ArrayList<Float>> inputTwo = new ArrayList<>();
        inputTwo.add(HR);
        inputTwo.add(time);
        graph = new ReviewGraph(controls.getLocalised(), controls.getTimeLimit(), controls.getOutliers(), controls.getTitle(), controls.getXLabel(), controls.getYLabel(), input, inputTwo);

        add(graph, c);
        c.gridy = 1;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty=0.5;
        add(controls, c);
    }
}
