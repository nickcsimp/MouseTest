import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Home {

    ArrayList<Integer> data;
    ArrayList<Integer> processedData;
    double[] FTdata;
    int display;
    int tempdisplay;
    Integer initialIso = 2;

    private final DataRetriever dataRetriever;

    public Home() throws IOException, InterruptedException {

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
        processedData = new ArrayList<>(); // Processed data here
        FTdata=new double[40];// TODO Unsure if needed still


        JLabel isoTitle = new JLabel("Edit Isoflurane:");
        JTextField iso = new JTextField();
        iso.setColumns(2);
        JButton confirmButt = new JButton("Confirm");

        // Sets gridbag constraints for various panels
        GridBagConstraints pauseSettings = gridConstraints(6,9,2,3,0.25,0.1, GridBagConstraints.NORTHEAST);
        GridBagConstraints startSettings = gridConstraints(8,9,2,1,0.25,0.1, GridBagConstraints.NORTHEAST);
        GridBagConstraints graphSettings = gridConstraints(0,2,10,6,0.5,0.8, GridBagConstraints.WEST);
        GridBagConstraints controlsSettings = gridConstraints(0,9,6,1,0.5,0.1, GridBagConstraints.WEST);
        GridBagConstraints rightPanelC = gridConstraints(0,0,10,2,0.5,0.8, GridBagConstraints.EAST);

        // for logger
        GridBagConstraints titleSettings = gridConstraints(0,1,1,1,0.5,0.8, GridBagConstraints.EAST);
        GridBagConstraints isoSettings = gridConstraints(0,2,1,1,0.5,0.8, GridBagConstraints.EAST);
        GridBagConstraints confirmSettings = gridConstraints(0,3,1,1,0.5,0.8, GridBagConstraints.EAST);
        GridBagConstraints panelSettings = gridConstraints(9,0,1,1,0.5,0.8, GridBagConstraints.EAST);

        // Instantiate the graph controls and 'Sidepanel'
        GraphControls homeControls = new GraphControls();
        SidePanel sidePanel = new SidePanel();

        // Instantiate the logging class with the SidePanel
        Log log = null;
        try{
            log = new Log(sidePanel);
            System.out.println("Log created");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Instantiates the graph
        RawGraph procGraph = new RawGraph(processedData, "Mouse Respiratory Rate", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers(), 1);

        // Creates start stop pause buttons
        JButton setButt = new JButton("Pause");
        JButton startStopButt = new JButton("Start");

        // Adds everything to frame
        JPanel isoPanel = new JPanel();
        isoPanel.add(isoTitle, titleSettings);
        isoPanel.add(iso, isoSettings);
        isoPanel.add(confirmButt, confirmSettings);

        frame.add(isoPanel, panelSettings);
        frame.add(setButt, pauseSettings);
        frame.add(procGraph, graphSettings);
        frame.add(homeControls, controlsSettings);
        frame.add(sidePanel, rightPanelC);
        JButton loading = new JButton("Finding Arduino...");
        frame.add(loading, startSettings);

        dataRetriever = new DataRetriever(data, null, homeControls, frame, procGraph, graphSettings, display, sidePanel);
        PortSelector portSelector = new PortSelector(frame, dataRetriever);
        portSelector.selectInput();

        frame.remove(loading); // When one is found we remove the loading sign
        frame.add(startStopButt, startSettings); // Add the buttons
        frame.revalidate(); // Repaint so that things show up
        frame.repaint();

        // This changes the start button to stop, then to restart
        Log finalLog = log;
        startStopButt.addActionListener(evt->{
            if(startStopButt.getText().equals("Start")) {
                startStopButt.setText("Stop");
                dataRetriever.start();
                finalLog.start();
                display=1;
                dataRetriever.setDisplay(display);
            } else if(startStopButt.getText().equals("Stop")) {
                startStopButt.setText("Restart");
                dataRetriever.interrupt();
            } else {
                frame.dispose();
                try {
                    Home home = new Home(); // When restart is pressed the whole app refreshes
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

        confirmButt.addActionListener(evt-> {
            finalLog.changeIso(Integer.parseInt(iso.getText()));
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
