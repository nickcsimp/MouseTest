import javax.swing.*;
import java.awt.*;

public class FTControls extends JPanel {

    private int sampleCount=40;
    private int sampleFreq=4;

    public FTControls(){
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        JFormattedTextField samples = new JFormattedTextField(40);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy=0;
        c.gridheight=1;
        c.gridwidth=1;
        c.weightx=0.25;
        c.weighty=1;

        c.gridx=0;
        JLabel samplingCount = new JLabel("Samples for Fourier Transform: ");
        JLabel samplingFreq = new JLabel("Sampling Freq: ");
        this.add(samplingCount, c);
        c.gridx=1;
        this.add(samples, c);
        samples.addActionListener(evt ->{
            sampleCount = ((Number)samples.getValue()).intValue();
        });
        c.gridx=2;
        this.add(samplingFreq,c);
        JFormattedTextField frequency = new JFormattedTextField(4);
        c.gridx=3;
        this.add(frequency, c);
        frequency.addActionListener(evt ->{
            sampleFreq = ((Number)frequency.getValue()).intValue();
        });
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

    public int getSampleCount(){
        return sampleCount;
    }
    public int getSampleFreq(){
        return sampleFreq;
    }

}
