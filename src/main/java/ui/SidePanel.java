package ui;

import helpers.Helpers;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {

    private int average;
    private int current;
    private int filterLength=10;
    JLabel averageLabel;
    JLabel currentLabel;

    private static class SETTINGS {
        private static final GridBagConstraints AVG_LABEL_CONSTRAINTS = Helpers.gridConstraints(0,0,2,1,0,0);
        private static final GridBagConstraints CURR_LABEL_CONSTRAINTS = Helpers.gridConstraints(0,1,2,1,0,0);
        private static final String AVG_BPM_TEXT = "Average BPM: ";
        private static final String CURR_BPM_TEXT = "Current BPM: ";
        // Font font = new Font("Verdana", Font.BOLD, 24);
        private static final Font FONT = new Font(null, Font.PLAIN, 30);
    }

    public SidePanel(){
        setLayout(new GridBagLayout());
        averageLabel = new JLabel(SETTINGS.AVG_BPM_TEXT);
        currentLabel = new JLabel(SETTINGS.CURR_BPM_TEXT);
        averageLabel.setFont(SETTINGS.FONT);
        currentLabel.setFont(SETTINGS.FONT);
        this.add(averageLabel, SETTINGS.AVG_LABEL_CONSTRAINTS);
        this.add(currentLabel, SETTINGS.CURR_LABEL_CONSTRAINTS);
    }

    // Updates the words with the correct data
    public void updatePanel(){
        averageLabel.setText(SETTINGS.AVG_BPM_TEXT+ average);
        currentLabel.setText(SETTINGS.CURR_BPM_TEXT+ current);
        revalidate();
        repaint();
    }

    // Data retrieval calculates and update the average and the current
    public void setAverageLabel(int ave){
        average = ave;
        updatePanel();
    }

    public void setCurrentLabel(int cur){
        current = cur;
        updatePanel();
    }

    public int getFilterLength(){
        return filterLength;
    }
    public void setFilterLength(int filterLength) { this.filterLength = filterLength; }
}
