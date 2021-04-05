import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;
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
        String choice = "";
        while (choice.equals("")) {
            String message = "Please select the USB port to which the Arduino is connected";
            ArrayList<SerialPort> availablePorts = new ArrayList<>(Arrays.asList(SerialPort.getCommPorts()));
            if (availablePorts.size() > 0) {
                ArrayList<String> options = new ArrayList<>();
                for (SerialPort p: availablePorts) {
                    options.add(p.getSystemPortName());
                }
                choice = (String) JOptionPane.showInputDialog(parent, message, "Choose port", JOptionPane.QUESTION_MESSAGE, null, options.toArray(), options.get(0));
                if (!choice.equals("")) {
                    SerialPort selectedPort = availablePorts.get(options.indexOf(choice));
                    try {
                        openPort(selectedPort);
                        dataRetriever.setSerialPort(selectedPort);
                    } catch (Exception e) {
                        choice = "";
                    }
                }
            } else {
                JOptionPane.showMessageDialog(parent, "There are no input devices connected, please plug in the Arduino");
            }
        }
    }

}
