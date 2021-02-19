import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Export extends JFrame {
    private ArrayList<Integer> list;
    private boolean localised;
    private int[] outlier;
    private int timeLimited;
    private String title;
    private String xaxis;
    private String yaxis;
    private DynamicGraph graphPanel;

    //TODO Set begin and end times
    //TODO Stop getting new data from arduino

    public Export(ArrayList<Integer> list) {
        this.list = list;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLayout(new GridBagLayout());

        localised = true;
        outlier[0] = 0;
        outlier[2] = 1000;
        timeLimited = 300;
        title="Title";
        xaxis="X Axis";
        yaxis="Y Axis";


        graphPanel = new DynamicGraph(list, title, yaxis, xaxis, localised, timeLimited, outlier);
        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(evt -> {
            BufferedImage image = new BufferedImage(graphPanel.getWidth(), graphPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            graphPanel.printAll(g);
            g.dispose();
            try {
                ImageIO.write(image, "jpg", new File("Paint.jpg"));
                ImageIO.write(image, "png", new File("Paint.png"));
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        });

        this.setPreferredSize(new Dimension(200,650));

        JRadioButton localisation = new JRadioButton("Localise Y Axis", localised);
        localisation.addActionListener(evt ->{
            localised=localisation.isSelected();
            updateGraph();
        });
        add(localisation);
        /*
        JRadioButton outliers = new JRadioButton("Highlight Outliers", outlier);
        localisation.addActionListener(evt ->{
            outlier=outliers.isSelected();
            updateGraph();
        });
        add(outliers);
        */
        JLabel timeLabel = new JLabel("Time Limit (s)");
        JFormattedTextField timeLimit = new JFormattedTextField(timeLimited);
        timeLimit.addActionListener(evt ->{
            timeLimited = ((Number)timeLimit.getValue()).intValue();
            updateGraph();
        });


        add(timeLimit);
        add(timeLabel);

        add(graphPanel);
        add(confirm);
    }

    public void updateGraph(){
        DynamicGraph graphNew = new DynamicGraph(list, title, yaxis, xaxis, localised, timeLimited, outlier);
        this.remove(graphPanel);
        graphPanel = graphNew;
        //Adds new graph
        this.add(graphPanel);
        //Repaints the frame to add the new graph
        revalidate();
        repaint();
    }
}
