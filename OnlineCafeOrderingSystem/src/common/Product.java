package common;

/**
 * Stores product details and a stock snapshot.
 */
public class Product {

    private final int id; // TODO: Agree on product IDs.
    private final String name;
    private final double price; // TODO: Confirm price type with Member 3.
    private final Category category;
    private final int stock;
    private final String imagePath; // TODO: Confirm image paths with Member 4.

    // TODO: Confirm constructor parameters with the team.
    public Product(int id, String name, double price,
                   Category category, int stock, String imagePath) {

        if (id <= 0) {
            throw new IllegalArgumentException("Product ID must be positive.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }

        if (Double.isNaN(price) || Double.isInfinite(price) || price < 0) {
            throw new IllegalArgumentException("Price must be finite and non-negative.");
        }

        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }

        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.imagePath = imagePath == null ? "" : imagePath.trim();
    }

    // TODO: Confirm getter names with Members 1, 3, and 4.
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public int getStock() {
        return stock;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    @Override
    public String toString() {
        return name;
    }
}