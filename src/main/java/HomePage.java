import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class HomePage extends JFrame {
    private static int SAMPLING_FREQUENCY=4;

    private static BufferedReader input;
    /** The output stream to the port */
    private static OutputStream output;
    private static SerialPort sp;
    private static int display; //0=home, 1=raw, //2=settings


    public HomePage(String title){
        super(title);
        try {
            arduino();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        display=0;
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationByPlatform(true);
        setVisible(true);

        ArrayList<Integer> data = new ArrayList<>();
        double[] dataFT = new double[40];//TODO input number of data points
        final double[][] FToutput = {new double[40]};
        ArrayList<Integer> outputData = new ArrayList<>();
        ArrayList<Integer> tempData = new ArrayList<>();


        GridBagConstraints menuHome = gridConstraints(0,0,2,1,1/3,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
        GridBagConstraints menuRaw = gridConstraints(2,0,2,1,1/3,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH);
        GridBagConstraints menuSettings = gridConstraints(4,0,2,1,1/3,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);
        GridBagConstraints leftGraphC = gridConstraints(0,1,3,8,1/2,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints leftControlsC = gridConstraints(0,9,3,1,1/2,0.1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        GridBagConstraints rightPanelC = gridConstraints(3,1,3,8,1/2,0.8,GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);

        GraphControls rawControls = new GraphControls();
        GraphControls homeControls = new GraphControls();
        getContentPane().add(homeControls, leftControlsC);

        JButton homeButt = new JButton("Homepage");
        homeButt.addActionListener(evt->{
            remove(rawControls);
            add(homeControls, leftControlsC);
            display=0;
        });
        add(homeButt, menuHome);

        JButton rawButt = new JButton("Raw Data");
        rawButt.addActionListener(evt->{
            remove(homeControls);
            add(rawControls, leftControlsC);
            display=1;
        });
        add(rawButt, menuRaw);

        JButton setButt = new JButton("Settings");
        setButt.addActionListener(evt->{
            display=2;
        });
        add(setButt, menuSettings);

        final JPanel[] rightPanel = {stats(tempData)};

        final RawGraph[] leftGraph = {new RawGraph(outputData, "Moving Average Breaths Per Minute", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers())};
        final FTGraph[] rightGraph = {new FTGraph(dataFT,SAMPLING_FREQUENCY)};
        getContentPane().add(leftGraph[0], leftGraphC);
        getContentPane().add(rightPanel[0], rightPanelC);
        final int[] count = {0};
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    data.add(Integer.parseInt(getData())); //getData() just reads the data from the port your arduino is connected to
                    count[0] += 1;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(count[0]<40) {
                    dataFT[count[0]]=data.get(count[0]-1);
                } else {
                    for(int j=0;j<39;j++){
                        dataFT[j]=dataFT[j+1];
                    }
                    dataFT[39]=data.get(count[0]-1);
                }

                FToutput[0] = dft(dataFT);

                tempData.add((int)(60*getFreq(dataFT)));
                outputData.add(movingAverage(tempData));

                if(display==0) {
                    remove(rightGraph[0]);
                    //Makes the new graph with the new data
                    JPanel newPanel = stats(tempData);
                    //Removes old graph
                    remove(rightPanel[0]);
                    //Changes name
                    rightPanel[0] =newPanel;
                    //Adds new graph
                    getContentPane().add(rightPanel[0], rightPanelC);

                    remove(rightGraph[0]);
                    //Makes the new graph with the new data
                    RawGraph graphNew = new RawGraph(outputData, "Moving Average Breaths Per Minute", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers());
                    //Removes old graph
                    remove(leftGraph[0]);
                    //Changes name
                    leftGraph[0] = graphNew;
                    //Adds new graph
                    getContentPane().add(leftGraph[0], leftGraphC);
                }
                if(display==1) {
                    remove(rightPanel[0]);
                    //Makes the new graph with the new data
                    RawGraph graphNew = new RawGraph(data, "Raw Piezo Output", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers());
                    //Removes old graph
                    remove(leftGraph[0]);
                    //Changes name
                    leftGraph[0] = graphNew;
                    //Adds new graph
                    getContentPane().add(leftGraph[0], leftGraphC);

                    //Makes the new graph with the new data
                    FTGraph graphFTNew = new FTGraph(FToutput[0], SAMPLING_FREQUENCY);
                    //Removes old graph
                    remove(rightGraph[0]);
                    //Changes name
                    rightGraph[0] = graphFTNew;
                    //Adds new graph
                    getContentPane().add(rightGraph[0], rightPanelC);
                }
                //Repaints the frame to add the new graph
                revalidate();
                repaint();
            }
        };
        new Timer(500, taskPerformer).start();
    }

    private JPanel stats(ArrayList<Integer> data){
        JPanel output = new JPanel();
        output.setPreferredSize(new Dimension(600, 600));
        output.setMaximumSize(new Dimension(600, 600));
        output.setMinimumSize(new Dimension(600, 600));
        output.setLayout(new GridBagLayout());
        //output.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        Font font = new Font("verdana", Font.PLAIN, 32);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy=0;
        JLabel average = new JLabel("Average Breaths per Minute: "+average(data));
        average.setFont(font);

        JLabel current = new JLabel("Current Breaths per Minute: "+movingAverage(data));
        current.setFont(font);

        output.add(average,c);
        c.gridy=1;
        output.add(current,c);
        return output;
    }




    // arduino just makes the connection to the port where the arduino is
    public static void arduino() throws InterruptedException, IOException {
        sp = SerialPort.getCommPort("/dev/tty.usbmodem14301");

        sp.setComPortParameters(9600, 8, 1, 0);
        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
        }

    }

    // reads information from arduino port (specified in the arduino function) and adds it to the ArrayList Data
    // Data was initially an ArrayList<Integer> but it created some sort of error, we can keep it as a string or
    // TODO look into it
    // I got this from https://stackoverflow.com/questions/16608878/read-data-from-a-java-socket
    private String getData() throws InterruptedException, IOException{
        BufferedReader bis = new BufferedReader(new InputStreamReader(sp.getInputStream()));
        String inputLine; // temporally stores the new number from the port
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); //this line
        // is essential so when the program starts looking at the port, it doesnt just "give up" if it sees no info
        inputLine = bis.readLine();
        // TODO it would be a good idea to add a check so the info is not null, but when i add this it doesnt work
        return inputLine;
        // while(inputLine != null)
    }


    private double[] dft(double[] inreal) {
        double realCount = 0;
        int n = inreal.length;
        double[] outreal = new double[n];
        double[] shifted = new double[n/2];
        for (int k = 0; k < n; k++) {
            realCount += inreal[k];
        }
        double realMean = realCount/n;
        for (int k = 0; k < n; k++) {
            inreal[k] = inreal[k]-realMean;
        }
        for (int k = 0; k < n; k++) {  // For each output element
            double sumreal = 0;
            for (int t = 0; t < n; t++) {  // For each input element
                double angle = 2 * Math.PI * t * k / n;
                sumreal +=  inreal[t] * Math.cos(angle);
            }
            outreal[k] = sumreal;
        }

        for(int j=1; j<n/2; j++){
            shifted[j]=outreal[n/2-j];
        }
        return shifted;
    }

    static int maxInd(double[] input){
        double output=0;
        int index=0;
        for(int i=0; i<input.length; i++){
            double d=input[i];
            if(d>output){
                output=d;
                index=i;
            }
        }
        return index;
    }

    static int movingAverage(ArrayList<Integer> data){
        int size = data.size();
        if(size==0){
            return 0;
        }
        int count = 0;
        if(size<10){
            for(Integer i:data){
                count = count + i;
            }
            return count/size;
        } else {
            for (int i = size - 10; i < size; i++) {
                count = count + data.get(i);
            }
        }
        return count/10;
    }

    static int average(ArrayList<Integer> data) {
        int size = data.size();
        if(size==0){
            return 0;
        }
        int count = 0;
        for(Integer i:data){
            count = count + i;
        }
        return count/size;
    }

    double getFreq(double[] ftInput){
        int length = ftInput.length;
        int noOfPeaks = maxInd(ftInput);
        return noOfPeaks/(2*length/SAMPLING_FREQUENCY);
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
}
