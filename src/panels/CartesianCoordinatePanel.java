package panels;

import managers.ParallelogramManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CartesianCoordinatePanel extends JPanel {
    private int scale = 50;
    private Color diagonalColor = Color.RED;
    private final ParallelogramManager manager = new ParallelogramManager();

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
        return manager.getCurrentPoints();
    }

    public Color getDiagonalColor() {
        return diagonalColor;
    }

    public void setDiagonalColor(Color diagonalColor) {
        this.diagonalColor = diagonalColor;
        repaint();
    }

    public void addPoint(int x, int y) {
        manager.addPoint(x, y);
        repaint();
    }

    public void addParallelogram() {
        manager.createParallelogram();
        repaint();
    }

    public void clearPanel() {
        manager.clearPoints();
        repaint();
    }

    public void drawParallelogram(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < manager.getParallelograms().size(); i++) {
            var parallelogram = manager.getParallelograms().get(i);
            var labels = manager.getParallelogramLabels().get(i);
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

            if (isInSecondQuadrant(parallelogram)) {
                g2d.setColor(diagonalColor);
                g2d.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);
                g2d.drawLine(xPoints[1], yPoints[1], xPoints[3], yPoints[3]);

                g2d.setColor(Color.BLUE);
                double distance = distanceFromPointToLine(parallelogram.get(0),
                        parallelogram.get(2), parallelogram.get(3));
                int heightX = xPoints[0];
                int heightY = yPoints[0] + (int) distance * scale;

                g2d.drawLine(xPoints[0], yPoints[0], heightX, heightY);
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

        for (int i = 0; i < manager.getCurrentPoints().size(); i++) {
            Point p = manager.getCurrentPoints().get(i);
            int x = centerX + p.x * scale;
            int y = centerY - p.y * scale;

            g2d.fillOval(x, y, 5, 5);

            g2d.setColor(Color.BLACK);
            g2d.drawString(manager.getPointLabels().get(i), x + 10, y - 5);
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

    private boolean isInSecondQuadrant(List<Point> parallelogram) {
        for (Point point : parallelogram) {
            if (!(point.x < 0 && point.y > 0)) {
                return false;
            }
        }

        return  true;
    }

    private double distanceFromPointToLine(Point p, Point lineStart, Point lineEnd) {
        double numerator = Math.abs((lineEnd.y - lineStart.y) * p.x - (lineEnd.x - lineStart.x) * p.y + lineEnd.x * lineStart.y - lineEnd.y * lineStart.x);
        double denominator = Math.sqrt(Math.pow(lineEnd.y - lineStart.y, 2) + Math.pow(lineEnd.x - lineStart.x, 2));
        return numerator / denominator;
    }
}
