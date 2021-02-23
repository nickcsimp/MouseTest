import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Home {
    ArrayList<Integer> data;
    ArrayList<Integer> processedData;
    double[] FTdata;
    private static SerialPort sp;
    int display;

    public Home(){

        try {
            arduino();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        display=0;
        JFrame frame = new JFrame("Mousify");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        int samplingFreq=4;//TODO settings
        data = new ArrayList<>();
        data.add(0);
        processedData = new ArrayList<>();
        processedData.add(0);
        GridBagConstraints menuHome = gridConstraints(0,0,2,1,1/3,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
        GridBagConstraints menuRaw = gridConstraints(2,0,2,1,1/3,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        GridBagConstraints menuSettings = gridConstraints(4,0,2,1,1/3,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);
        GridBagConstraints menuStart = gridConstraints(6,0,2,1,1/3,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);
        GridBagConstraints leftGraphC = gridConstraints(0,1,4,8,1/2,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints leftControlsC = gridConstraints(0,9,4,1,1/2,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints rightPanelC = gridConstraints(4,1,4,8,1/2,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);

        GraphControls rawControls = new GraphControls();
        GraphControls homeControls = new GraphControls();

        JPanel rightPanel = new JPanel();
        RawGraph procGraph = new RawGraph(processedData, "Moving Average Breaths Per Minute", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers());
        RawGraph rawGraph = new RawGraph(data, "Raw Piezo Output", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers());
        FTGraph rightGraph = new FTGraph(FTdata, samplingFreq);
        JButton homeButt = new JButton("Homepage");
        JButton rawButt = new JButton("Raw Data");
        JButton setButt = new JButton("Settings");
        JButton startStopButt = new JButton("Start");
        DataRetrieval dataRetrieval = new DataRetrieval(samplingFreq, data, sp, rawControls, frame, rawGraph, leftGraphC, display);
        DFT discreteFourierTransform = new DFT(dataRetrieval, samplingFreq, frame, display, rightGraph, rightPanelC);
        FTRetrieval ftRetrieval=new FTRetrieval(discreteFourierTransform, frame, procGraph, leftGraphC, display, homeControls);

        frame.add(homeButt, menuHome);
        frame.add(setButt, menuSettings);
        frame.add(rawButt, menuRaw);
        frame.add(startStopButt, menuStart);

        startStopButt.addActionListener(evt->{
            if(startStopButt.getText().equals("Start")) {
                startStopButt.setText("Stop");
                dataRetrieval.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                discreteFourierTransform.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ftRetrieval.start();
            } else {
                startStopButt.setText("Start");
                dataRetrieval.interrupt();
                discreteFourierTransform.interrupt();
                ftRetrieval.interrupt();
            }
        });

        homeButt.addActionListener(evt->{
            display=0;
            dataRetrieval.setDisplay(display);
            ftRetrieval.setDisplay(display);
            discreteFourierTransform.setDisplay(display);
            discreteFourierTransform.remove();
            dataRetrieval.remove();
            ftRetrieval.updateGraph();
            frame.remove(rawControls);
            frame.add(homeControls, leftControlsC);
        });
        setButt.addActionListener(evt->{
            display=2;
            dataRetrieval.setDisplay(display);
            ftRetrieval.setDisplay(display);
            discreteFourierTransform.setDisplay(display);
        });
        rawButt.addActionListener(evt->{
            display=1;
            dataRetrieval.setDisplay(display);
            ftRetrieval.setDisplay(display);
            discreteFourierTransform.setDisplay(display);
            ftRetrieval.remove();
            frame.remove(homeControls);
            frame.add(rawControls, leftControlsC);
            discreteFourierTransform.updateGraph();
            dataRetrieval.updateGraph();
        });

    }


    private GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy, int fill, int anchor){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        c.fill=fill;
        c.anchor=anchor;
        return c;
    }
    public static void arduino() throws InterruptedException, IOException {
        for (SerialPort s : SerialPort.getCommPorts()) //iterate through all the ports
        {
            String PortName = s.getSystemPortName();
            if(PortName.length() > 12) {
                if(PortName.substring(0, 12).equals("tty.usbmodem")){
                    System.out.println("Found port :)");
                    sp = s;
                    sp.setComPortParameters(9600, 8, 1, 0);
                    sp.openPort();
                    break;
                }
            }
        }
    }

}
