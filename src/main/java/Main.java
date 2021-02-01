import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        arduino();
        //dynamicGraph();
    }

    public static void normalGraph(){
        ArrayList<Integer> list = new ArrayList<>();
        for(int i=0; i<11; i++) {
            list.add(i);
            list.add(10-i);
        }
        DrawGraph graph = new DrawGraph(list);
        JFrame frame = new JFrame("DrawGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graph);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void dynamicGraph() throws InterruptedException {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        DynamicGraph graph = new DynamicGraph(list);
        JFrame frame = new JFrame("DynamicGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graph);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        for(int i =0; i<100; i++){
            Thread.sleep(1000);
            list.add(i);
            DynamicGraph graphNew = new DynamicGraph(list);
            frame.remove(graph);
            graph = graphNew;
            frame.getContentPane().add(graph);
            frame.revalidate();
            frame.repaint();
        }
    }

    public static void arduino() throws InterruptedException, IOException {
        SerialPort sp = SerialPort.getCommPort("/dev/cu.usbmodem14101");
        //sp.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 5000);
        sp.setComPortParameters(9600, 8, 1, 0);
        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
            return;
        }
        sp.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[sp.bytesAvailable()];
                int numRead = sp.readBytes(newData, newData.length);
                String s = new String(newData, StandardCharsets.UTF_8);
                //System.out.println("Read " + numRead + " bytes.");
                int i=Integer.parseInt(s);
                if(s==""){
                    System.out.println("New: null");
                }
                else{
                    System.out.println("New: "+s);
                }

            }
        });
    }
}






