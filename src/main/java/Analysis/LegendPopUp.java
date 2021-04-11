package Analysis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class LegendPopUp extends JFrame {
    public LegendPopUp(ReviewLegend leg) {
        ReviewLegend legend = leg;
        int numberOfLines=legend.getNumberOfLines();
        ArrayList<String> names = legend.getNames();
        ArrayList<int[]> outliers = legend.getOutliers();
        Color[] colours = legend.getColors();
        this.setPreferredSize(new Dimension(300, 250));
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationByPlatform(true);
        this.setVisible(true);

        for(int i=0; i<numberOfLines; i++){
            JLabel nameLabel = new JLabel(names.get(i));
            JTextField nameField = new JTextField(names.get(i));
            JButton chooseColour = new JButton("Choose Colour");
            chooseColour.setForeground(colours[i]);
            JColorChooser colorChooser = new JColorChooser(colours[i]);
            JLabel outlierLabel = new JLabel("Outliers - ");
            JLabel outMinLabel = new JLabel("Min: ");
            JTextField outMinField = new JTextField(String.valueOf(outliers.get(i)[0]));
            JLabel outMaxLabel = new JLabel("Max: ");
            JTextField outMaxField = new JTextField(String.valueOf(outliers.get(i)[1]));

            GridBagConstraints nameLabelC = gridConstraints(0, 3*i, 2, 1, 0.3, 0.5);
            GridBagConstraints nameFieldC = gridConstraints(2, 3*i, 2, 1, 0.3, 0.5);
            GridBagConstraints colorChooserC = gridConstraints(4, 3*i, 2, 1, 0.3, 0.5);
            GridBagConstraints outlierLabelC = gridConstraints(0, 3*i+1, 2, 1, 0.4, 0.5);
            GridBagConstraints outMinLabelC = gridConstraints(2, 3*i+1, 1, 1, 0.2, 0.5);
            GridBagConstraints outMinFieldC = gridConstraints(3, 3*i+1, 1, 1, 0.2, 0.5);
            GridBagConstraints outMaxLabelC = gridConstraints(4, 3*i+1, 1, 1, 0.2, 0.5);
            GridBagConstraints outMaxFieldC = gridConstraints(5, 3*i+1, 1, 1, 0.2, 0.5);
            GridBagConstraints separatorC = gridConstraints(0, 3*i+2, 6, 1, 0.2, 0.5);

            this.add(nameLabel, nameLabelC);
            this.add(nameField, nameFieldC);
            this.add(chooseColour, colorChooserC);
            this.add(outlierLabel, outlierLabelC);
            this.add(outMinLabel, outMinLabelC);
            this.add(outMinField, outMinFieldC);
            this.add(outMaxLabel, outMaxLabelC);
            this.add(outMaxField, outMaxFieldC);
            this.add(new JSeparator(), separatorC);


            int finalI = i;
            nameField.addActionListener(e -> {
                nameField.getRootPane().requestFocus();
            });

            nameField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {}
                @Override
                public void focusLost(FocusEvent e) {
                    names.set(finalI, nameField.getText());
                }
            });

            outMinField.addActionListener(e -> {
                outMinField.getRootPane().requestFocus();
            });

            outMinField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {}
                @Override
                public void focusLost(FocusEvent e) {
                    outliers.get(finalI)[0]= Integer.valueOf(outMinField.getText());
                }
            });

            outMaxField.addActionListener(e -> {
                outMaxField.getRootPane().requestFocus();
            });

            outMaxField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {}
                @Override
                public void focusLost(FocusEvent e) {
                    outliers.get(finalI)[1]= Integer.valueOf(outMaxField.getText());
                }
            });

            chooseColour.addActionListener(evt->{
                Color newColor = JColorChooser.showDialog(
                        LegendPopUp.this,
                        "Choose Background Color",
                        chooseColour.getForeground());
                chooseColour.setForeground(newColor);
                colours[finalI]=newColor;
            });
        }
        JButton submit = new JButton("Submit");
        GridBagConstraints submitC = gridConstraints(0, 3*numberOfLines, 6, 1, 1, 0.5);

        submit.addActionListener(evt->{
            legend.setNames(names);
            legend.setColors(colours);
            legend.setOutliers(outliers);
            legend.refresh();
            this.dispose();
        });
        this.add(submit, submitC);
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
