package ui;

import helpers.Helpers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataPresenter {

    JFrame parent;
    GraphControls homeControls;
    SidePanel sidePanel;
    RawGraph rawGraph;
    ArrayList<Integer> data;

    private static class SETTINGS {
        private static final String TITLE = "Mouse Respiratory Rate";
        private static final String Y_AXIS_LABEL = "Piezo Output";
        private static final String X_AXIS_LABEL = "Time";
        private static final GridBagConstraints GRAPH_CONSTRAINTS = Helpers.gridConstraints(0,2,10,6,0.5,0.8, GridBagConstraints.WEST);
        private static final GridBagConstraints HOME_CONTROLS_CONSTRAINTS = Helpers.gridConstraints(0,9,6,1,0.5,0.1, GridBagConstraints.WEST);
        private static final GridBagConstraints SIDE_PANEL_CONSTRAINTS = Helpers.gridConstraints(4,0,10,2,0.5,0.8, GridBagConstraints.EAST);
    }

    public DataPresenter(JFrame parent) {

        this.data = new ArrayList<>();
        this.parent = parent;
        this.homeControls = new GraphControls();
        this.sidePanel = new SidePanel();
        this.rawGraph = new RawGraph(new ArrayList<>(), SETTINGS.TITLE, SETTINGS.Y_AXIS_LABEL, SETTINGS.X_AXIS_LABEL);

        // Adds everything to frame
        parent.add(rawGraph, SETTINGS.GRAPH_CONSTRAINTS);
        parent.add(homeControls, SETTINGS.HOME_CONTROLS_CONSTRAINTS);
        parent.add(sidePanel, SETTINGS.SIDE_PANEL_CONSTRAINTS);

    }

    public void update(List<Integer> chunks) {
        data.addAll(chunks);
        updateGraph(data);
        updatePanel(data);
    }

    // Update stats panel
    private void updatePanel(List<Integer> data) {
        sidePanel.setCurrentLabel(data.get(data.size()-1)); // Last item in data is the latest reading
        sidePanel.setAverageLabel(Helpers.average(data));
        sidePanel.updatePanel();
    }

    // Updates graph with new info
    private void updateGraph(List<Integer> data){
        rawGraph.updateData(data);
    }



}
