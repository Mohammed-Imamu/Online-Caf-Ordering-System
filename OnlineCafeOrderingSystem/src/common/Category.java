package common;

/**
 * Represents a cafe product category.
 */
public class Category {

    private final int id;      // TODO: Agree on IDs and type.
    private final String name; // TODO: Agree on category names.

    // TODO: Confirm constructor parameters with the team.
    public Category(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Category ID must be positive.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }

        this.id = id;
        this.name = name.trim();
    }

    // TODO: Confirm getter names with the team.
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // TODO: Confirm display format with Member 4.
    @Override
    public String toString() {
        return name;
    }
}