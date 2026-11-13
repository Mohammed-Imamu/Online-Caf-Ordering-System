/*
 * ProductsPanel.java
 * ------------------
 * Purpose: GUI panel that lets the user:
 *  - Pick a category (JComboBox).
 *  - See the list of products of that category with name, price, stock.
 *  - Choose a quantity and click "Add to Cart".
 *
 * The panel parses two kinds of server messages:
 *   CATEGORIES|id,name;id,name;...
 *   PRODUCTS|id,name,price,stock,categoryId;...
 *
 * When a product is out of stock (stock == 0), the quantity spinner
 * and the "Add" button are disabled automatically.
 */
package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProductsPanel extends JPanel {

    // ---- UI components ----
    private final JComboBox<CategoryItem> categoryCombo = new JComboBox<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Price", "Stock"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable productsTable = new JTable(tableModel);
    private final JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JButton addBtn = new JButton("Add to Cart");
    private final JButton refreshBtn = new JButton("Refresh");
    private final JLabel statusLabel = new JLabel(" ");

    private final Client client;

    // ---- Simple inner class for combo box items ----
    private static class CategoryItem {
        final int id;
        final String name;
        CategoryItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    public ProductsPanel(Client client) {
        this.client = client;
        buildUI();
        wireEvents();
        client.requestCategories();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Top: category selector ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Category:"));
        top.add(categoryCombo);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        // ---- Center: products table ----
        add(new JScrollPane(productsTable), BorderLayout.CENTER);

        // ---- Bottom: qty + add button ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Quantity:"));
        bottom.add(qtySpinner);
        bottom.add(addBtn);
        bottom.add(statusLabel);
        add(bottom, BorderLayout.SOUTH);
    }

    private void wireEvents() {
        categoryCombo.addActionListener(e -> {
            CategoryItem selected = (CategoryItem) categoryCombo.getSelectedItem();
            if (selected != null) {
                tableModel.setRowCount(0);
                client.requestProducts(selected.id);
            }
        });

        refreshBtn.addActionListener(e -> {
            categoryCombo.removeAllItems();
            tableModel.setRowCount(0);
            client.requestCategories();
        });

        addBtn.addActionListener(e -> addSelectedToCart());
    }

    /**
     * Called by ClientGUI when the server sends CATEGORIES|... message.
     */
    public void populateCategories(String payload) {
        categoryCombo.removeAllItems();
        for (String entry : payload.split(";")) {
            String[] parts = entry.split(",");
            if (parts.length == 2) {
                categoryCombo.addItem(new CategoryItem(
                        Integer.parseInt(parts[0]), parts[1]));
            }
        }
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
    }

    /**
     * Called by ClientGUI when the server sends PRODUCTS|... message.
     * Format: id,name,price,stock,categoryId;...
     */
    public void populateProducts(String payload) {
        tableModel.setRowCount(0);
        for (String entry : payload.split(";")) {
            if (entry.isEmpty()) continue;
            String[] p = entry.split(",");
            if (p.length < 5) continue;
            int id = Integer.parseInt(p[0]);
            String name = p[1];
            double price = Double.parseDouble(p[2]);
            int stock = Integer.parseInt(p[3]);
            tableModel.addRow(new Object[]{id, name, price, stock});
        }
        updateStockControls();
    }

    /**
     * Enable/disable the spinner and add button depending on the
     * currently selected row's stock.
     */
    private void updateStockControls() {
        int row = productsTable.getSelectedRow();
        if (row < 0) {
            addBtn.setEnabled(false);
            qtySpinner.setEnabled(false);
            return;
        }
        int stock = (int) tableModel.getValueAt(row, 3);
        boolean available = stock > 0;
        addBtn.setEnabled(available);
        qtySpinner.setEnabled(available);
        // Also cap the maximum quantity to the available stock
        if (available) {
            qtySpinner.setModel(new SpinnerNumberModel(1, 1, stock, 1));
        }
        statusLabel.setText(available ? " " : "Out of stock");
    }

    private void addSelectedToCart() {
        int row = productsTable.getSelectedRow();
        if (row < 0) {
            statusLabel.setText("Please select a product first.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int qty = (int) qtySpinner.getValue();
        client.addToCart(id, qty);
        statusLabel.setText("Added to cart: " + qty + " x " + tableModel.getValueAt(row, 1));
    }

    // Expose a selection listener so ClientGUI can update the
    // stock-dependent controls when the user clicks a row.
    public void addTableSelectionListener(Runnable r) {
        productsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) r.run();
        });
    }

    public void refreshStockControls() {
        updateStockControls();
    }
}