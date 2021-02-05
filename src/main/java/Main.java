import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

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

        //Create mainFrame
        JFrame frame = new JFrame("Mousify");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        //Initialise list
        ArrayList<Integer> list = new ArrayList<>();
        //Initialise graphPanel with empty list
        DynamicGraph graphPanel = new DynamicGraph(list, true, 300);
        frame.getContentPane().add(graphPanel);


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
                //Makes the new graoh with the new data
                DynamicGraph graphNew = new DynamicGraph(list, true, 20);
                //Removes old graph
                frame.remove(graphPanel);
                //Changes name
                graphNew = graphPanel;
                //Adds new graph
                frame.getContentPane().add(graphPanel);
                //Repaints the frame to add the new graph
                frame.revalidate();
                frame.repaint();
            }
        };
        new Timer(500, taskPerformer).start();
    }

    public static void dynamicGraph() throws InterruptedException {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(50);
        DynamicGraph graph = new DynamicGraph(list, true, 20);
        JFrame frame = new JFrame("DynamicGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graph);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        for(int i =1; i<80; i++){
            Thread.sleep(500);
            Random rand = new Random();
            int randint = rand.nextInt(10);
            list.add(randint+45);
            DynamicGraph graphNew = new DynamicGraph(list, true, 20);
            frame.remove(graph);
            graph = graphNew;
            frame.getContentPane().add(graph);
            frame.revalidate();
            frame.repaint();
        }
        for(int i =80; i<1000; i++){
            Thread.sleep(500);
            Random rand = new Random();
            int randint = rand.nextInt(10);
            list.add(randint+45);
            DynamicGraph graphNew = new DynamicGraph(list, true ,20);
            frame.remove(graph);
            graph = graphNew;
            frame.getContentPane().add(graph);
            frame.revalidate();
            frame.repaint();
        }
    }

    // this is me just fooling around to see if i could get a graph based on the Data
    public static void sensorGraph() throws InterruptedException{
        //SensorGraph dataGraph = new SensorGraph();
        JFrame frame = new JFrame("SensorGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(dataGraph);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // arduino just makes the connection to the port where the arduino is
    public static void arduino() throws InterruptedException, IOException {
        sp = SerialPort.getCommPort("/dev/tty.usbmodem14101");

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






