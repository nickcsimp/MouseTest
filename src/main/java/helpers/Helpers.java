package helpers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


// Various useful functions that can be standalone
public class Helpers {

    // Finds the global average of data for stats panel
    public static Integer average(List<Integer> data){
        Integer count = 0;
        for (Integer i:data) {
            count += i;
        }
        return count/data.size();
    }

    // Used if FT needed - gets the necessary number of data points and can be put into FT calculation
    public static double[] getFTInput(ArrayList<Integer> data, int sampleCount) {
        List<Integer> samples = data.subList(Math.max(data.size()-sampleCount, 0), data.size());
        double[] output = new double[sampleCount];
        double avg = average(samples);
        for(int i=0; i<samples.size(); i++){
            output[i]=data.get(i)-avg;
        }
        return output;
    }

    // Quicker way of defining gridbag constraints
    public static GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy, int anchor){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        c.fill= GridBagConstraints.HORIZONTAL;
        c.anchor=anchor;
        return c;
    }

    // With fewer arguments
    public static GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        return c;
    }

    // Finds the maximum number in the data to set the y axis maximum
    public static int maximum(List<Integer> list, int upper, int lower){
        Integer output = 0;
        for(int i=lower; i<=upper; i++) {
            if (list.get(i) > output) {
                output = list.get(i);
            }
        }
        return output;
    }

    // Finds the minimum number in the data to set the y axis minimum
    public static int minimum(List<Integer> list, int upper, int lower){
        Integer output = maximum(list, upper, lower); // Uses max as a limit instead of eg. 1000000
        for(int i=lower; i<=upper; i++){
            if(list.get(i)<output){
                output=list.get(i);
            }
        }
        return output;
    }
}
