import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    private int Average;
    private int Current;
    private int filterLength=10;
    JLabel average;
    JLabel current;
    JFrame frame;

    public SidePanel(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridwidth=2;
        c.gridheight=1;
        c.gridy=0;

        Font font = new Font("Verdana", Font.BOLD, 24);
        average = new JLabel("Average BPM: ");
        current = new JLabel("Current BPM: ");
        average.setFont(font);
        current.setFont(font);
        JLabel mavgFilterLength = new JLabel("Moving average filter length: ");
        mavgFilterLength.setFont(font);
        JFormattedTextField length = new JFormattedTextField(filterLength);
        length.addActionListener(evt->{
            filterLength = ((Number)length.getValue()).intValue();
        });
        this.add(average, c);
        c.gridy=1;
        this.add(current, c);
        c.gridy=2;
        c.gridwidth=1;
        this.add(mavgFilterLength, c);
        c.gridx=1;
        this.add(length, c);
    }
    public void updatePanel(){
        average.setText("Average BPM: "+Average);
        current.setText("Current BPM: "+Current);
        revalidate();
        repaint();
    }

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
