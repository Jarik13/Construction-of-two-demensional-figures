import panels.CartesianCoordinatePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Parallelograms in Java!");
        initializeUI(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private static void initializeUI(JFrame frame) {
        CartesianCoordinatePanel mainPanel = new CartesianCoordinatePanel();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Point", "X", "Y"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable pointTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(pointTable);

        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel xLabel = new JLabel("X:");
        JTextField xField = new JTextField(5);
        JLabel yLabel = new JLabel("Y:");
        JTextField yField = new JTextField(5);
        JButton addButton = new JButton("Add Point");
        JButton removeButton = new JButton("Remove Point");
        JButton createParallelogramButton = new JButton("Create Parallelogram");

        inputPanel.add(xLabel);
        inputPanel.add(xField);
        inputPanel.add(yLabel);
        inputPanel.add(yField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(createParallelogramButton);

        addButton.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                mainPanel.addPoint(x, y);

                String pointLabel = String.valueOf((char) ('A' + mainPanel.getCurrentPoints().size() - 1));
                tableModel.addRow(new Object[]{pointLabel, x, y});

                xField.setText("");
                yField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid integers for X and Y.");
            }
        });

        removeButton.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                mainPanel.removePoint(x, y);

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 1).equals(x) && tableModel.getValueAt(i, 2).equals(y)) {
                        tableModel.removeRow(i);
                        break;
                    }
                }

                xField.setText("");
                yField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid integers for X and Y.");
            }
        });

        createParallelogramButton.addActionListener(e -> {
            if (mainPanel.getCurrentPoints().size() >= 4) {
                mainPanel.addParallelogram();
            } else {
                JOptionPane.showMessageDialog(frame, "Not enough points for the parallelogram (4 points)");
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(contentPanel, BorderLayout.WEST);
        frame.add(inputPanel, BorderLayout.NORTH);
    }
}
