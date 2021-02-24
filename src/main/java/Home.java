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
    int tempdisplay;

    public Home(){
        int samplingFreq=4;//TODO settings

        display=0;
        tempdisplay=0;
        JFrame frame = new JFrame("Mousify");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        data = new ArrayList<>();
        processedData = new ArrayList<>();
        FTdata=new double[40];//TODO
        GridBagConstraints menuHome = gridConstraints(0,0,2,1,0.25,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
        GridBagConstraints menuRaw = gridConstraints(2,0,2,1,0.25,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        GridBagConstraints menuSettings = gridConstraints(4,0,2,1,0.25,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);
        GridBagConstraints menuStart = gridConstraints(6,0,2,1,0.25,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);
        GridBagConstraints leftGraphC = gridConstraints(0,1,4,8,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints leftControlsC = gridConstraints(0,9,4,1,0.5,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints rightPanelC = gridConstraints(4,1,4,8,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        GridBagConstraints rightGraphC = gridConstraints(4,1,4,7,0.5,0.7,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        GridBagConstraints rightControlsC = gridConstraints(4,9,4,1,0.5,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);

        GraphControls rawControls = new GraphControls();
        GraphControls homeControls = new GraphControls();
        FTControls ftControls = new FTControls();
        SidePanel sidePanel = new SidePanel();

        //JPanel rightPanel = new JPanel();
        RawGraph procGraph = new RawGraph(processedData, "Moving Average Breaths Per Minute", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers(), 1);
        RawGraph rawGraph = new RawGraph(data, "Raw Piezo Output", "Piezo Output", "Time (s)", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers(), ftControls.getSampleFreq());
        FTGraph rightGraph = new FTGraph(FTdata, ftControls);
        JButton homeButt = new JButton("Homepage");
        JButton rawButt = new JButton("Raw Data");
        JButton setButt = new JButton("Pause");
        JButton startStopButt = new JButton("Start");

        frame.add(homeButt, menuHome);
        frame.add(setButt, menuSettings);
        frame.add(rawButt, menuRaw);
        frame.add(procGraph, leftGraphC);
        frame.add(homeControls, leftControlsC);
        frame.add(sidePanel, rightPanelC);
        JButton loading = new JButton("Finding Arduino...");
        frame.add(loading, menuStart);
        while(sp==null) {
            try {
                arduino();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        frame.remove(loading);
        frame.add(startStopButt, menuStart);
        frame.revalidate();
        frame.repaint();

        DataRetrieval dataRetrieval = new DataRetrieval(ftControls, data, sp, rawControls, frame, rawGraph, leftGraphC, display);
        DFT discreteFourierTransform = new DFT(dataRetrieval, ftControls, frame, display, rightGraph, rightGraphC);
        FTRetrieval ftRetrieval=new FTRetrieval(discreteFourierTransform, frame, procGraph, leftGraphC, display, homeControls, 1, sidePanel);

        startStopButt.addActionListener(evt->{
            if(startStopButt.getText().equals("Start")) {
                startStopButt.setText("Stop");
                dataRetrieval.start();
                discreteFourierTransform.start();
                ftRetrieval.start();
            } else if(startStopButt.getText().equals("Stop")) {
                startStopButt.setText("Restart");
                dataRetrieval.interrupt();
                discreteFourierTransform.interrupt();
                ftRetrieval.interrupt();
            } else {
                frame.dispose();
                Home home = new Home();
            }
        });

        homeButt.addActionListener(evt->{
            display=0;
            dataRetrieval.setDisplay(display);
            ftRetrieval.setDisplay(display);
            discreteFourierTransform.setDisplay(display);

            discreteFourierTransform.remove();
            dataRetrieval.remove();
            frame.remove(rawControls);
            frame.remove(ftControls);

            ftRetrieval.updateGraph();
            frame.add(homeControls, leftControlsC);
            frame.add(sidePanel, rightPanelC);
        });
        setButt.addActionListener(evt->{
            if(setButt.getText().equals("Pause")) {
                tempdisplay=display;
                setButt.setText("Resume");
                display = 2;
                dataRetrieval.setDisplay(display);
                ftRetrieval.setDisplay(display);
                discreteFourierTransform.setDisplay(display);
            } else {
                setButt.setText("Resume");
                display = tempdisplay;
                dataRetrieval.setDisplay(display);
                ftRetrieval.setDisplay(display);
                discreteFourierTransform.setDisplay(display);
            }

        });
        rawButt.addActionListener(evt->{
            display=1;
            dataRetrieval.setDisplay(display);
            ftRetrieval.setDisplay(display);
            discreteFourierTransform.setDisplay(display);

            ftRetrieval.remove();
            frame.remove(homeControls);
            frame.remove(sidePanel);

            frame.add(ftControls, rightControlsC);
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
