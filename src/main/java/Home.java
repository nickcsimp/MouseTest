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
        GridBagConstraints pauseSettings = gridConstraints(8,9,1,1,0.25,0.0,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        GridBagConstraints startSettings = gridConstraints(9,9,1,1,0.25,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        GridBagConstraints graphSettings = gridConstraints(0,1,10,6,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints controlsSettings = gridConstraints(0,9,8,1,0.5,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints dataSettings = gridConstraints(0,0,8,3,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);

        GridBagConstraints titleSettings = gridConstraints(0,1,1,1,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        GridBagConstraints isoSettings = gridConstraints(0,2,1,1,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        GridBagConstraints confirmSettings = gridConstraints(0,3,1,1,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        GridBagConstraints panelSettings = gridConstraints(8,0,2,1,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);

        GraphControls homeControls = new GraphControls();
        SidePanel sidePanel = new SidePanel();

        RawGraph procGraph = new RawGraph(processedData, "Mouse Respiratory Rate", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers(), 1);

        // create Log to save isoflurane concentration data
        Log log = null;
        try{
            log = new Log();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel isoTitle = new JLabel("Edit Isoflurane:");
        JTextField iso = new JTextField();
        iso.setColumns(3);
        JButton confirmButt = new JButton("Confirm");

        JButton setButt = new JButton("Pause");
        setButt.setFont(new Font(null, Font.PLAIN, 25));
        JButton startStopButt = new JButton("Start");
        startStopButt.setFont(new Font(null, Font.PLAIN, 25));

        JPanel isoPanel = new JPanel();
        isoPanel.add(isoTitle, titleSettings);
        isoPanel.add(iso, isoSettings);
        isoPanel.add(confirmButt, confirmSettings);

        frame.add(setButt, pauseSettings);
        frame.add(procGraph, graphSettings);
        frame.add(homeControls, controlsSettings);
        frame.add(sidePanel, dataSettings);
        frame.add(isoPanel, panelSettings);
        JButton loading = new JButton("Finding Arduino...");
        frame.add(loading, startSettings);
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
        frame.add(startStopButt, startSettings);
        frame.revalidate();
        frame.repaint();

        DataRetrieval dataRetrieval = new DataRetrieval(data, sp, homeControls, frame, procGraph, graphSettings, display, sidePanel);

        startStopButt.addActionListener(evt->{
            if(startStopButt.getText().equals("Start")) {
                startStopButt.setText("Stop");
                dataRetrieval.start();
                display=1;
                dataRetrieval.setDisplay(display);
            } else if(startStopButt.getText().equals("Stop")) {
                startStopButt.setText("Restart");
                dataRetrieval.interrupt();
            } else {
                frame.dispose();
                Home home = new Home();
            }
        });

        setButt.addActionListener(evt->{
            if(setButt.getText().equals("Pause")) {
                tempdisplay=display;
                setButt.setText("Resume");
                display = 2;
                dataRetrieval.setDisplay(display);
            } else {
                setButt.setText("Pause");
                display = tempdisplay;
                dataRetrieval.setDisplay(display);
            }
        });

        Log finalLog = log;
        confirmButt.addActionListener(evt->{
            try{
                finalLog.addPoint(iso.getText(), sidePanel.getAverage(), sidePanel.getCurrent());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
