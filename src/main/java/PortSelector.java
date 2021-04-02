import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PortSelector {

    JFrame parent;
    DataRetriever dataRetriever;

    // Initialise PortSelector with a DataRetriever object
    // Once a selection is made, the serial port is set on the DataRetriever
    public PortSelector(JFrame parent, DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
        this.parent = parent;
    }

    private void openPort(SerialPort port) {
        port.setComPortParameters(9600, 8, 1, 0);
        port.openPort();
    }

    public void selectInput() {
        int choice = -1;
        while (choice == -1) {
            String message = "Please select the USB port to which the Arduino is connected";
            ArrayList<SerialPort> availablePorts = new ArrayList<>(Arrays.asList(SerialPort.getCommPorts()));
            ArrayList<String> options = new ArrayList<>();
            for (SerialPort p: availablePorts) {
                options.add(p.getSystemPortName());
            }
            choice = JOptionPane.showOptionDialog(parent, message, "Choose port", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options.toArray(), options.get(0));
            if (choice != -1) {
                SerialPort selectedPort = availablePorts.get(choice);
                try {
                    openPort(selectedPort);
                    dataRetriever.setSerialPort(selectedPort);
                } catch (Exception e) {
                    choice = -1;
                }
            }
        }
    }

}
