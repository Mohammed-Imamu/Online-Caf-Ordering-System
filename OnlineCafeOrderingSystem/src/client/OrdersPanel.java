package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OrdersPanel extends JPanel {
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public OrdersPanel(ClientGUI parent) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("My Orders", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Date", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        ordersTable = new JTable(tableModel);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loadDummyBtn = new JButton("Load Dummy Orders");
        JButton cancelBtn = new JButton("Cancel Selected Order");

        buttonPanel.add(loadDummyBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadDummyBtn.addActionListener(e -> {
            tableModel.setRowCount(0); // مسح الجدول
            tableModel.addRow(new Object[]{"1001", "2023-11-20", "$15.00", "CONFIRMED"});
            tableModel.addRow(new Object[]{"1002", "2023-11-21", "$8.50", "PENDING"});
        });

        cancelBtn.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.setValueAt("CANCELLED", selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Order Cancelled Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select an order to cancel.");
            }
        });
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Orders Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.add(new OrdersPanel(null));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}