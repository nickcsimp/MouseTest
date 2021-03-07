import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.io.*;
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
    SidePanel sidePanel;

    public DataRetrieval(ArrayList<Integer> data, SerialPort sp, GraphControls c, JFrame frame, RawGraph rawGraph, GridBagConstraints cs, int display, SidePanel sidePanel){
        this.data=data;
        this.sp = sp;
        rawControls=c;
        this.frame=frame;
        this.c = cs;
        this.rawGraph = rawGraph;
        this.display = display;
        this.samplingFreq=4;
        finished=false;
        this.sidePanel=sidePanel;
    }

    public void run(){
        while(!finished) {
            this.samplingFreq=4;
            long startTime = System.nanoTime();
            int input = 0;
            try {
                input = getData();
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
            System.out.println("Time elapsed: " + timeElapsed);
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
            sidePanel.updatePanel();
        }
    }

    public void setDisplay(int display){
        this.display=display;
    }

    // I got this from https://stackoverflow.com/questions/16608878/read-data-from-a-java-socket
    private Integer getData() throws IOException {
        BufferedReader bis = new BufferedReader(new InputStreamReader(sp.getInputStream()));
        Integer inputLine=0; // temporally stores the new number from the port
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        while (bis.ready())
        {
            inputLine=Integer.parseInt(bis.readLine());
            System.out.println(inputLine);
        }
        return inputLine;
    }

    public void updateGraph(){
        frame.remove(rawGraph);
        RawGraph newGraph = new RawGraph(data, "Mouse Respiratory Rate", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers(), samplingFreq);
        //newGraph.addAxis(freq4);
        rawGraph=newGraph;
        frame.add(rawGraph, c);
        frame.revalidate();
        frame.repaint();
    }

    public void remove(){
        frame.remove(rawGraph);
    }

    public double[] getFTInput(){
        int sampleCount = 4;
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
