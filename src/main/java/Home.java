import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Home {

    ArrayList<Integer> data;
    int display;
    int tempdisplay;

    private DataRetriever dataRetriever;

    public Home(){

        display=0; // Used for changing screens and pausing
        tempdisplay=0;

        // Opens frame and sets settings
        JFrame frame = new JFrame("Mousify");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        data = new ArrayList<>();// Raw data contained here

        // Sets gridbag constraints for various panels
        GridBagConstraints pauseSettings = gridConstraints(6,9,2,3,0.25,0.1, GridBagConstraints.NORTHEAST);
        GridBagConstraints startSettings = gridConstraints(8,9,2,1,0.25,0.1, GridBagConstraints.NORTHEAST);
        GridBagConstraints graphSettings = gridConstraints(0,2,10,6,0.5,0.8, GridBagConstraints.WEST);
        GridBagConstraints controlsSettings = gridConstraints(0,9,6,1,0.5,0.1, GridBagConstraints.WEST);
        GridBagConstraints rightPanelC = gridConstraints(4,0,10,2,0.5,0.8, GridBagConstraints.EAST);

        // Instantiate the graph controls and 'Sidepanel'
        GraphControls homeControls = new GraphControls();
        SidePanel sidePanel = new SidePanel();

        // Instantiates the graph
        RawGraph rawGraph = new RawGraph(data, "Mouse Respiratory Rate", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers(), GlobalSettings.INSTANCE.getSamplingFrequency());

        // Creates start stop pause buttons
        JButton setButt = new JButton("Pause");
        JButton startStopButt = new JButton("Start");

        // Adds everything to frame
        frame.add(setButt, pauseSettings);
        frame.add(rawGraph, graphSettings);
        frame.add(homeControls, controlsSettings);
        frame.add(sidePanel, rightPanelC);
        JButton loading = new JButton("Finding Arduino...");
        frame.add(loading, startSettings);

        PortSelector portSelector = new PortSelector(frame);
        SerialPort selectedPort = portSelector.selectInput();
        dataRetriever = new DataRetriever(data, selectedPort, homeControls, frame, rawGraph, graphSettings, display, sidePanel);

        frame.remove(loading); // When one is found we remove the loading sign
        frame.add(startStopButt, startSettings); // Add the buttons
        frame.revalidate(); // Repaint so that things show up
        frame.repaint();

        // This changes the start button to stop, then to restart
        startStopButt.addActionListener(evt->{
            if(startStopButt.getText().equals("Start")) {
                startStopButt.setText("Stop");
                dataRetriever.start();
                display=1;
                dataRetriever.setDisplay(display);
            } else if(startStopButt.getText().equals("Stop")) {
                startStopButt.setText("Restart");
                dataRetriever.interrupt();
            } else {
                data = new ArrayList<>();
                SerialPort sp = dataRetriever.getSerialPort();
                dataRetriever.remove();
                dataRetriever = new DataRetriever(data, sp, homeControls, frame, rawGraph, graphSettings, display, sidePanel);
                dataRetriever.updateGraph();
                startStopButt.setText("Start");
                frame.revalidate(); // Repaint so that things show up
                frame.repaint();
            }
        });

        // This pauses display refresh but doesnt pause data retrieval
        setButt.addActionListener(evt->{
            if (setButt.getText().equals("Pause")) {
                tempdisplay=display;
                setButt.setText("Resume");
                display = 2;
            } else {
                setButt.setText("Pause");
                display = tempdisplay;
            }
            dataRetriever.setDisplay(display);
        });

    }

    // Quicker way of defining gridbag constraints
    private GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy, int anchor){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        c.fill= GridBagConstraints.HORIZONTAL;
        c.anchor=anchor;
        return c;
    }

}
