package Analysis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ReviewPanel extends JPanel {

    String titel;
    String xlabel;
    String ylabel;
    JPanel titlePanel = new JPanel();
    JPanel yPanel = new JPanel();
    JPanel xPanel = new JPanel();

    private static class SETTINGS {
        private static final int PREF_W = 1200;
        private static final int PREF_H = 700;
    }


    public ReviewPanel(ReviewGraph graph){
        this.titel="Title";
        this.xlabel="XLabel";
        this.ylabel="YLabel";
        this.setLayout(new GridBagLayout());
        JPanel topLeft = new JPanel();
        JPanel bottomLeft = new JPanel();

        GridBagConstraints titleC = gridConstraints(1, 0, 9, 1, 1, 0.1, GridBagConstraints.NORTH);
        GridBagConstraints yC = gridConstraints(0, 1, 4, 8, 0.4, 0.8, GridBagConstraints.WEST);
        GridBagConstraints xC = gridConstraints(1, 9, 9, 1, 1, 0.1, GridBagConstraints.SOUTH);
        GridBagConstraints graphC = gridConstraints(4, 1, 6, 8, 0.6, 0.8, GridBagConstraints.EAST);
        GridBagConstraints tlC = gridConstraints(0, 0, 1, 1, 0.1, 0.1, GridBagConstraints.NORTHWEST);
        GridBagConstraints blC = gridConstraints(0, 9, 1, 1, 0.1, 0.1, GridBagConstraints.SOUTHWEST);

        this.add(titlePanel, titleC);
        this.add(xPanel, xC);
        this.add(yPanel, yC);
        this.add(graph, graphC);
        this.add(bottomLeft, blC);
        this.add(topLeft, tlC);


        Font titleFont = new Font(null, Font.PLAIN, 30);
        Font xFont = new Font(null, Font.PLAIN, 20);

        JLabel title = new JLabel(titel);
        title.setFont(titleFont);
        JLabel xLabel = new JLabel(xlabel);
        xLabel.setFont(xFont);
        JLabel yLabel = new JLabel(ylabel);
        yLabel.setFont(xFont);
        yLabel.setUI(new RotatedUI());

        titlePanel.add(title);
        xPanel.add(xLabel);
        yPanel.add(yLabel);

        titlePanel.addMouseListener(new ChangeTitle());
        xPanel.addMouseListener(new ChangeX());
        yPanel.addMouseListener(new ChangeY());
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ReviewPanel.SETTINGS.PREF_W, ReviewPanel.SETTINGS.PREF_H);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(ReviewPanel.SETTINGS.PREF_W, ReviewPanel.SETTINGS.PREF_H);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(ReviewPanel.SETTINGS.PREF_W, ReviewPanel.SETTINGS.PREF_H);
    }

    // Quicker way of defining gridbag constraints
    //TODO copied from home
    private GridBagConstraints gridConstraints(int x, int y, int width, int height, double wx, double wy, int anchor){
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x;
        c.gridy=y;
        c.gridwidth=width;
        c.gridheight=height;
        c.weightx=wx;
        c.weighty=wy;
        c.anchor=anchor;
        return c;
    }

    public class ChangeTitle implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Font titleFont = new Font(null, Font.PLAIN, 30);
            JTextField text = new JTextField(titel);
            titlePanel.removeAll();
            titlePanel.add(text);
            text.setFont(titleFont);
            ReviewPanel.this.revalidate();
            ReviewPanel.this.repaint();
            text.addActionListener(evt->{
                if(text.getText().equals("")) {
                    titel=("Title");
                }
                else {titel=text.getText();}
                titlePanel.remove(text);
                JLabel lab = new JLabel(titel);
                lab.setFont(titleFont);
                titlePanel.add(lab);
                ReviewPanel.this.revalidate();
                ReviewPanel.this.repaint();
            });
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    public class ChangeY implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Font xFont = new Font(null, Font.PLAIN, 20);
            JTextField text = new JFormattedTextField(ylabel);
            text.setFont(xFont);
            yPanel.removeAll();
            yPanel.add(text);
            ReviewPanel.this.revalidate();
            ReviewPanel.this.repaint();
            text.addActionListener(evt->{
                if(text.getText().equals("")){
                    ylabel="yLabel";
                }
                else {ylabel=text.getText();}
                yPanel.remove(text);
                JLabel lab = new JLabel(ylabel);
                lab.setFont(xFont);
                lab.setUI(new RotatedUI());
                yPanel.add(lab);
                ReviewPanel.this.revalidate();
                ReviewPanel.this.repaint();
            });
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    public class ChangeX implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Font xFont = new Font(null, Font.PLAIN, 20);
            JTextField text = new JFormattedTextField(xlabel);
            text.setFont(xFont);
            xPanel.removeAll();
            xPanel.add(text);
            ReviewPanel.this.revalidate();
            ReviewPanel.this.repaint();
            text.addActionListener(evt->{
                if(text.getText().equals("")){
                    xlabel="xLabel";
                }
                else {xlabel=text.getText();}
                xPanel.remove(text);
                JLabel lab = new JLabel(xlabel);
                lab.setFont(xFont);
                xPanel.add(lab);
                ReviewPanel.this.revalidate();
                ReviewPanel.this.repaint();
            });
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

}
