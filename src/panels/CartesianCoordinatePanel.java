package panels;

import managers.ParallelogramManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Comparator;
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
        List<Point> newParallelogram = manager.getCurrentPoints();
        if (newParallelogram.size() == 4) {
            if (checkOverlap(newParallelogram)) {
                newParallelogram.clear();
                JOptionPane.showMessageDialog(this, "This area is already occupied by another parallelogram.");
            } else {
                manager.createParallelogram();
            }
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Not enough points for the parallelogram (4 points)");
        }
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
            List<Point> screenPoints = calculatePolygonPoints(parallelogram, centerX, centerY);

            Point topLeft = screenPoints.getFirst();
            Point bottomLeft = null;
            Point bottomRight = null;

            List<Point> topPoints = screenPoints.stream()
                    .filter(p -> p.y == screenPoints.stream().mapToDouble(Point::getY).min().orElse(Integer.MIN_VALUE))
                    .toList();

            List<Point> bottomPoints = screenPoints.stream()
                    .filter(p -> p.y == screenPoints.stream().mapToDouble(Point::getY).max().orElse(Integer.MAX_VALUE))
                    .toList();

            if (!topPoints.isEmpty()) {
                topLeft = topPoints.stream()
                        .min(Comparator.comparingInt(p -> p.x))
                        .orElse(null);
            }

            if (!bottomPoints.isEmpty()) {
                bottomLeft = bottomPoints.stream()
                        .min(Comparator.comparingInt(p -> p.x))
                        .orElse(null);

                bottomRight = bottomPoints.stream()
                        .max(Comparator.comparingInt(p -> p.x))
                        .orElse(null);
            }

            Point2D start = screenPoints.get(0);
            Point2D end = screenPoints.get(3);
            Color color1 = Color.GREEN;
            Color color2 = Color.YELLOW;
            LinearGradientPaint gradient = new LinearGradientPaint(start, end, new float[]{0f, 1f}, new Color[]{color1, color2});

            g2d.setPaint(gradient);
            g2d.fillPolygon(
                    screenPoints.stream().mapToInt(p -> p.x).toArray(),
                    screenPoints.stream().mapToInt(p -> p.y).toArray(),
                    4);

            g2d.setColor(Color.BLACK);
            for (int j = 0; j < parallelogram.size(); j++) {
                g2d.drawString(labels.get(j), screenPoints.get(j).x + 10, screenPoints.get(j).y - 5);
            }

            if (isInSecondQuadrant(parallelogram)) {
                g2d.setColor(diagonalColor);
                g2d.drawLine(screenPoints.get(0).x, screenPoints.get(0).y, screenPoints.get(2).x, screenPoints.get(2).y);
                g2d.drawLine(screenPoints.get(1).x, screenPoints.get(1).y, screenPoints.get(3).x, screenPoints.get(3).y);

                g2d.setColor(Color.BLUE);
                double height = distanceFromPointToLine(topLeft, bottomLeft, bottomRight);
                int heightX = topLeft.x;
                int heightY = topLeft.y + (int) height;

                g2d.drawLine(topLeft.x, topLeft.y, heightX, heightY);
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

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

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

    private List<Point> calculatePolygonPoints(List<Point> parallelogram, int centerX, int centerY) {
        return parallelogram
                .stream()
                .map(p -> new Point(centerX + p.x * scale, centerY - p.y * scale))
                .toList();
    }

    private boolean checkOverlap(List<Point> newParallelogram) {
        for (List<Point> existingParallelogram : manager.getParallelograms()) {
            Polygon newPoly = new Polygon(
                    newParallelogram.stream().mapToInt(p -> p.x).toArray(),
                    newParallelogram.stream().mapToInt(p -> p.y).toArray(),
                    newParallelogram.size()
            );

            Polygon existingPoly = new Polygon(
                    existingParallelogram.stream().mapToInt(p -> p.x).toArray(),
                    existingParallelogram.stream().mapToInt(p -> p.y).toArray(),
                    existingParallelogram.size()
            );

            if (newPoly.intersects(existingPoly.getBounds2D())) {
                return true;
            }
        }
        return false;
    }
}
