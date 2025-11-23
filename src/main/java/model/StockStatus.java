package model;

import util.ColorConstants;

/**
 * Enumeration representing the stock status of inventory items.
 * Each status has an associated display name and color scheme for UI rendering.
 *
 * Stock levels:
 * - IN_STOCK: Above low stock threshold
 * - LOW: At or below low stock threshold
 * - CRITICAL: At or below critical threshold (50% of low stock threshold)
 * - OUT_OF_STOCK: Quantity is zero
 *
 * @author Travis Dagostino
 * @version 1.0
 * @since 2025-11-22
 */
public enum StockStatus {
    /**
     * Sufficient stock available.
     */
    IN_STOCK("In Stock", ColorConstants.STOCK_IN_STOCK_BG, ColorConstants.STOCK_IN_STOCK_TEXT),

    /**
     * Stock is low and needs attention.
     */
    LOW("Low Stock", ColorConstants.STOCK_LOW_BG, ColorConstants.STOCK_LOW_TEXT),

    /**
     * Stock is critically low and requires immediate attention.
     */
    CRITICAL("Critical", ColorConstants.STOCK_CRITICAL_BG, ColorConstants.STOCK_CRITICAL_TEXT),

    /**
     * No stock available.
     */
    OUT_OF_STOCK("Out of Stock", ColorConstants.STOCK_CRITICAL_BG, ColorConstants.STOCK_CRITICAL_TEXT);

    private final String displayName;
    private final String backgroundColor;
    private final String textColor;

    /**
     * Constructs a StockStatus with the specified display properties.
     *
     * @param displayName The user-friendly name to display
     * @param backgroundColor The background color for the status badge
     * @param textColor The text color for the status badge
     */
    StockStatus(String displayName, String backgroundColor, String textColor) {
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    /**
     * Gets the display name of the stock status.
     *
     * @return The user-friendly status name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the background color for this status.
     *
     * @return The hex color code for the background
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Gets the text color for this status.
     *
     * @return The hex color code for the text
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     * Determines the stock status based on current quantity and thresholds.
     *
     * @param quantity The current stock quantity
     * @param lowStockThreshold The threshold for low stock alerts
     * @return The appropriate StockStatus based on the quantity
     */
    public static StockStatus fromQuantity(int quantity, int lowStockThreshold) {
        if (quantity == 0) {
            return OUT_OF_STOCK;
        } else if (quantity <= lowStockThreshold / 2) {
            return CRITICAL;
        } else if (quantity <= lowStockThreshold) {
            return LOW;
        } else {
            return IN_STOCK;
        }
    }
}
