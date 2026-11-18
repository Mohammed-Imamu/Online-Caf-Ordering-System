package client;

import common.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CartPanel extends JPanel {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private Map<Product, Integer> cartItems;

    public CartPanel(ClientGUI parent) {
        this.cartItems = new HashMap<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Shopping Cart", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Product Name", "Price", "Quantity", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        cartTable = new JTable(tableModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel buttonPanel = new JPanel();
        JButton testAddBtn = new JButton("Add Dummy Item (For Testing)");
        JButton removeBtn = new JButton("Remove Item");
        JButton placeOrderBtn = new JButton("Place Order");

        buttonPanel.add(testAddBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(placeOrderBtn);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

     
        testAddBtn.addActionListener(e -> {
            // إضافة منتج وهمي للتجربة
            Product dummy = new Product(1, "Coffee", 5.0, 10, "Drinks");
            addToCart(dummy, 1);
        });
        
        removeBtn.addActionListener(e -> removeItem());
        
        placeOrderBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Order Placed! (Integration handed to team)");
        });
    }

    public void addToCart(Product product, int quantity) {
        if (cartItems.containsKey(product)) {
            cartItems.put(product, cartItems.get(product) + quantity);
        } else {
            cartItems.put(product, quantity);
        }
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0); 
        double total = 0;
        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            double subtotal = p.getPrice() * qty;
            total += subtotal;
            tableModel.addRow(new Object[]{p.getName(), p.getPrice(), qty, subtotal});
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void removeItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            String productName = (String) tableModel.getValueAt(selectedRow, 0);
            Product toRemove = null;
            for (Product p : cartItems.keySet()) {
                if (p.getName().equals(productName)) {
                    toRemove = p;
                    break;
                }
            }
            if (toRemove != null) {
                cartItems.remove(toRemove);
                updateTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
        }
    }

   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cart Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.add(new CartPanel(null));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}