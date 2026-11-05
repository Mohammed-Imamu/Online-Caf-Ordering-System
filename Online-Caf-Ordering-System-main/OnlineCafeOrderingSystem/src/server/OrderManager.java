
package server;

import common.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderManager {

    private ArrayList<Order> orders;
    private AtomicInteger nextOrderId;//استخدمنا هذا الكلاس يعطي رقم الطلب بشكل منظم حتى لو جاء طلبين في نفس الوقت
    private InventoryManager inventoryManager;

    
    public OrderManager(InventoryManager inventoryManager) {

        this.orders = new ArrayList<>();
        this.nextOrderId = new AtomicInteger(1);
        this.inventoryManager = inventoryManager;
    }

    
    public synchronized Order createOrder(String username, Map<Integer, Integer> items,double totalAmount) {
        if (username == null  || items == null || totalAmount < 0) {
            return null;
        }

        //hasStock(productId, quantity):تتحقق هل الكمية المطلوبة متوفرة
        //hasStock(productId, quantity) :ميثود مفروض موجوده في كلاسinventoryManager
        
        for (Map.Entry<Integer, Integer> entry : items.entrySet()){
            //Entry :تسمح لنا نجيب (Key,Value)
            int productId = entry.getKey();
            int quantity = entry.getValue();

            if (quantity <= 0 || !inventoryManager.hasStock(productId, quantity)) {

                return null;
            }
        }

        //reduceStock : ميثود موجوده فيه inventoryManager
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {

            inventoryManager.reduceStock(entry.getKey(), entry.getValue());
        }

        
        Order order = new Order(nextOrderId.getAndIncrement(), username, items,totalAmount);

        orders.add(order);

        return order;
    }

    
    public synchronized boolean confirmOrder(int orderId) {

        Order order = findOrder(orderId);

        return order != null&& "CONFIRMED".equals(order.getStatus());
    }

    
    public synchronized boolean cancelOrder(String username, int orderId) {

        Order order = findOrder(orderId);

        if (order == null || !order.getUsername().equals(username)|| !"CONFIRMED".equals(order.getStatus())) {

            return false;
        }
        //increaseStock : ميثود موجوده فيه inventoryManager
        
        for (Map.Entry<Integer, Integer> entry : order.getItems().entrySet()) {

            inventoryManager.increaseStock( entry.getKey(), entry.getValue());}

        order.setStatus("CANCELLED");

        return true;
    }

    
    public synchronized Order findOrder(int orderId) {

        for (Order order : orders) {

            if (order.getOrderId() == orderId) {
                return order;
            }
        }

        return null;
    }

    
    public synchronized List<Order> getUserOrders(String username) {

        List<Order> userOrders = new ArrayList<>();

        for (Order order : orders) {

            if (order.getUsername().equalsIgnoreCase(username)) {
                userOrders.add(order);
            }
        }

        return userOrders;
    }

    
    public synchronized List<Order> getAllOrders() {

        return new ArrayList<>(orders);
    }

}

