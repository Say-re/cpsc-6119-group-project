package service;

import model.InventoryItem;
import patterns.observer.InventorySubject;
import repo.InventoryDataManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for managing inventory operations.
 * Integrates with InventoryDataManager for persistence and InventorySubject for low stock notifications.
 * Uses the Observer pattern to notify when stock levels are low.
 *
 * @version 1.0
 * @since 2025-11-22
 */
public class InventoryService {
    private final InventorySubject subject;
    private final InventoryDataManager dataManager;
    private final Map<String,Integer> stock = new HashMap<>();
    private int lowStockThreshold = 10; // Default threshold

    /**
     * Constructs an InventoryService with the specified observer subject.
     *
     * @param subject The InventorySubject for low stock notifications
     */
    public InventoryService(InventorySubject subject) {
        this.subject = subject;
        this.dataManager = new InventoryDataManager();
        loadInventoryFromCsv();
    }

    /**
     * Loads inventory data from CSV into memory.
     * Initializes stock levels for all items.
     */
    private void loadInventoryFromCsv() {
        List<InventoryItem> items = dataManager.getAllItems();
        for (InventoryItem item : items) {
            stock.put(item.getName(), item.getQuantity());
        }
    }

    /**
     * Sets the low stock threshold for notifications.
     *
     * @param threshold The quantity threshold for low stock alerts
     */
    public void setLowStockThreshold(int threshold) {
        this.lowStockThreshold = threshold;
    }

    /**
     * Gets the current low stock threshold.
     *
     * @return The low stock threshold value
     */
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    /**
     * Sets the stock quantity for a product and notifies observers if low.
     * Updates both in-memory stock and persists to CSV.
     *
     * @param productName The name of the product
     * @param qty The new quantity
     */
    public void setStock(String productName, int qty) {
        stock.put(productName, qty);
        if (qty <= lowStockThreshold) {
            subject.notifyLowStock(productName, qty);
        }

        // Persist to CSV
        try {
            dataManager.getItem(productName).ifPresent(item -> {
                item.setQuantity(qty);
                try {
                    dataManager.saveItem(item);
                } catch (IOException e) {
                    System.err.println("Error updating stock in CSV: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error setting stock: " + e.getMessage());
        }
    }

    /**
     * Decrements the stock quantity for a product.
     * Ensures quantity does not go below zero.
     *
     * @param productName The name of the product
     * @param by The amount to decrement by
     */
    public void decrement(String productName, int by) {
        int newQty = stock.getOrDefault(productName, 0) - by;
        setStock(productName, Math.max(0, newQty));
    }

    /**
     * Gets the current stock quantity for a product.
     *
     * @param productName The name of the product
     * @return The current stock quantity, or 0 if not found
     */
    public int getStock(String productName) {
        return stock.getOrDefault(productName, 0);
    }

    /**
     * Gets all inventory items from the data manager.
     *
     * @return List of all InventoryItem objects
     */
    public List<InventoryItem> getAllItems() {
        return dataManager.getAllItems();
    }

    /**
     * Gets items that are at or below the low stock threshold.
     *
     * @return List of InventoryItem objects with low stock
     */
    public List<InventoryItem> getLowStockItems() {
        return dataManager.getLowStockItems(lowStockThreshold);
    }

    /**
     * Calculates the total value of all inventory.
     *
     * @return The total monetary value of inventory
     */
    public double getTotalInventoryValue() {
        return dataManager.getTotalInventoryValue();
    }

    /**
     * Gets the count of items with low stock.
     *
     * @return Number of items at or below low stock threshold
     */
    public int getLowStockItemCount() {
        return getLowStockItems().size();
    }

    /**
     * Updates an inventory item's details and persists to CSV.
     *
     * @param item The InventoryItem to update
     * @throws IOException If there is an error writing to CSV
     */
    public void updateItem(InventoryItem item) throws IOException {
        dataManager.saveItem(item);
        stock.put(item.getName(), item.getQuantity());
        if (item.getQuantity() <= lowStockThreshold) {
            subject.notifyLowStock(item.getName(), item.getQuantity());
        }
    }

    /**
     * Adds a new inventory item and persists to CSV.
     *
     * @param item The new InventoryItem to add
     * @throws IOException If there is an error writing to CSV
     */
    public void addItem(InventoryItem item) throws IOException {
        dataManager.saveItem(item);
        stock.put(item.getName(), item.getQuantity());
    }

    /**
     * Deletes an inventory item by name.
     *
     * @param productName The name of the product to delete
     * @throws IOException If there is an error writing to CSV
     */
    public void deleteItem(String productName) throws IOException {
        dataManager.deleteItem(productName);
        stock.remove(productName);
    }

    /**
     * Reloads inventory data from CSV.
     * Useful after external changes to the CSV file.
     */
    public void refresh() {
        stock.clear();
        loadInventoryFromCsv();
    }
}
