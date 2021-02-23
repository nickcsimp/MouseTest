import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataRetrieval extends Thread{
    SerialPort sp;
    ArrayList<Integer> data;
    GraphControls rawControls;
    JFrame frame;
    int display;
    RawGraph rawGraph;
    GridBagConstraints c;
    int samplingFreq;

    public DataRetrieval(int samplingFreq, ArrayList<Integer> data, SerialPort sp, GraphControls c, JFrame frame, RawGraph rawGraph, GridBagConstraints cs, int display){
        this.data=data;
        this.sp = sp;
        rawControls=c;
        this.frame=frame;
        this.c = cs;
        this. rawGraph = rawGraph;
        this.display = display;
        this.samplingFreq=samplingFreq;
    }

    public void run(){
        while(true) {
            int input = 0;
            try {
                input = Integer.parseInt(getData());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e){
                input = data.get(data.size()-1);
            }
            if(input<1000) {
                    data.add(input);
            } else {
                data.add(data.get(data.size()-1));
            }

            if(display==1){
                updateGraph();
            }
            try {
                Thread.sleep(1000/samplingFreq);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDisplay(int display){
        this.display=display;
    }

    // I got this from https://stackoverflow.com/questions/16608878/read-data-from-a-java-socket
    private String getData() throws IOException {
        BufferedReader bis = new BufferedReader(new InputStreamReader(sp.getInputStream()));
        String inputLine=new String(); // temporally stores the new number from the port
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        while (bis.ready ())
        {
            inputLine=bis.readLine();
        }
        return inputLine;
    }

    public RawGraph getRawGraph(){
        return new RawGraph(data, "Raw Piezo Output", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers());
    }

    public void updateGraph(){
        frame.remove(rawGraph);
        RawGraph newGraph = new RawGraph(data, "Raw Piezo Output", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers());
        rawGraph=newGraph;
        frame.add(rawGraph, c);
        frame.revalidate();
        frame.repaint();
    }

    public void remove(){
        frame.remove(rawGraph);
    }

    public double[] getFTInput(){
        //TODO settings input
        //TODO time scales fucked
        double[] output = new double[40];
        if (data.size() > 40) {
            for(int i=0; i<40; i++){
                output[i]=data.get(data.size()-40+i);
            }
        } else {
            for(int i=0; i<data.size(); i++){
                output[i]=data.get(i);
            }
        }

        return output;
    }


}
