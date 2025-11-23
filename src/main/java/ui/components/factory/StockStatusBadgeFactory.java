package ui.components.factory;

import javafx.scene.control.Label;
import model.StockStatus;
import ui.components.StockStatusBadge;

/**
 * Concrete factory for creating stock status badges.
 * Implements the Factory Method Pattern for StockStatus badge creation.
 *
 * This factory is responsible for creating StockStatusBadge components
 * that display inventory stock levels (in stock, low, critical, out of stock).
 *
 * @author Travis Dagostino
 * @since 2025-11-22
 */
public class StockStatusBadgeFactory extends BadgeFactory {

    /**
     * Singleton instance of the factory.
     */
    private static StockStatusBadgeFactory instance;

    /**
     * Default low stock threshold used when creating badges from quantity.
     */
    private int defaultLowStockThreshold = 10;

    /**
     * Private constructor to implement Singleton pattern.
     * Prevents direct instantiation.
     */
    private StockStatusBadgeFactory() {
        // Singleton pattern - private constructor
    }

    /**
     * Gets the singleton instance of StockStatusBadgeFactory.
     *
     * @return The singleton factory instance
     */
    public static StockStatusBadgeFactory getInstance() {
        if (instance == null) {
            instance = new StockStatusBadgeFactory();
        }
        return instance;
    }

    /**
     * Sets the default low stock threshold for this factory.
     *
     * @param threshold The threshold value for low stock alerts
     */
    public void setDefaultLowStockThreshold(int threshold) {
        this.defaultLowStockThreshold = threshold;
    }

    /**
     * Gets the current default low stock threshold.
     *
     * @return The default low stock threshold value
     */
    public int getDefaultLowStockThreshold() {
        return defaultLowStockThreshold;
    }

    /**
     * Factory method to create a stock status badge.
     * Implements the abstract method from BadgeFactory.
     *
     * @param status The status object (must be a StockStatus instance)
     * @return StockStatusBadge component displaying the stock status
     */
    @Override
    public Label createBadge(Object status) {
        validateStatus(status, "StockStatus");

        if (!(status instanceof StockStatus)) {
            throw new IllegalArgumentException(
                "Expected StockStatus but got " + status.getClass().getName()
            );
        }

        return new StockStatusBadge((StockStatus) status);
    }

    /**
     * Method for creating stock status badges.
     *
     * @param status The StockStatus to create a badge for
     * @return StockStatusBadge component displaying the stock status
     */
    public StockStatusBadge createStockStatusBadge(StockStatus status) {
        validateStatus(status, "StockStatus");
        return new StockStatusBadge(status);
    }

    /**
     * Creates a stock status badge based on quantity and threshold.
     * Automatically determines the appropriate StockStatus based on the quantity.
     *
     * @param quantity The current stock quantity
     * @param lowStockThreshold The threshold for low stock alerts
     * @return StockStatusBadge component with the appropriate status
     */
    public StockStatusBadge createBadgeFromQuantity(int quantity, int lowStockThreshold) {
        StockStatus status = StockStatus.fromQuantity(quantity, lowStockThreshold);
        return new StockStatusBadge(status);
    }

    /**
     * Creates a stock status badge based on quantity using the default threshold.
     * Automatically determines the appropriate StockStatus based on the quantity.
     *
     * @param quantity The current stock quantity
     * @return StockStatusBadge component with the appropriate status
     */
    public StockStatusBadge createBadgeFromQuantity(int quantity) {
        return createBadgeFromQuantity(quantity, defaultLowStockThreshold);
    }

    /**
     * Creates a stock status badge from a status name string.
     *
     * @param statusName The name of the stock status (e.g., "IN_STOCK", "LOW")
     * @return StockStatusBadge component for the specified status
     */
    public StockStatusBadge createBadgeFromName(String statusName) {
        if (statusName == null || statusName.trim().isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be null or empty");
        }

        try {
            StockStatus status = StockStatus.valueOf(statusName.toUpperCase().trim());
            return createStockStatusBadge(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid stock status name: " + statusName, e);
        }
    }
}
