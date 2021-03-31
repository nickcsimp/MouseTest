import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    private int Average;
    private int Current;
    private int filterLength=10;
    JLabel average;
    JLabel current;

    public SidePanel(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridwidth=2;
        c.gridheight=1;
        c.gridy=0;

//        Font font = new Font("Verdana", Font.BOLD, 24);
        //Automatically has the words but obvs no data is there yet
        Font font = new Font(null, Font.PLAIN, 30);
        average = new JLabel("Average BPM: ");
        current = new JLabel("Current BPM: ");
        average.setFont(font);
        current.setFont(font);
        this.add(average, c);
        c.gridy=1;
        this.add(current, c);
    }
    //Updates the words with the correct data
    public void updatePanel(){
        average.setText("Average BPM: "+Average);
        current.setText("Current BPM: "+Current);
        revalidate();
        repaint();
    }

    //data retrieval calculates and update the average and the current
    public void setAverage(int ave){
        Average=ave;
        updatePanel();
    }
    public void setCurrent(int cur){
        Current =cur;
        updatePanel();
    }
    public int getFilterLength(){
        return filterLength;
    }
}
