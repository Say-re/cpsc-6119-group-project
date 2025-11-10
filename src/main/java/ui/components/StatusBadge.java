package ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import model.OrderStatus;

/**
 * Colored status badge component for displaying order status
 */
public class StatusBadge extends Label {

    public StatusBadge(OrderStatus status) {
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
        getStyleClass().add("status-badge");
    }

    /**
     * Create badge for stock status
     */
    public static StatusBadge forStockStatus(String status, int currentStock, int minStock) {
        StatusBadge badge = new StatusBadge(determineStockStatus(currentStock, minStock));
        return badge;
    }

    /**
     * Determine stock status based on levels
     */
    private static OrderStatus determineStockStatus(int currentStock, int minStock) {
        if (currentStock <= 0) {
            return OrderStatus.CANCELLED; // Red for out of stock
        } else if (currentStock < minStock * 0.5) {
            return OrderStatus.PLACED; // Yellow for critical/low
        } else {
            return OrderStatus.DELIVERED; // Green for in stock
        }
    }
}
