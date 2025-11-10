package model;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * View model combining Order with Customer information for display in admin dashboard
 */
public class OrderDisplayModel {
    private final Order order;
    private final String customerName;
    private final String customerEmail;

    public OrderDisplayModel(Order order, String customerName, String customerEmail) {
        this.order = order;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public Order getOrder() { return order; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }

    // Convenience methods for table display
    public String getOrderId() { return order.getId(); }
    public OrderStatus getStatus() { return order.getStatus(); }
    public double getGrandTotal() { return order.getGrandTotal(); }
    public Instant getCreatedAt() { return order.getCreatedAt(); }

    /**
     * Get a summary of items in the order (e.g., "Chocolate Bar x2, Gummy Bears x3")
     */
    public String getItemsSummary() {
        return order.getItems().stream()
            .map(item -> item.getName() + " x" + item.getQty())
            .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "OrderDisplayModel{" +
                "orderId='" + getOrderId() + '\'' +
                ", customer='" + customerName + '\'' +
                ", total=" + getGrandTotal() +
                ", status=" + getStatus() +
                '}';
    }
}
