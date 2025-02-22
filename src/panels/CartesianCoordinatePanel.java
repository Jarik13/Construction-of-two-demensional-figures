package panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CartesianCoordinatePanel extends JPanel {
    private int scale = 50;
    private List<Point> points = new ArrayList<>();
    private List<String> pointLabels = new ArrayList<>();
    private char labelChar = 'A';

    public CartesianCoordinatePanel() {
        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            if (rotation < 0) {
                scale += 5;
            } else if (rotation > 0 && scale > 10) {
                scale -= 5;
            }
            repaint();
        });
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
        pointLabels.add(String.valueOf(labelChar));
        labelChar++;
        repaint();
    }

    public void removePoint(int x, int y) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x == x && points.get(i).y == y) {
                points.remove(i);
                pointLabels.remove(i);
                labelChar--;
                break;
            }
        }
        repaint();
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));

        g2d.drawLine(0, centerY, width, centerY);
        g2d.drawLine(centerX, 0, centerX, height);

        drawArrow(g2d, width, centerY, true);
        drawArrow(g2d, centerX, 0, false);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        g2d.drawString("X", width - 20, centerY - 5);
        g2d.drawString("Y", centerX + 5, 15);

        drawGrid(g2d, width, height, centerX, centerY);

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            int x = centerX + p.x * scale;
            int y = centerY - p.y * scale;

            g2d.fillOval(x - 5, y - 5, 5, 5);

            g2d.setColor(Color.BLACK);
            g2d.drawString(pointLabels.get(i), x + 10, y - 5);
        }
    }

    private void drawArrow(Graphics2D g2d, int x, int y, boolean isXAxis) {
        int arrowSize = 10;
        if (isXAxis) {
            g2d.drawLine(x - arrowSize, y - arrowSize / 2, x, y);
            g2d.drawLine(x - arrowSize, y + arrowSize / 2, x, y);
        } else {
            g2d.drawLine(x - arrowSize / 2, y + arrowSize, x, y);
            g2d.drawLine(x + arrowSize / 2, y + arrowSize, x, y);
        }
    }

    private void drawGrid(Graphics2D g2d, int width, int height, int centerX, int centerY) {
        g2d.setStroke(new BasicStroke(1));

        for (int x = centerX + scale; x < width; x += scale) {
            g2d.drawLine(x, centerY - 5, x, centerY + 5);
            g2d.drawString(String.valueOf((x - centerX) / scale), x - 5, centerY + 20);
        }
        for (int x = centerX - scale; x > 0; x -= scale) {
            g2d.drawLine(x, centerY - 5, x, centerY + 5);
            g2d.drawString(String.valueOf((x - centerX) / scale), x - 10, centerY + 20);
        }

        for (int y = centerY - scale; y > 0; y -= scale) {
            g2d.drawLine(centerX - 5, y, centerX + 5, y);
            g2d.drawString(String.valueOf((centerY - y) / scale), centerX + 10, y + 5);
        }
        for (int y = centerY + scale; y < height; y += scale) {
            g2d.drawLine(centerX - 5, y, centerX + 5, y);
            g2d.drawString(String.valueOf((centerY - y) / scale), centerX + 10, y + 5);
        }
    }
}
