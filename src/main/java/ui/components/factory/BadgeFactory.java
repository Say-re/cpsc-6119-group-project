package ui.components.factory;

import javafx.scene.control.Label;

/**
 * Abstract factory class for creating status badge components.
 * Implements the Factory Method Design Pattern.
 *
 * This abstract class defines the contract for badge creation.
 * Concrete subclasses implement specific badge creation logic for different types
 * (OrderStatus, StockStatus, etc.).
 *
 * @author Travis Dagostino
 * @since 2025-11-22
 */
public abstract class BadgeFactory {

    /**
     * Factory method to create a badge component.
     * Concrete subclasses implement this method to create specific badge types.
     *
     * @param status The status object (type depends on concrete factory implementation)
     * @return Label component representing the status badge
     */
    public abstract Label createBadge(Object status);

    /**
     * Validates that the status object is not null.
     *
     * @param status The status object to validate
     * @param typeName The expected type name for error messages
     */
    protected void validateStatus(Object status, String typeName) {
        if (status == null) {
            throw new IllegalArgumentException(typeName + " cannot be null");
        }
    }

    /**
     * Template method for creating a badge with validation.
     * Validates the status before delegating to the factory method.
     *
     * @param status The status object
     * @param typeName The expected type name for validation
     * @return Label component representing the status badge
     */
    protected Label createValidatedBadge(Object status, String typeName) {
        validateStatus(status, typeName);
        return createBadge(status);
    }
}
