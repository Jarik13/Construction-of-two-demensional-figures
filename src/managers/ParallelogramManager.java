package managers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ParallelogramManager {
    private List<Point> currentPoints = new ArrayList<>();
    private List<java.util.List<Point>> parallelograms = new ArrayList<>();
    private List<String> pointLabels = new ArrayList<>();
    private List<List<String>> parallelogramLabels = new ArrayList<>();
    private char labelChar = 'A';

    public List<Point> getCurrentPoints() {
        return currentPoints;
    }

    public List<String> getPointLabels() {
        return pointLabels;
    }

    public List<List<Point>> getParallelograms() {
        return parallelograms;
    }

    public List<List<String>> getParallelogramLabels() {
        return parallelogramLabels;
    }

    public void addPoint(int x, int y) {
        if (currentPoints.size() < 3) {
            currentPoints.add(new Point(x, y));
            pointLabels.add(String.valueOf(labelChar));
            labelChar++;
        }
        if (currentPoints.size() == 3) {
            reorderPoints();
            addFourthPoint();
        }
    }

    public void createParallelogram() {
        if (isParallelogram()) {
            parallelograms.add(new ArrayList<>(currentPoints));
            parallelogramLabels.add(new ArrayList<>(pointLabels));
        } else {
            JOptionPane.showMessageDialog(null, "The points do not form a parallelogram.");
        }
        currentPoints.clear();
        pointLabels.clear();
        labelChar = 'A';
    }

    public void clearPoints() {
        currentPoints.clear();
        parallelograms.clear();
        pointLabels.clear();
        parallelogramLabels.clear();
        labelChar = 'A';
    }

    private void reorderPoints() {
        if (currentPoints.size() < 3) return;

        Point base = currentPoints.stream()
                .min(Comparator.comparingInt((Point p) -> p.x).thenComparingInt(p -> p.y))
                .orElseThrow();

        currentPoints.sort(Comparator.comparingDouble(p -> Math.atan2(p.y - base.y, p.x - base.x)));
    }

    private void addFourthPoint() {
        if (currentPoints.size() == 3) {
            Point p1 = currentPoints.get(0);
            Point p2 = currentPoints.get(1);
            Point p3 = currentPoints.get(2);

            int x4 = p1.x + (p3.x - p2.x);
            int y4 = p1.y + (p3.y - p2.y);
            Point p4 = new Point(x4, y4);

            currentPoints.add(p4);
            pointLabels.add(String.valueOf(labelChar));
            labelChar++;
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
