package Analysis;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class RotatedUI extends BasicLabelUI {

    @Override
    public Dimension getPreferredSize(JComponent c){
        Dimension oldDim = super.getPreferredSize(c);
        return new Dimension(oldDim.height, oldDim.width);
    }

    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D)g;
        AffineTransform trans = g2.getTransform();
        JLabel lab = (JLabel)c;
        String input = lab.getText();

        FontMetrics f = g.getFontMetrics();

        g2.rotate(Math.toRadians(-90));
        g2.translate(-c.getHeight(), 0);

        paintEnabledText(lab, g, input, 0, f.getAscent());

        g2.setTransform(trans);
    }
}
