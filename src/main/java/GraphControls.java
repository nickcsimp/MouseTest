import javax.swing.*;
import java.awt.*;

public class GraphControls extends JPanel {
    private boolean localised;
    private int timeLimited;
    private int outlierLimits[];

    public GraphControls(){
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        localised = true;
        outlierLimits = new int[2];
        timeLimited = 20;
        outlierLimits[0] = 1000;
        outlierLimits[1] = 0;

        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.weightx=1;
        c.weighty=1;
        c.gridheight=1;
        c.gridwidth=4;
        //c.anchor=GridBagConstraints.WEST;
        JButton localisation = new JButton("Localise Y Axis: On");
        localisation.addActionListener(evt ->{
            localised=!localised;
            if(localised){
                localisation.setText("Localise Y Axis: On");
            }else{
                localisation.setText("Localise Y Axis: Off");
            }
        });
        add(localisation, c);

        c.gridx=5;
        c.gridwidth=1;
        c.weightx=0.25;
        //c.anchor=GridBagConstraints.CENTER;
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
        add(outliers, c);
        c.gridx=6;
        add(min, c);
        c.gridx=7;
        add(minOut, c);
        c.gridx=8;
        add(max, c);
        c.gridx=9;
        add(maxOut, c);

        c.gridx=10;
        c.gridwidth=2;
        c.weightx=0.5;
        //c.anchor=GridBagConstraints.EAST;
        JLabel timeLabel = new JLabel("Time Limit (s):");
        JFormattedTextField timeLimit = new JFormattedTextField(timeLimited);
        timeLimit.addActionListener(evt ->{
            timeLimited = ((Number)timeLimit.getValue()).intValue();
        });

        add(timeLabel, c);
        c.gridx=12;
        add(timeLimit, c);
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

    public int getTimeLimit(){
        return timeLimited;
    }
}
