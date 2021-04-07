package Analysis;

import javax.swing.*;
import java.awt.*;

public class ReviewControls extends JPanel {
    private boolean localised;
    private int[] timeLimit;
    private final int[] outlierLimits;
    private String title;
    private String xlabel;
    private String ylabel;

    public ReviewControls(){
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        localised = true; // Assumes localised is wanted
        outlierLimits = new int[2]; // Instantiates the limits
        timeLimit = new int[2]; // Automatic time limit is 20
        outlierLimits[0] = 1000; // Automatic upper limit is 1000
        outlierLimits[1] = 0; // Automatic lower limit is 0
        timeLimit[0]=0;
        timeLimit[1]=100;
        title = "Title";
        xlabel = "X label";
        ylabel = "Y label";

        GridBagConstraints localC = gridConstraints(0, 0, 4, 1, 0.25, 0.5, GridBagConstraints.NORTHWEST);
        GridBagConstraints outLabC = gridConstraints(5, 0, 1, 1, 0.06, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints outMinC = gridConstraints(6, 0, 1, 1, 0.06, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints outMinFieldC = gridConstraints(7, 0, 1, 1, 0.06, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints outMaxC = gridConstraints(8, 0, 1, 1, 0.06, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints outMaxFieldC = gridConstraints(9, 0, 1, 1, 0.06, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeLabelC = gridConstraints(10, 0, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeStartLabC = gridConstraints(12, 0, 1, 1, 0.06, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeStartC = gridConstraints(14, 0, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeEndLabC = gridConstraints(16, 0, 1, 1, 0.06, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints timeEndC = gridConstraints(16, 0, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints titleLabelC = gridConstraints(0, 1, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints titleC = gridConstraints(2, 1, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints xLabelC = gridConstraints(4, 1, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints xC = gridConstraints(6, 1, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints yLabelC = gridConstraints(8, 1, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);
        GridBagConstraints yC = gridConstraints(10, 1, 2, 1, 0.12, 0.5, GridBagConstraints.NORTH);


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
        });
        add(localisation, localC);

        // Outliers set the limit of acceptable - if the BR leaves the window then it is plotted as red and the alarm sounds
        JLabel outliers = new JLabel("Outliers - ");
        JLabel max = new JLabel("Max:");
        JLabel min = new JLabel("Min:");
        JFormattedTextField maxOut = new JFormattedTextField(outlierLimits[0]);
        maxOut.addActionListener(evt ->{
            outlierLimits[0] = ((Number)maxOut.getValue()).intValue();
        });

        JFormattedTextField minOut = new JFormattedTextField(outlierLimits[1]);
        minOut.addActionListener(evt ->{
            outlierLimits[1] = ((Number)minOut.getValue()).intValue();
        });
        add(outliers, outLabC);
        add(min, outMinC);
        add(minOut, outMinFieldC);
        add(max, outMaxC);
        add(maxOut, outMaxFieldC);



        JLabel timeLabel = new JLabel("Time Limit (s) - ");
        JLabel startLabel = new JLabel("Start:");
        JLabel endLabel = new JLabel("End:");
        JFormattedTextField startTime = new JFormattedTextField(this.timeLimit[0]);
        startTime.addActionListener(evt ->{
            this.timeLimit[0] = ((Number)startTime.getValue()).intValue();
        });
        JFormattedTextField endTime = new JFormattedTextField(this.timeLimit[1]);
        endTime.addActionListener(evt ->{
            this.timeLimit[1] = ((Number)endTime.getValue()).intValue();
        });

        add(timeLabel, timeLabelC);
        add(startLabel, timeStartLabC);
        add(startTime, timeStartC);
        add(endLabel, timeEndLabC);
        add(endTime, timeEndC);

        JLabel titleLabel = new JLabel("Title:");
        JFormattedTextField titleField = new JFormattedTextField(this.title);
        titleField.addActionListener(evt ->{
            this.title = String.valueOf(titleField.getValue());
        });

        add(titleLabel, titleLabelC);
        add(titleField, titleC);

        JLabel xlabelLabel = new JLabel("X Label:");
        JFormattedTextField xField = new JFormattedTextField(this.xlabel);
        xField.addActionListener(evt ->{
            this.xlabel = String.valueOf(xField.getValue());
        });

        add(xlabelLabel, xLabelC);
        add(xField, xC);

        JLabel ylabelLabel = new JLabel("Y Label:");
        JFormattedTextField yField = new JFormattedTextField(this.ylabel);
        yField.addActionListener(evt ->{
            this.ylabel = String.valueOf(yField.getValue());
        });
        add(ylabelLabel, yLabelC);
        add(yField, yC);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 50);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(200, 50);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200, 50);
    }

    public boolean getLocalised(){
        return localised;
    }

    public int[] getOutliers(){
        return outlierLimits;
    }

    public int[] getTimeLimit(){
        return timeLimit;
    }


    public String getTitle(){ return title; }

    public String getXLabel(){ return xlabel; }

    public String getYLabel(){ return ylabel; }

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
