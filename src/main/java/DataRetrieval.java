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
            this.samplingFreq=4;//TODO sort out
            long startTime = System.nanoTime(); //This begins a timer so that we retrieve data exactly every second
            int input = 0;
            try {
                input = getData(); //This reads data from the serial port
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e){
                input = data.get(data.size()-1); //If the new number is fucked for any reason, we use the last number
            }
            if(input<1000) {
                    data.add(input);
            } else {
                data.add(data.get(data.size()-1)); //If the new number is ridiculous, we use the last number and assume anomaly
            }
            if(display==1){
                updateGraph(); //If the correct screen is meant to be showing (Pause is 2 and there used to be more)
            }
            long endTime = System.nanoTime(); //Ends timer
            long timeElapsed = endTime - startTime; //Calculates time taken (Obvs not exact but better than nothing)
            //System.out.println("Time elapsed: " + timeElapsed);

            int sleepTime = 0;
            if(((1000/samplingFreq)-timeElapsed/1000000)>0){ //If above was faster than sampling frequency then we need to wait
                sleepTime=(int)((1000/samplingFreq)-timeElapsed/1000000);
            }

            try {
                Thread.sleep(sleepTime); //Wait if necessary
            } catch (InterruptedException e) {
                finished=true; //If process is interupted then we stop
                return;
            }
            //Update stats panel
            sidePanel.setCurrent(input);
            sidePanel.setAverage(average());
            sidePanel.updatePanel();
        }
    }

    //Used to pause graph update
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

    //Updates graph with new info
    public void updateGraph(){
        frame.remove(rawGraph); //Remove old things
        //Use a dummy graph
        RawGraph newGraph = new RawGraph(data, "Mouse Respiratory Rate", "Piezo Output", "Time", rawControls.getLocalised(), rawControls.getTimeLimit(), rawControls.getOutliers(), samplingFreq);
        //newGraph.addAxis(freq4);
        rawGraph=newGraph;//Change to normal graph

        frame.add(rawGraph, c); //Add to frame and refresh
        frame.revalidate();
        frame.repaint();
    }

    //Removes graph - not sure if ever needed
    public void remove(){
        frame.remove(rawGraph);
    }

    //Used if FT needed - gets the necessary number of data points and can be put into FT calculation
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

    //Finds the global average of data for stats panel
    public int average(){
        int count=0;
        for(Integer i:data){
            count+=i;
        }
        return count/data.size();
    }

}
