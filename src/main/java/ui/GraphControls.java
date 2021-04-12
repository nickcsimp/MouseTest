package ui;

import helpers.GlobalSettings;
import helpers.Helpers;

import javax.swing.*;
import java.awt.*;

public class GraphControls extends JPanel {

    private static class SETTINGS {
        private static final GridBagConstraints LOCALISE_BUTTON_CONSTRAINTS = Helpers.gridConstraints(0,1,4,1,1,1);
        private static final GridBagConstraints OUTLIERS_TITLE_CONSTRAINTS = Helpers.gridConstraints(5,1,1,1,0.25,1);
        private static final GridBagConstraints MIN_LABEL_CONSTRAINTS = Helpers.gridConstraints(6,1,1,1,0.25,1);
        private static final GridBagConstraints MIN_OUT_CONSTRAINTS = Helpers.gridConstraints(7,1,1,1,0.25,1);
        private static final GridBagConstraints MAX_LABEL_CONSTRAINTS = Helpers.gridConstraints(8,1,1,1,0.25,1);
        private static final GridBagConstraints MAX_OUT_CONSTRAINTS = Helpers.gridConstraints(9,1,1,1,0.25,1);
        private static final GridBagConstraints TIME_LABEL_CONSTRAINTS = Helpers.gridConstraints(10,1,2,1,0.5,1);
        private static final GridBagConstraints TIME_LIMIT_CONSTRAINTS = Helpers.gridConstraints(12,1,2,1,0.5,1);
        private static final String TIME_LIMIT_TEXT = "Time Limit (s):";
        private static final String LOCALISE_ON_TEXT = "Localise Y Axis: On";
        private static final String LOCALISE_OFF_TEXT = "Localise Y Axis: Off";
        private static final String OUTLIERS_TEXT = "Outliers - ";
        private static final String MAX_TEXT = "Max:";
        private static final String MIN_TEXT = "Min:";
        private static final Dimension DIMS = new Dimension(200, 50);
    }

    public GraphControls() {
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        setupLocalisation();
        setupTimeLimit();
        setupOutliers();
    }

    private void setupLocalisation() {
        // If localised is true then the graph will not have y=0 in the bottom left
        // This means that the graph doesnt have 0-50 showing nothing when the BR is always 50-70 for example
        JButton localisation = new JButton(SETTINGS.LOCALISE_ON_TEXT);
        localisation.addActionListener(evt ->{
            boolean l = GlobalSettings.INSTANCE.isLocalised();
            GlobalSettings.INSTANCE.setLocalised(!l);
            localisation.setText(l ? SETTINGS.LOCALISE_ON_TEXT : SETTINGS.LOCALISE_OFF_TEXT);
        });
        add(localisation, SETTINGS.LOCALISE_BUTTON_CONSTRAINTS);
    }

    private void setupOutliers() {
        // Outliers set the limit of acceptable - if the BR leaves the window then it is plotted as red and the alarm sounds
        JLabel outliers = new JLabel(SETTINGS.OUTLIERS_TEXT);
        JLabel maxLabel = new JLabel(SETTINGS.MAX_TEXT);
        JLabel minLabel = new JLabel(SETTINGS.MIN_TEXT);

        JFormattedTextField minOut = new JFormattedTextField(GlobalSettings.INSTANCE.getOutlierMin());
        minOut.addActionListener(evt ->{
            GlobalSettings.INSTANCE.setOutlierMin(((Number)minOut.getValue()).intValue());
        });

        JFormattedTextField maxOut = new JFormattedTextField(GlobalSettings.INSTANCE.getOutlierMax());
        maxOut.addActionListener(evt ->{
            GlobalSettings.INSTANCE.setOutlierMax(((Number)maxOut.getValue()).intValue());
        });

        add(outliers, SETTINGS.OUTLIERS_TITLE_CONSTRAINTS);
        add(minLabel, SETTINGS.MIN_LABEL_CONSTRAINTS);
        add(minOut, SETTINGS.MIN_OUT_CONSTRAINTS);
        add(maxLabel, SETTINGS.MAX_LABEL_CONSTRAINTS);
        add(maxOut, SETTINGS.MAX_OUT_CONSTRAINTS);
    }

    private void setupTimeLimit() {
        // Time limit sets the values on the xaxis
        // Maximum is always at or above the current time
        // Minimum is calculated depending on the user set time limit
        JLabel timeLabel = new JLabel(SETTINGS.TIME_LIMIT_TEXT);
        JFormattedTextField timeLimit = new JFormattedTextField(GlobalSettings.INSTANCE.getTimeLimitNumSamples());
        timeLimit.addActionListener(evt -> {
            GlobalSettings.INSTANCE.setTimeLimitSeconds(((Number)timeLimit.getValue()).intValue());
        });
        add(timeLabel, SETTINGS.TIME_LABEL_CONSTRAINTS);
        add(timeLimit, SETTINGS.TIME_LIMIT_CONSTRAINTS);
    }

    @Override
    public Dimension getPreferredSize() { return SETTINGS.DIMS; }

    @Override
    public Dimension getMaximumSize() {
        return SETTINGS.DIMS;
    }

    @Override
    public Dimension getMinimumSize() {
        return SETTINGS.DIMS;
    }

}
