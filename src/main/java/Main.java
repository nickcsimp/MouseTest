import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static BufferedReader input;
    /** The output stream to the port */
    private static OutputStream output;
    private static SerialPort sp;
    private static ArrayList<String> Data = new ArrayList<String>(); //this contains the info from the sensor

    public static void main(String[] args) throws InterruptedException, IOException {
    /*
        //Create mainFrame
        JFrame frame = new JFrame("Mousify");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        GraphControls controls = new GraphControls();
        c.gridx = 8;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.2;
        frame.getContentPane().add(controls, c);

        //Initialise list
        ArrayList<Integer> list = new ArrayList<>();
        final DynamicGraph[] graphPanel = {new DynamicGraph(list, "Raw Piezo Output", "Piezo Output", "Time", controls.getLocalised(), controls.getTimeLimit(), controls.getOutliers())};
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 8;
        c.gridheight = 4;
        c.weightx = 0.8;
        frame.getContentPane().add(graphPanel[0], c);
        JButton paint = new JButton("Export");
        paint.addActionListener(evt->{
            frame.dispose();
            ArrayList<Integer> finalList = list;
            Export exp = new Export(finalList);
        });
        c.gridx = 8;
        c.gridy = 4;
        c.gridwidth = 2;
        c.gridheight = 4;
        c.weightx = 0.2;
        c.weighty = 0.5;
        frame.add(paint, c);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 8;
        c.gridheight = 8;
        c.weightx = 0.8;
        c.weighty = 1;

        arduino(); //makes the connection to the arduino port

        // this next part just runs getData every 0.5seconds (specified by the first parameter in Timer)
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                try {
                    getData(); //getData() just reads the data from the port your arduino is connected to
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Turns new data into integer
                list.add(Integer.parseInt(Data.get(Data.size()-1)));
                //Makes the new graph with the new data
                DynamicGraph graphNew = new DynamicGraph(list, "Raw Piezo Output", "Piezo Output", "Time", controls.getLocalised(), controls.getTimeLimit(), controls.getOutliers());
                //Removes old graph
                frame.remove(graphPanel[0]);
                //Changes name
                graphPanel[0] = graphNew;
                //Adds new graph
                frame.getContentPane().add(graphPanel[0], c);
                //Repaints the frame to add the new graph
                frame.revalidate();
                frame.repaint();
            }
        };
        new Timer(500, taskPerformer).start();*/
        //dynamicGraph();
        testDashboard();
    }

    public static JPanel rawInput(JFrame frame, JPanel graph, ArrayList<Integer> list, boolean local, int time, int[] out) throws InterruptedException {
        //Use graphPanel = rawInput(frame, graphPanel, list, controls.getLocalised(), controls.getTimeLimit(), controls.getOutliers());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 8;
        c.gridheight = 8;
        c.weightx = 0.8;
        c.weighty = 1;
        //Makes the new graph with the new data
        DynamicGraph graphNew = new DynamicGraph(list, "Raw Piezo Output", "Piezo Output", "Time", local, time, out);
        //Removes old graph
        frame.remove(graph);
        //Changes name
        graph = graphNew;
        //Adds new graph
        frame.getContentPane().add(graph, c);
        //Repaints the frame to add the new graph
        frame.revalidate();
        frame.repaint();
        return graph;
    }

    public static void dynamicGraph() throws InterruptedException {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(50);
        JFrame frame = new JFrame("DynamicGraph");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        GraphControls controls = new GraphControls();
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.1;
        frame.getContentPane().add(controls, c);

        DynamicGraph graph = new DynamicGraph(list, "Raw Piezo Output", "Piezo Output", "Time", controls.getLocalised(), controls.getTimeLimit(), controls.getOutliers());
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 7;
        c.weightx = 1;
        c.weighty = 0.7;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graph, c);


        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        for(int i =1; i<80; i++){
            Thread.sleep(500);
            Random rand = new Random();
            int randint = rand.nextInt(10);
            list.add(randint+45);
            DynamicGraph graphNew = new DynamicGraph(list, "Raw Piezo Output", "Piezo Output", "Time", controls.getLocalised(), controls.getTimeLimit(), controls.getOutliers());
            frame.remove(graph);
            graph = graphNew;
            frame.getContentPane().add(graph, c);
            frame.revalidate();
            frame.repaint();
        }
    }

    public static void testDashboard() throws InterruptedException {
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> avgList = new ArrayList<>();
        ArrayList<Integer> totavgList = new ArrayList<>();
        list.add(50);
        avgList.add(movingAverage(list));
        totavgList.add(average(list));
        JFrame frame = new JFrame("Dashboard");
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints raw = new GridBagConstraints();
        GridBagConstraints avg = new GridBagConstraints();
        GridBagConstraints totavg = new GridBagConstraints();

        GraphControls rawControls = new GraphControls();
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.1;
        c.fill=GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(rawControls, c);

        DynamicGraph rawGraph = new DynamicGraph(list, "Raw Piezo Output", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers());
        raw.gridx = 0;
        raw.gridy = 0;
        raw.gridwidth = 1;
        raw.gridheight = 7;
        raw.weightx = 1;
        raw.weighty = 0.7;
        raw.anchor=GridBagConstraints.NORTHWEST;
        raw.fill=GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(rawGraph, raw);

        GraphControls avgControls = new GraphControls();
        c.gridx = 1;
        c.gridy = 7;
        frame.getContentPane().add(avgControls, c);

        DynamicGraph avgGraph = new DynamicGraph(avgList, "Moving Average", "Piezo Output", "Time", avgControls.getLocalised(), avgControls.getTimeLimit(), avgControls.getOutliers());
        avg.gridx = 1;
        avg.gridy = 0;
        avg.gridwidth = 1;
        avg.gridheight = 7;
        avg.weightx = 1;
        avg.weighty = 0.7;
        avg.anchor=GridBagConstraints.NORTHEAST;
        avg.fill=GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(avgGraph, avg);

        GraphControls totAvgControls = new GraphControls();
        c.gridx = 0;
        c.gridy = 15;
        frame.getContentPane().add(totAvgControls, c);

        DynamicGraph totAvgGraph = new DynamicGraph(totavgList, "Average", "Piezo Output", "Time", totAvgControls.getLocalised(), totAvgControls.getTimeLimit(), totAvgControls.getOutliers());
        totavg.gridx = 0;
        totavg.gridy = 8;
        totavg.gridwidth = 1;
        totavg.gridheight = 7;
        totavg.weightx = 1;
        totavg.weighty = 0.7;
        totavg.anchor=GridBagConstraints.SOUTHWEST;
        totavg.fill=GridBagConstraints.HORIZONTAL;
        frame.getContentPane().add(totAvgGraph, totavg);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        for(int i =1; i<8000; i++){
            Thread.sleep(500);
            Random rand = new Random();
            int randint = rand.nextInt(10);
            double rad = Math.toRadians(10*i);
            int in = (int) (50*Math.sin(rad));
            list.add(in+50+randint);
            avgList.add(movingAverage(list));
            totavgList.add(average(list));
            DynamicGraph newRaw = new DynamicGraph(list, "Raw Piezo Output", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers());
            frame.remove(rawGraph);
            rawGraph = newRaw;
            frame.getContentPane().add(rawGraph, raw);

            DynamicGraph newAvg = new DynamicGraph(avgList, "Moving Average", "Piezo Output", "Time", avgControls.getLocalised(), avgControls.getTimeLimit(), avgControls.getOutliers());
            frame.remove(avgGraph);
            avgGraph = newAvg;
            frame.getContentPane().add(avgGraph, avg);

            DynamicGraph newTotAvg = new DynamicGraph(totavgList, "Average", "Piezo Output", "Time", totAvgControls.getLocalised(), totAvgControls.getTimeLimit(), totAvgControls.getOutliers());
            frame.remove(totAvgGraph);
            totAvgGraph = newTotAvg;
            frame.getContentPane().add(totAvgGraph, totavg);

            frame.revalidate();
            frame.repaint();
        }
    }

    static int movingAverage(ArrayList<Integer> data){
        int size = data.size();
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
        int count = 0;
        for(Integer i:data){
            count = count + i;
        }
        return count/size;
    }



    // arduino just makes the connection to the port where the arduino is
    public static void arduino() throws InterruptedException, IOException {
        sp = SerialPort.getCommPort("/dev/tty.usbmodem14301");

        sp.setComPortParameters(9600, 8, 1, 0);
        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
            return;
        }

    }

    // reads information from arduino port (specified in the arduino function) and adds it to the ArrayList Data
    // Data was initially an ArrayList<Integer> but it created some sort of error, we can keep it as a string or
    // TODO look into it
    // I got this from https://stackoverflow.com/questions/16608878/read-data-from-a-java-socket
    public static void getData() throws InterruptedException, IOException{
        BufferedReader bis = new BufferedReader(new InputStreamReader(sp.getInputStream()));
        String inputLine; // temporally stores the new number from the port
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); //this line
            // is essential so when the program starts looking at the port, it doesnt just "give up" if it sees no info
        inputLine = bis.readLine();

        // TODO it would be a good idea to add a check so the info is not null, but when i add this it doesnt work
        // while(inputLine != null)
        Data.add(inputLine); //add it to Data

    }


}






