/*
 * ClientGUI.java
 * --------------
 * Purpose: Main window of the client application. Uses a CardLayout
 *          to switch between the panels (Login -> Main tabs -> ...).
 *
 * MVC role: This is the "Controller" that ties together:
 *   - Client (network layer)
 *   - LoginPanel, ProductsPanel, CartPanel, OrdersPanel (views)
 *
 * It implements Client.MessageListener so every message received from
 * the server is dispatched to the appropriate panel based on the
 * message prefix.
 */
package client;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame implements Client.MessageListener {

    private final Client client = new Client();

    // ---- CardLayout for top-level screens ----
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    // ---- Panels ----
    private LoginPanel loginPanel;
    private ProductsPanel productsPanel;
    private JTabbedPane mainTabs;
    private CartPanel cartPanel;       // Member 5
    private OrdersPanel ordersPanel;   // Member 5

    private String username = "";

    public ClientGUI() {
        super("Online Café Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        client.setMessageListener(this);

        loginPanel = new LoginPanel(client, this);
        cardPanel.add(loginPanel, "login");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "login");
    }

    /**
     * Called by LoginPanel after a successful TCP connection and
     * after sending CONNECT|username.
     */
    public void onLoginSuccess(String username) {
        this.username = username;

        productsPanel = new ProductsPanel(client);

        // CartPanel and OrdersPanel are written by Member 5.
        // We assume their constructors take a Client reference.
        cartPanel   = new CartPanel(client);
        ordersPanel = new OrdersPanel(client);

        mainTabs = new JTabbedPane();
        mainTabs.addTab("Products", productsPanel);
        mainTabs.addTab("Cart",     cartPanel);
        mainTabs.addTab("Orders",   ordersPanel);

        // Enable/disable add-to-cart controls based on selected row.
        productsPanel.addTableSelectionListener(
                () -> productsPanel.refreshStockControls());

        cardPanel.add(mainTabs, "main");
        cardLayout.show(cardPanel, "main");

        setTitle("Online Café Ordering System - " + username);
    }

    /**
     * Dispatch incoming server messages to the right panel.
     * The message format is: PREFIX|payload
     */
    @Override
    public void onMessageReceived(String message) {
        // Update Swing components on the EDT
        SwingUtilities.invokeLater(() -> dispatch(message));
    }

    private void dispatch(String message) {
        String[] parts = message.split("\\|", 2);
        String prefix = parts[0];
        String payload = parts.length > 1 ? parts[1] : "";

        switch (prefix) {
            case "OK":
                JOptionPane.showMessageDialog(this, payload,
                        "Server", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "ERROR":
                JOptionPane.showMessageDialog(this, payload,
                        "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "CATEGORIES":
                if (productsPanel != null) productsPanel.populateCategories(payload);
                break;
            case "PRODUCTS":
                if (productsPanel != null) productsPanel.populateProducts(payload);
                break;
            case "CART":
                if (cartPanel != null) cartPanel.updateCart(payload);
                break;
            case "ORDERS":
                if (ordersPanel != null) ordersPanel.updateOrders(payload);
                break;
            default:
                System.out.println("Unknown message: " + message);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
    }
}