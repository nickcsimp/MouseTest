import javax.swing.*;
import java.awt.*;

public class GraphControls extends JPanel {
    private boolean localised;
    private boolean outlier;
    private int timeLimited;
    public GraphControls(){

        localised = true;
        outlier = false;
        timeLimited = 20;

        this.setPreferredSize(new Dimension(200,650));

        JRadioButton localisation = new JRadioButton("Localise Y Axis", localised);
        localisation.addActionListener(evt ->{
            localised=localisation.isSelected();
        });
        add(localisation);

        JRadioButton outliers = new JRadioButton("Highlight Outliers", outlier);
        localisation.addActionListener(evt ->{
            outlier=outliers.isSelected();
        });
        add(outliers);

        JLabel timeLabel = new JLabel("Time Limit (s)");
        JFormattedTextField timeLimit = new JFormattedTextField(timeLimited);
        timeLimit.addActionListener(evt ->{
            timeLimited = ((Number)timeLimit.getValue()).intValue();
        });


        add(timeLimit);
        add(timeLabel);
    }

    public boolean getLocalised(){
        return localised;
    }

    public boolean getOutliers(){
        return outlier;
    }

    public int getTimeLimit(){
        return timeLimited;
    }
}
