import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FTRetrieval extends Thread{
    DFT dft;
    JFrame frame;
    RawGraph procGraph;
    GridBagConstraints c;
    int display;
    ArrayList<Integer> processedData;
    ArrayList<Integer> tempData;
    GraphControls homeControls;
    int samplingFreq;
    int filterLength;
    boolean finished;
    SidePanel sidePanel;

    public FTRetrieval(DFT dft, JFrame frame, RawGraph procGraph, GridBagConstraints c, int Display, GraphControls homeControls, int samplingFreq, SidePanel sidePanel){
        this.dft = dft;
        this.frame=frame;
        this.procGraph=procGraph;
        this.c=c;
        this.display=Display;
        this.homeControls=homeControls;
        processedData=new ArrayList<>();
        this.samplingFreq=samplingFreq;
        filterLength=sidePanel.getFilterLength();
        tempData = new ArrayList<>();
        finished=false;
        this.sidePanel=sidePanel;
    }

    public void run(){
        while(!finished){
            filterLength=sidePanel.getFilterLength();
            System.out.println("Retrieval: "+ dft.getBpm());//TODO 42?
            if(tempData.size()<filterLength || tempData.isEmpty()) {
                tempData.add((int) dft.getBpm());
            } else {
                for(int i=0; i<filterLength-1; i++){
                    tempData.set(i, tempData.get(i+1));
                }
                tempData.set(filterLength-1, (int) dft.getBpm());
            }
            processedData.add(movingAverage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                finished = true;
                return;
            }
            if(display==0){
                updateGraph();
            }
            sidePanel.setCurrent(movingAverage());
            sidePanel.setAverage(average());
        }
    }

    public void updateGraph(){
        frame.remove(procGraph);
        RawGraph newGraph = new RawGraph(processedData, "Moving Average Breaths Per Minute", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers(), 1);
        procGraph=newGraph;
        frame.add(procGraph, c);
        frame.revalidate();
        frame.repaint();
    }

    public void remove(){
        frame.remove(procGraph);
    }

    public void setDisplay(int display){
        this.display=display;
    }

    public int movingAverage(){
        int output=0;
        int x=filterLength;
        if(tempData.size()<=filterLength){
            x=tempData.size();
        }
        for(int i=0; i<x; i++){
            output+=tempData.get(i);
        }
        return output/x;
    }

    public int average(){
        int output=0;
        int x=processedData.size();
        for(int i=0; i<x; i++){
            output+=processedData.get(i);
        }
        return output/x;
    }
}
