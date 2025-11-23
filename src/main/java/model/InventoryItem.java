package model;

/**
 * Represents an inventory item in the Sweet Factory.
 * Maps to inventory.csv structure: name, type, price, quantity.
 *
 * This class stores information about candy products available in the inventory,
 * including their name, type (chocolate, gummy, hard), price, and current stock quantity.
 *
 * @author Travis Dagostino
 * @version 1.0
 * @since 2025-11-22
 */
public class InventoryItem {
    private String name;
    private String type;
    private double price;
    private int quantity;

    /**
     * Constructs a new InventoryItem with the specified details.
     *
     * @param name The name of the candy product
     * @param type The type of candy (e.g., "chocolate", "gummy", "hard")
     * @param price The price per unit of the product
     * @param quantity The current stock quantity
     */
    public InventoryItem(String name, String type, double price, int quantity) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Gets the name of the inventory item.
     *
     * @return The product name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of candy.
     *
     * @return The candy type (e.g., "chocolate", "gummy", "hard")
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the price per unit of the product.
     *
     * @return The unit price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the current stock quantity.
     *
     * @return The quantity in stock
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the name of the inventory item.
     *
     * @param name The new product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type of candy.
     *
     * @param type The new candy type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the price per unit of the product.
     *
     * @param price The new unit price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Sets the current stock quantity.
     *
     * @param quantity The new quantity in stock
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns a string representation of the inventory item.
     *
     * @return A formatted string with item details
     */
    @Override
    public String toString() {
        return String.format("InventoryItem{name='%s', type='%s', price=%.2f, quantity=%d}",
                name, type, price, quantity);
    }
}
