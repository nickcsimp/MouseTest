import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {

    private Integer average;
    private Integer current;
    private int filterLength=10;
    JLabel averageLabel;
    JLabel currentLabel;

    public SidePanel(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridwidth=2;
        c.gridheight=1;
        c.gridy=0;

        // Font font = new Font("Verdana", Font.BOLD, 24);
        // Automatically has the words but obvs no data is there yet
        Font font = new Font(null, Font.PLAIN, 30);
        averageLabel = new JLabel("Average BPM: ");
        currentLabel = new JLabel("Current BPM: ");
        averageLabel.setFont(font);
        currentLabel.setFont(font);
        this.add(averageLabel, c);
        c.gridy=1;
        this.add(currentLabel, c);
    }

    // Updates the words with the correct data
    public void updatePanel(){
        averageLabel.setText("Average BPM: "+ average);
        currentLabel.setText("Current BPM: "+ current);
        revalidate();
        repaint();
    }

    // Data retrieval calculates and update the average and the current
    public void setAverageLabel(int ave){
        average =ave;
        updatePanel();
    }

    public void setCurrentLabel(int cur){
        current =cur;
        updatePanel();
    }

    public int getFilterLength(){
        return filterLength;
    }
    public void setFilterLength(int filterLength) { this.filterLength = filterLength; }

    public String getAverage(){ return average.toString(); }

    public String getCurrent(){ return current.toString();}
}
