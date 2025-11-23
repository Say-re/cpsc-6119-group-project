package ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import model.StockStatus;

/**
 * Colored status badge component for displaying stock status.
 * Displays the stock level with appropriate color coding (green, yellow, or red).
 *
 * @author Travis Dagostino
 * @version 1.0
 * @since 2025-11-22
 */
public class StockStatusBadge extends Label {

    /**
     * Constructs a stock status badge with the specified status.
     *
     * @param status The StockStatus to display
     */
    public StockStatusBadge(StockStatus status) {
        setText(status.getDisplayName());

        // Apply styling based on status
        setStyle(
            "-fx-background-color: " + status.getBackgroundColor() + "; " +
            "-fx-text-fill: " + status.getTextColor() + "; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 4px 12px; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold;"
        );

        setPadding(new Insets(4, 12, 4, 12));
        getStyleClass().add("stock-status-badge");
    }

    /**
     * Creates a stock status badge based on quantity and threshold.
     *
     * @param quantity The current stock quantity
     * @param lowStockThreshold The threshold for low stock alerts
     * @return A StockStatusBadge with appropriate status
     */
    public static StockStatusBadge fromQuantity(int quantity, int lowStockThreshold) {
        StockStatus status = StockStatus.fromQuantity(quantity, lowStockThreshold);
        return new StockStatusBadge(status);
    }
}
