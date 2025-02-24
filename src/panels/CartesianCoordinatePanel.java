package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CartesianCoordinatePanel extends JPanel {
    private int scale = 50;
    private List<Point> currentPoints = new ArrayList<>();
    private List<List<Point>> parallelograms = new ArrayList<>();
    private List<String> pointLabels = new ArrayList<>();
    private List<List<String>> parallelogramLabels = new ArrayList<>();
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

    public List<Point> getCurrentPoints() {
        return currentPoints;
    }

    public void addPoint(int x, int y) {
        currentPoints.add(new Point(x, y));
        pointLabels.add(String.valueOf(labelChar));
        labelChar++;

        repaint();
    }

    public void addParallelogram() {
        if (isParallelogram()) {
            parallelograms.add(new ArrayList<>(currentPoints));
            parallelogramLabels.add(new ArrayList<>(pointLabels));
        } else {
            JOptionPane.showMessageDialog(this, "The points do not form a parallelogram.");
        }
        currentPoints.clear();
        pointLabels.clear();
        labelChar = 'A';
        repaint();
    }

    public void clearPanel() {
        currentPoints.clear();
        parallelograms.clear();
        pointLabels.clear();
        parallelogramLabels.clear();
        labelChar = 'A';
        repaint();
    }

    public void drawParallelogram(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < parallelograms.size(); i++) {
            var parallelogram = parallelograms.get(i);
            var labels = parallelogramLabels.get(i);
            int[] xPoints = new int[4];
            int[] yPoints = new int[4];

            for (int j = 0; j < xPoints.length; j++) {
                xPoints[j] = centerX + parallelogram.get(j).x * scale;
                yPoints[j] = centerY - parallelogram.get(j).y * scale;
            }

            Point2D start = new Point2D.Float(xPoints[0], yPoints[0]);
            Point2D end = new Point2D.Float(xPoints[xPoints.length - 1], yPoints[yPoints.length - 1]);
            Color color1 = Color.GREEN;
            Color color2 = Color.YELLOW;
            LinearGradientPaint gradient = new LinearGradientPaint(start, end, new float[]{0f, 1f}, new Color[]{color1, color2});

            g2d.setPaint(gradient);
            g2d.fillPolygon(xPoints, yPoints, 4);

            for (int j = 0; j < parallelogram.size(); j++) {
                g2d.setColor(Color.BLACK);
                g2d.drawString(labels.get(j), xPoints[j] + 10, yPoints[j] - 5);
            }
        }
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

        for (int i = 0; i < currentPoints.size(); i++) {
            Point p = currentPoints.get(i);
            int x = centerX + p.x * scale;
            int y = centerY - p.y * scale;

            g2d.fillOval(x, y, 5, 5);

            g2d.setColor(Color.BLACK);
            g2d.drawString(pointLabels.get(i), x + 10, y - 5);
        }

        drawParallelogram(g2d, centerX, centerY);
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

    private boolean isParallelogram() {
        if (currentPoints.size() != 4) return false;

        Point p1 = currentPoints.get(0);
        Point p2 = currentPoints.get(1);
        Point p3 = currentPoints.get(2);
        Point p4 = currentPoints.get(3);

        int v1x = p2.x - p1.x, v1y = p2.y - p1.y;
        int v2x = p3.x - p2.x, v2y = p3.y - p2.y;
        int v3x = p4.x - p3.x, v3y = p4.y - p3.y;
        int v4x = p1.x - p4.x, v4y = p1.y - p4.y;

        return (v1x * v3y - v1y * v3x == 0) && (v2x * v4y - v2y * v4x == 0);
    }
}
