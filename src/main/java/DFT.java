import javax.swing.*;
import java.awt.*;

public class DFT extends Thread{
    double[] shifted;
    int samplingFreq;
    DataRetrieval data;
    double freq;
    double bpm;
    JFrame frame;
    int display;
    FTGraph ftGraph;
    GridBagConstraints c;
    boolean finished;
    FTControls ftControls;

    public DFT(DataRetrieval data, FTControls ftControls, JFrame frame, int display, FTGraph ftGraph, GridBagConstraints c){
        this.data = data;
        this.samplingFreq=ftControls.getSampleFreq();
        this.frame=frame;
        this.display=display;
        this.c=c;
        this.ftGraph=ftGraph;
        shifted = new double[ftControls.getSampleCount()];
        finished = false;
        this.ftControls=ftControls;
    }
    public void run(){
        while(!finished) {
            double[] inreal = data.getFTInput();
            double realCount = 0;
            int n = inreal.length;
            double[] outreal = new double[n];
            samplingFreq=ftControls.getSampleFreq();

            shifted = new double[n / 2];
            for (int k = 0; k < n; k++) {
                realCount += inreal[k];
            }
            double realMean = realCount / n;
            for (int k = 0; k < n; k++) {
                inreal[k] = inreal[k] - realMean;
            }
            for (int k = 0; k < n; k++) {  // For each output element
                double sumreal = 0;
                for (int t = 0; t < n; t++) {  // For each input element
                    double angle = 2 * Math.PI * t * k / n;
                    sumreal += inreal[t] * Math.cos(angle);
                }
                outreal[k] = sumreal;
            }
            int maxInd = 0;
            double maxOut = 0;
            for (int j = 0; j < n / 2; j++) {
                shifted[j] = outreal[j];
                if(shifted[j]>maxOut){
                    maxOut=shifted[j];
                    maxInd=j;
                }
            }
            freq=maxInd*samplingFreq/(double)inreal.length;
            bpm=60*freq;
            System.out.println("MaxInd: "+ maxInd);
            if(display==1){
                updateGraph();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                finished=true;
                return;
            }
        }
    }

    public double getBpm(){
        return bpm;
    }

    public void setDisplay(int display){
        this.display=display;
    }

    public void updateGraph(){
        frame.remove(ftGraph);
        FTGraph newGraph = new FTGraph(shifted, ftControls);
        ftGraph=newGraph;
        frame.add(ftGraph, c);
        frame.revalidate();
        frame.repaint();
    }

    public void remove(){
        frame.remove(ftGraph);
    }
}
