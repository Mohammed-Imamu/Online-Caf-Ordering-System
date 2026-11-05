/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author user
 */
public class Order {//
    private int orderId;
    private String username;
    private LocalDateTime time;//استخدمت هذي المكتبه عشان تجيب لك الوقت اللي طلب فيه العميل
    private Map<Integer, Integer> items;//نخزنها كا هاش ماب اسرع(Key (Product ID),Value (Quantity))
    private double totalAmount;
    private String status;
    
    public Order(int orderId, String username, Map<Integer, Integer> items, double totalAmount){
    this.orderId = orderId;
    this.username = username;
    this.time = LocalDateTime.now();
    this.items = new HashMap<>(items);
    this.totalAmount = totalAmount;
    this.status = "CONFIRMED";
}

    /**
     * @return the orderId
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the userName
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.username = userName;
    }

    /**
     * @return the time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /**
     * @return the items
     */
    public Map<Integer, Integer> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(Map<Integer, Integer> items) {
        this.items = items;
    }

    /**
     * @return the totalAmount
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Order ID: " + orderId
            + "\nUsername: " + username
            + "\nTime: " + time
            + "\nItems: " + items
            + "\nTotal: " + totalAmount
            + "\nStatus: " + status;
}
    
    
    
    
}
