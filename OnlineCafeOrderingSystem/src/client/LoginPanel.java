/*
 * LoginPanel.java
 * ---------------
 * Purpose: GUI panel shown first when the client starts.
 *          Collects the server IP, port and username, then connects.
 * Validation:
 *  - IP/host must not be empty.
 *  - Port must be a number between 1 and 65535.
 *  - Username must not be empty and must be >= 3 characters.
 */
package client;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final JTextField hostField = new JTextField("127.0.0.1", 15);
    private final JTextField portField = new JTextField("5000", 6);
    private final JTextField userField = new JTextField(15);
    private final JButton connectBtn = new JButton("Connect");
    private final JLabel statusLabel = new JLabel(" ");

    private final Client client;
    private final ClientGUI parent;

    public LoginPanel(Client client, ClientGUI parent) {
        this.client = client;
        this.parent = parent;
        buildUI();
        wireEvents();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Online Café Ordering System");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Server IP:"), gbc);
        gbc.gridx = 1;                add(hostField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;                add(portField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;                add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(connectBtn, gbc);

        gbc.gridy = 5;
        statusLabel.setForeground(Color.RED);
        add(statusLabel, gbc);
    }

    private void wireEvents() {
        connectBtn.addActionListener(e -> doConnect());
        // Allow pressing Enter in the username field to connect
        userField.addActionListener(e -> doConnect());
    }

    private void doConnect() {
        String host = hostField.getText().trim();
        String portText = portField.getText().trim();
        String username = userField.getText().trim();

        // -------- Validation --------
        if (host.isEmpty()) {
            statusLabel.setText("Server IP cannot be empty.");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(portText);
            if (port < 1 || port > 65535) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            statusLabel.setText("Port must be a number between 1 and 65535.");
            return;
        }
        if (username.length() < 3) {
            statusLabel.setText("Username must be at least 3 characters.");
            return;
        }

        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText("Connecting...");

        if (client.connect(host, port)) {
            client.sendConnect(username);
            parent.onLoginSuccess(username);
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Connection failed. Is the server running?");
        }
    }
    }