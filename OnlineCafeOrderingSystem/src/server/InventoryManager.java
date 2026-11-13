package server;

import common.Category;
import common.Product;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

 //  Manages products and protects stock updates from concurrent requests.

public class InventoryManager {

    private final Map<Integer, Product> products = new LinkedHashMap<>();

    public InventoryManager() {
        // TODO: Confirm category IDs and names with the team.
        Category coffee = new Category(1, "Coffee");
        Category coldDrinks = new Category(2, "Cold Drinks");
        Category desserts = new Category(3, "Desserts");

        // TODO: Confirm product IDs, prices, stock, and image paths.
        addProduct(101, "Espresso", 10.0, coffee, 20, "");
        addProduct(102, "Americano", 12.0, coffee, 20, "");
        addProduct(103, "Cappuccino", 16.0, coffee, 20, "");
        addProduct(104, "Spanish Latte", 20.0, coffee, 20, "");

        addProduct(201, "Iced Matcha", 22.0, coldDrinks, 20, "");
        addProduct(202, "Iced Tea", 14.0, coldDrinks, 20, "");
        addProduct(203, "Lemonade", 15.0, coldDrinks, 20, "");
        addProduct(204, "Milkshake", 20.0, coldDrinks, 20, "");

        addProduct(301, "Croissant", 12.0, desserts, 15, "");
        addProduct(302, "Brownie", 14.0, desserts, 15, "");
        addProduct(303, "Cheesecake", 22.0, desserts, 15, "");
        addProduct(304, "Cookies", 8.0, desserts, 15, "");
    }

    private void addProduct(int id, String name, double price,
                            Category category, int stock, String imagePath) {
        products.put(id,
                new Product(id, name, price, category, stock, imagePath));
    }

    // TODO: Confirm method names and return types with the team.
    public synchronized List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public synchronized Product getProductById(int productId) {
        return products.get(productId);
    }

    public synchronized List<Product> getProductsByCategory(int categoryId) {
        List<Product> result = new ArrayList<>();

        for (Product product : products.values()) {
            if (product.getCategory().getId() == categoryId) {
                result.add(product);
            }
        }

        return result;
    }

    /**
     * Checks the entire order before reducing any stock.
     * Returns false without changes if any item is invalid or unavailable.
     */
    // TODO: Member 3 passes product ID -> quantity and checks the result.
    public synchronized boolean purchaseProducts(Map<Integer, Integer> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        Map<Integer, Integer> requested = new LinkedHashMap<>(items);

        if (!isValidRequest(requested)) {
            return false;
        }

        for (Map.Entry<Integer, Integer> entry : requested.entrySet()) {
            Product product = products.get(entry.getKey());

            if (entry.getValue() > product.getStock()) {
                return false;
            }
        }

        for (Map.Entry<Integer, Integer> entry : requested.entrySet()) {
            Product product = products.get(entry.getKey());
            replaceStock(product, product.getStock() - entry.getValue());
        }

        return true;
    }

    // Restores all quantities from a successfully cancelled order.
    
    // TODO: Member 3 must verify ownership and prevent repeated cancellation.
    public synchronized boolean restoreStock(Map<Integer, Integer> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        Map<Integer, Integer> returned = new LinkedHashMap<>(items);

        if (!isValidRequest(returned)) {
            return false;
        }

        for (Map.Entry<Integer, Integer> entry : returned.entrySet()) {
            Product product = products.get(entry.getKey());

            if (entry.getValue() > Integer.MAX_VALUE - product.getStock()) {
                return false;
            }
        }

        for (Map.Entry<Integer, Integer> entry : returned.entrySet()) {
            Product product = products.get(entry.getKey());
            replaceStock(product, product.getStock() + entry.getValue());
        }

        return true;
    }

    private boolean isValidRequest(Map<Integer, Integer> items) {
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            Integer productId = entry.getKey();
            Integer quantity = entry.getValue();

            if (productId == null || quantity == null || quantity <= 0
                    || !products.containsKey(productId)) {
                return false;
            }
        }

        return true;
    }

    //  Replaces the immutable Product with an updated stock snapshot.
    
    private void replaceStock(Product product, int newStock) {
        products.put(product.getId(), new Product(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                newStock,
                product.getImagePath()
        ));
    }

    // TODO: Member 1 must share ONE InventoryManager across ClientHandlers.
    // TODO: Member 1 must broadcast updated stock after purchase or cancellation.
}