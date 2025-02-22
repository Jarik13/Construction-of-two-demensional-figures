package panels;

import javax.swing.*;
import java.awt.*;

public class ParallelogramPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int scale = 2;

        int[] xPoints = {100 * scale, 150 * scale, 200 * scale, 150 * scale, 100 * scale, 50 * scale};
        int[] yPoints = {50 * scale, 50 * scale, 100 * scale, 150 * scale, 150 * scale, 100 * scale};

        g2d.setColor(Color.BLUE);
        g2d.fillPolygon(xPoints, yPoints, 6);

        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints, yPoints, 6);
    }
}
