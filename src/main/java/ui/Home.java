package ui;

import com.fazecast.jSerialComm.SerialPort;
import data.DataRetriever;
import helpers.Helpers;

import javax.swing.*;
import java.awt.*;

public class Home {

    private DataRetriever dataRetriever;
    private final DataPresenter dataPresenter;
    private final SerialPort serialPort;
    private final JFrame mainFrame;
    private JButton pauseButton;
    private JButton startStopButton;
    private boolean started = false;

    private static class SETTINGS {
        private static final String APP_NAME = "Mousify";
        private static final String PAUSE_TEXT = "Pause";
        private static final String RESUME_TEXT = "Resume";
        private static final String START_TEXT = "Start";
        private static final String STOP_TEXT = "Stop";
        private static final GridBagConstraints PAUSE_CONSTRAINTS = Helpers.gridConstraints(6,9,2,3,0.25,0.1, GridBagConstraints.NORTHEAST);
        private static final GridBagConstraints START_CONSTRAINTS = Helpers.gridConstraints(8,9,2,1,0.25,0.1, GridBagConstraints.NORTHEAST);
    }

    public Home(){
        this.mainFrame = setupMainFrame();
        this.serialPort = new PortSelector(this.mainFrame).selectInput();
        this.dataPresenter = new DataPresenter(this.mainFrame);
        setupButtons();
    }

    private JFrame setupMainFrame() {
        JFrame frame = new JFrame(SETTINGS.APP_NAME);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        return frame;
    }


    private void setupButtons() {

        pauseButton = new JButton(SETTINGS.PAUSE_TEXT);
        startStopButton = new JButton(SETTINGS.START_TEXT);

        mainFrame.add(startStopButton, SETTINGS.START_CONSTRAINTS);

        pauseButton.addActionListener(evt-> {
            if (dataRetriever == null) { return; }
            if (this.dataRetriever.isPaused()) {
                pauseButton.setText(SETTINGS.PAUSE_TEXT);
                this.dataRetriever.resume();
            } else {
                pauseButton.setText(SETTINGS.RESUME_TEXT);
                this.dataRetriever.pause();
            }
        });

        startStopButton.addActionListener(evt->{
            if (started) {
                started = false;
                if (dataRetriever == null) { return; }
                mainFrame.remove(pauseButton);
                startStopButton.setText(SETTINGS.START_TEXT);
                this.dataRetriever.cancel(true);
                hidePauseButton();
            } else {
                started = true;
                startStopButton.setText(SETTINGS.STOP_TEXT);
                this.dataRetriever = new DataRetriever(this.dataPresenter, this.serialPort);
                this.dataRetriever.execute();
                showPauseButton();
            }
        });

    }

    private void hidePauseButton() {
        mainFrame.remove(pauseButton);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void showPauseButton() {
        mainFrame.add(pauseButton, SETTINGS.PAUSE_CONSTRAINTS);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

}
