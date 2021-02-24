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
    boolean finished;
    FTControls ftControls;

    public DataRetrieval(FTControls ftControls, ArrayList<Integer> data, SerialPort sp, GraphControls c, JFrame frame, RawGraph rawGraph, GridBagConstraints cs, int display){
        this.data=data;
        this.sp = sp;
        rawControls=c;
        this.frame=frame;
        this.c = cs;
        this. rawGraph = rawGraph;
        this.display = display;
        this.samplingFreq=ftControls.getSampleFreq();
        finished=false;
        this.ftControls = ftControls;
    }

    public void run(){
        while(!finished) {
            this.samplingFreq=ftControls.getSampleFreq();
            long startTime = System.nanoTime();
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
            long endTime = System.nanoTime();

            long timeElapsed = endTime - startTime;
            int sleepTime = 0;
            if(((1000/samplingFreq)-timeElapsed/1000000)>0){
                sleepTime=(int)((1000/samplingFreq)-timeElapsed/1000000);
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                finished=true;
                return;
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

    public void updateGraph(){
        frame.remove(rawGraph);
        RawGraph newGraph = new RawGraph(data, "Raw Piezo Output", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers(), samplingFreq);
        rawGraph=newGraph;
        frame.add(rawGraph, c);
        frame.revalidate();
        frame.repaint();
    }

    public void remove(){
        frame.remove(rawGraph);
    }

    public double[] getFTInput(){
        int sampleCount = ftControls.getSampleCount();
        double[] output = new double[sampleCount];
        double count = 0;
        if (data.size() > sampleCount) {
            for(int i=0; i<sampleCount; i++){
                count += data.get(data.size()-sampleCount+i);
            }
            double avg = count/sampleCount;
            for(int i=0; i<sampleCount; i++){
                output[i]=data.get(data.size()-sampleCount+i)-avg;
            }
        } else {
            for(int i=0; i<data.size(); i++){
                count += data.get(i);
            }
            double avg = count/sampleCount;
            for(int i=0; i<data.size(); i++){
                output[i]=data.get(i)-avg;
            }
        }
        return output;
    }


}
