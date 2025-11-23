package ui.components.factory;

import javafx.scene.control.Label;
import model.OrderStatus;
import ui.components.StatusBadge;

/**
 * Concrete factory for creating order status badges.
 * Implements the Factory Method Pattern for OrderStatus badge creation.
 *
 * This factory is responsible for creating StatusBadge components
 * that display order status information (pending, processing, completed, etc.).
 *
 * @author Travis Dagostino
 * @since 2025-11-22
 */
public class OrderStatusBadgeFactory extends BadgeFactory {

    /**
     * Singleton instance of the factory.
     */
    private static OrderStatusBadgeFactory instance;

    /**
     * Private constructor to implement Singleton pattern.
     * Prevents direct instantiation.
     */
    private OrderStatusBadgeFactory() {
        // Singleton pattern - private constructor
    }

    /**
     * Gets the singleton instance of OrderStatusBadgeFactory.
     *
     * @return The singleton factory instance
     */
    public static OrderStatusBadgeFactory getInstance() {
        if (instance == null) {
            instance = new OrderStatusBadgeFactory();
        }
        return instance;
    }

    /**
     * Factory method to create an order status badge.
     *
     * @param status The status object (must be an OrderStatus instance)
     * @return StatusBadge component displaying the order status
     */
    @Override
    public Label createBadge(Object status) {
        validateStatus(status, "OrderStatus");

        if (!(status instanceof OrderStatus)) {
            throw new IllegalArgumentException(
                "Expected OrderStatus but got " + status.getClass().getName()
            );
        }

        return new StatusBadge((OrderStatus) status);
    }

    /**
     * Method for creating order status badges.
     *
     * @param status The OrderStatus to create a badge for
     * @return StatusBadge component displaying the order status
     */
    public StatusBadge createOrderStatusBadge(OrderStatus status) {
        validateStatus(status, "OrderStatus");
        return new StatusBadge(status);
    }

    /**
     * Creates an order status badge from a status name string.
     *
     * @param statusName The name of the order status (e.g., "PLACED", "DELIVERED")
     * @return StatusBadge component for the specified status
     */
    public StatusBadge createBadgeFromName(String statusName) {
        if (statusName == null || statusName.trim().isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be null or empty");
        }

        try {
            OrderStatus status = OrderStatus.valueOf(statusName.toUpperCase().trim());
            return createOrderStatusBadge(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status name: " + statusName, e);
        }
    }
}
