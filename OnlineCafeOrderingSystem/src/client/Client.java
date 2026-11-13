/*
 * Client.java
 * ------------
 * Purpose: Handles the network connection between the GUI client and the
 *          server using TCP sockets (Socket, BufferedReader, PrintWriter).
 *
 * Responsibilities:
 *  - Connect / disconnect from the server.
 *  - Send requests (commands) to the server.
 *  - Receive responses asynchronously on a background thread so the GUI
 *    never freezes.
 *  - Notify a listener (ClientGUI) whenever a message arrives.
 *
 * Protocol (text-based, one line per message):
 *    COMMAND|arg1|arg2|...
 * Responses:
 *    OK|message
 *    ERROR|message
 *    CATEGORIES|id,name;id,name;...
 *    PRODUCTS|id,name,price,stock,categoryId;...

 أستخدمت البروتوكول عشان التنسيق
PREFIX|payload 
الشي ذا بيفيد العضو رقم 1 و 3 
 بيفيد عضو 1 بخصوص 
ClientHandler.java 
و عضو 3 بخصوص 
Message.java
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

 
    public interface MessageListener {
        void onMessageReceived(String message);
    }

    private MessageListener listener;
    private volatile boolean connected = false;

    
    private Thread readerThread;

    public Client() {
    }

    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    
    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            startReaderThread();
            return true;
        } catch (IOException e) {
            connected = false;
            return false;
        }
    }

    
    private void startReaderThread() {
        readerThread = new Thread(() -> {
            try {
                String line;
                while (connected && (line = in.readLine()) != null) {
                    if (listener != null) {
                        listener.onMessageReceived(line);
                    }
                }
            } catch (IOException e) {
                
            } finally {
                connected = false;
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    
    public void send(String message) {
        if (out != null && connected) {
            out.println(message);
        }
    }


    public void sendConnect(String username) {
        send("CONNECT|" + username);
    }

    public void requestCategories() {
        send("GET_CATEGORIES");
    }

    public void requestProducts(int categoryId) {
        send("GET_PRODUCTS|" + categoryId);
    }

    public void addToCart(int productId, int qty) {
        send("ADD_TO_CART|" + productId + "|" + qty);
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        connected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
           
        }
    }
}