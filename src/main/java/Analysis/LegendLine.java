package Analysis;

import javax.swing.*;
import java.awt.*;

public class LegendLine extends JPanel {
    private Color colour;
    public LegendLine(Color color){
        colour = color;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(colour);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
    }
}
