import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Home {
    ArrayList<Integer> data;
    ArrayList<Integer> processedData;
    double[] FTdata;
    int display;
    int tempdisplay;

    private final DataRetriever dataRetriever;

    public Home(){

        int samplingFreq=4;//TODO settings

        display=0; //Used for changing screens and pausing
        tempdisplay=0;

        //Opens frame and sets settings
        JFrame frame = new JFrame("Mousify");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        data = new ArrayList<>();//Raw data contained here
        processedData = new ArrayList<>(); //Processed data here
        FTdata=new double[40];//TODO Unsure if needed still

        //Sets gridbag constraints for various panels
        GridBagConstraints pauseSettings = gridConstraints(6,9,2,3,0.25,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);
        GridBagConstraints startSettings = gridConstraints(8,9,2,1,0.25,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);
        GridBagConstraints graphSettings = gridConstraints(0,2,10,6,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints controlsSettings = gridConstraints(0,9,6,1,0.5,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints rightPanelC = gridConstraints(4,0,10,2,0.5,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);

        //Instantiate the graph controls and 'Sidepanel'
        GraphControls homeControls = new GraphControls();
        SidePanel sidePanel = new SidePanel();

        //Instantiates the graph
        RawGraph procGraph = new RawGraph(processedData, "Mouse Respiratory Rate", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers(), 1);

        //Creates start stop pause buttons
        JButton setButt = new JButton("Pause");
        JButton startStopButt = new JButton("Start");

        //Adds everything to frame
        frame.add(setButt, pauseSettings);
        frame.add(procGraph, graphSettings);
        frame.add(homeControls, controlsSettings);
        frame.add(sidePanel, rightPanelC);
        JButton loading = new JButton("Finding Arduino...");
        frame.add(loading, startSettings);

        dataRetriever = new DataRetriever(data, null, homeControls, frame, procGraph, graphSettings, display, sidePanel);
        PortSelector portSelector = new PortSelector(frame, dataRetriever);
        portSelector.selectInput();

        frame.remove(loading); //When one is found we remove the loading sign
        frame.add(startStopButt, startSettings); //Add the buttons
        frame.revalidate(); //Repaint so that things show up
        frame.repaint();

        //This changes the start button to stop, then to restart
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
                frame.dispose();
                Home home = new Home(); //When restart is pressed the whole app refreshes
            }
        });

        //This pauses display refresh but doesnt pause data retrieval
        setButt.addActionListener(evt->{
            if(setButt.getText().equals("Pause")) {
                tempdisplay=display;
                setButt.setText("Resume");
                display = 2;
                dataRetriever.setDisplay(display);
            } else {
                setButt.setText("Pause");
                display = tempdisplay;
                dataRetriever.setDisplay(display);
            }
        });

    }

    //Quicker way of defining gridbag constraints
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

}
