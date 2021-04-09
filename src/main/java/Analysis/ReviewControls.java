package Analysis;

import javax.swing.*;
import java.awt.*;

public class ReviewControls extends JPanel {
    private boolean localised;
    private int[] timeLimit;
    private ReviewGraph graph;

    public ReviewControls(ReviewGraph graph){
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        localised = true; // Assumes localised is wanted
        timeLimit = new int[2]; // Automatic time limit is 20
        timeLimit[0]=0;
        timeLimit[1]=10;

        GridBagConstraints localC = gridConstraints(0, 0, 3, 1, 0.6, 0.5, GridBagConstraints.NORTHWEST);
        GridBagConstraints timeLabelC = gridConstraints(3, 0, 1, 1, 0.2, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeStartLabC = gridConstraints(4, 0, 1, 1, 0.2, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeStartC = gridConstraints(5, 0, 1, 1, 0.2, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeEndLabC = gridConstraints(6, 0, 1, 1, 0.2, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeEndC = gridConstraints(7, 0, 1, 1, 0.2, 0.5, GridBagConstraints.NORTH);


        // If localised is true then the graph will not have y=0 in the bottom left
        // This means that the graph doesnt have 0-50 showing nothing when the BR is always 50-70 for example
        JButton localisation = new JButton("Localise Y Axis: On");
        localisation.addActionListener(evt ->{
            localised=!localised;
            if(localised){
                localisation.setText("Localise Y Axis: On");
            }else{
                localisation.setText("Localise Y Axis: Off");
            }
            graph.setLocalised(localised);
        });
        add(localisation, localC);

        JLabel timeLabel = new JLabel("Time Limit (s) - ");
        JLabel startLabel = new JLabel("Start:");
        JLabel endLabel = new JLabel("End:");
        JFormattedTextField startTime = new JFormattedTextField(this.timeLimit[0]);
        startTime.addActionListener(evt ->{
            this.timeLimit[0] = ((Number)startTime.getValue()).intValue();
            graph.setTimeLimit(timeLimit);
        });
        JFormattedTextField endTime = new JFormattedTextField(this.timeLimit[1]);
        endTime.addActionListener(evt ->{
            this.timeLimit[1] = ((Number)endTime.getValue()).intValue();
            graph.setTimeLimit(timeLimit);
        });

        add(timeLabel, timeLabelC);
        add(startLabel, timeStartLabC);
        add(startTime, timeStartC);
        add(endLabel, timeEndLabC);
        add(endTime, timeEndC);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 50);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(600, 50);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(600, 50);
    }

    public boolean getLocalised(){
        return localised;
    }

    public int[] getTimeLimit(){
        return timeLimit;
    }

    // Quicker way of defining gridbag constraints
    //TODO copied from home
    private GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy, int anchor){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        c.fill= GridBagConstraints.HORIZONTAL;
        c.anchor=anchor;
        return c;
    }

}
