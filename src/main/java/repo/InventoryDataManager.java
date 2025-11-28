package repo;

import model.InventoryItem;
import util.CsvUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Data manager for inventory operations.
 * Handles reading/writing to inventory.csv file.
 *
 * @author Travis Dagostino
 * @version 1.0
 * @since 2025-11-22
 */
public class InventoryDataManager {
    private static final String INVENTORY_FILE = "inventory.csv";

    /**
     * Retrieves all inventory items from the CSV file.
     * Skips the header row and parses each data row into an InventoryItem object.
     *
     * @return List of all InventoryItem objects in the inventory
     */
    public List<InventoryItem> getAllItems() {
        List<InventoryItem> items = new ArrayList<>();
        try {
            List<String[]> rows = CsvUtil.read(INVENTORY_FILE);
            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] r = rows.get(i);
                if (r.length >= 4 && !r[0].trim().isEmpty()) {
                    String name = r[0].trim();
                    String type = r[1].trim();
                    double price = Double.parseDouble(r[2].trim());
                    int quantity = Integer.parseInt(r[3].trim());
                    items.add(new InventoryItem(name, type, price, quantity));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading inventory: " + e.getMessage());
        }
        return items;
    }

    /**
     * Retrieves a specific inventory item by its name.
     * Search is case-insensitive.
     *
     * @param name The name of the inventory item to find
     * @return Optional containing the InventoryItem if found, empty otherwise
     */
    public Optional<InventoryItem> getItem(String name) {
        return getAllItems().stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Saves an inventory item to the CSV file.
     * If an item with the same name exists, it will be updated.
     * If the item is new, it will be added.
     *
     * @param item The InventoryItem to save or update
     */
    public void saveItem(InventoryItem item) throws IOException {
        List<String[]> rows = CsvUtil.read(INVENTORY_FILE);

        // Keep header if it exists, otherwise create one
        List<String[]> updatedRows = new ArrayList<>();
        if (rows.isEmpty() || !rows.get(0)[0].equals("name")) {
            updatedRows.add(new String[]{"name", "type", "price", "quantity"});
        } else {
            updatedRows.add(rows.get(0)); // Keep header
        }

        // Update existing or add new
        boolean found = false;
        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r.length >= 4 && r[0].trim().equalsIgnoreCase(item.getName())) {
                // Update existing item
                updatedRows.add(new String[]{
                        item.getName(),
                        item.getType(),
                        String.format(Locale.US, "%.2f", item.getPrice()),
                        String.valueOf(item.getQuantity())
                });
                found = true;
            } else if (r.length >= 4 && !r[0].trim().isEmpty()) {
                // Keep other items as-is
                updatedRows.add(r);
            }
        }

        // If not found, add as new item
        if (!found) {
            updatedRows.add(new String[]{
                    item.getName(),
                    item.getType(),
                    String.format(Locale.US, "%.2f", item.getPrice()),
                    String.valueOf(item.getQuantity())
            });
        }

        CsvUtil.write(INVENTORY_FILE, updatedRows);
    }

    /**
     * Deletes an inventory item by name from the CSV file.
     *
     * @param name The name of the item to delete (case-insensitive)
     */
    public void deleteItem(String name) throws IOException {
        List<String[]> rows = CsvUtil.read(INVENTORY_FILE);
        List<String[]> updatedRows = new ArrayList<>();

        // Keep header
        if (!rows.isEmpty()) {
            updatedRows.add(rows.get(0));
        }

        // Keep all items except the one to delete
        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r.length >= 4 && !r[0].trim().equalsIgnoreCase(name)) {
                updatedRows.add(r);
            }
        }

        CsvUtil.write(INVENTORY_FILE, updatedRows);
    }

    /**
     * Retrieves all inventory items of a specific type.
     *
     * @param type The candy type to filter by (e.g., "chocolate", "gummy", "hard")
     * @return List of InventoryItem objects matching the specified type
     */
    public List<InventoryItem> getItemsByType(String type) {
        List<InventoryItem> items = new ArrayList<>();
        for (InventoryItem item : getAllItems()) {
            if (item.getType().equalsIgnoreCase(type)) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Retrieves all items that are at or below the specified stock threshold.
     *
     * @param threshold The quantity threshold for low stock items
     * @return List of InventoryItem objects with quantity at or below threshold
     */
    public List<InventoryItem> getLowStockItems(int threshold) {
        List<InventoryItem> items = new ArrayList<>();
        for (InventoryItem item : getAllItems()) {
            if (item.getQuantity() <= threshold) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Calculates the total value of all inventory items.
     * Total value is the sum of (price * quantity) for each item.
     *
     * @return The total monetary value of the entire inventory
     */
    public double getTotalInventoryValue() {
        double total = 0.0;
        for (InventoryItem item : getAllItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}
