package data;

import com.fazecast.jSerialComm.SerialPort;
import helpers.GlobalSettings;
import ui.DataPresenter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever extends PausableSwingWorker<Void, Integer> {

    private final DataPresenter presenter;
    private final SerialPort sp;
    private final int timePeriodMs = 1000/GlobalSettings.INSTANCE.getSamplingFrequency(); // Get sampling frequency from global settings

    public DataRetriever(DataPresenter presenter, SerialPort sp){
        this.presenter = presenter;
        this.sp = sp;
        this.sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
    }

    @Override
    protected Void doInBackground() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        while(!isCancelled()) {
            if (!isPaused()) {
                int dataPoint = 0;
                // long startTime = System.nanoTime();
                try {
                    dataPoint = getData();
                    if (dataPoint  > 1000) {
                        dataPoint = data.get(data.size()-1); // If the new number is ridiculous, we use the last number and assume anomaly
                    }
                } catch (NumberFormatException e) {
                    if (data.size() >= 1) { // Need to check that size of data is not 0
                        dataPoint = data.get(data.size()-1); // If the new number is f*cked for any reason, we use the last number
                    }
                }
                data.add(dataPoint);
                publish(dataPoint);
                // The part below is commented out as currently the part of the loop above consistently takes much less than 1ms
                // long endTime = System.nanoTime(); // Ends timer
                // long timeElapsedMs = (endTime - startTime)/1000000; // Calculates time taken (Obvs not exact but better than nothing)
                // System.out.println("Time elapsed (ms): " + timeElapsedMs);
                // int sleepTime = (int) Math.max(0, timePeriodMs-timeElapsedMs);
                Thread.sleep(timePeriodMs);
            } else {
                Thread.sleep(200);
            }
        }
        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        presenter.update(chunks);
    }

    private Integer getData() throws IOException, InterruptedException {
        BufferedReader bis = new BufferedReader(new InputStreamReader(sp.getInputStream()));
        while (!bis.ready()) { this.wait(); } // Wait until stream can be read
        return Integer.parseInt(bis.readLine()); // As Arduino code uses println, the data is sent as a string
    }

}
