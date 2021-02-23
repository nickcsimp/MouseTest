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
    GraphControls homeControls;
    public FTRetrieval(DFT dft, JFrame frame, RawGraph procGraph, GridBagConstraints c, int Display, GraphControls homeControls){
        this.dft = dft;
        this.frame=frame;
        this.procGraph=procGraph;
        this.c=c;
        this.display=Display;
        this.homeControls=homeControls;
        processedData=new ArrayList<>();
    }

    public void run(){
        while(true){
            processedData.add((int)dft.getBpm());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(display==0){
                updateGraph();
            }
        }
    }

    public void updateGraph(){
        frame.remove(procGraph);
        RawGraph newGraph = new RawGraph(processedData, "Moving Average Breaths Per Minute", "Breaths per Minute", "Time (s)", homeControls.getLocalised(), homeControls.getTimeLimit(), homeControls.getOutliers());
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
}
