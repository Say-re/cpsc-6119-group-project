package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String id;
    private final String customerId;
    private final Instant createdAt;
    private OrderStatus status = OrderStatus.PLACED;
    private final List<OrderItem> items = new ArrayList<>();
    private double subtotal;
    private double discountTotal;

    public Order(String id, String customerId, Instant createdAt) {
        this.id = id; this.customerId = customerId; this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public Instant getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus s) { this.status = s; }

    public List<OrderItem> getItems() { return items; }
    public void addItem(OrderItem item) {
        items.add(item);
        subtotal += item.lineTotal();
    }

    public double getSubtotal() { return subtotal; }
    public double getDiscountTotal() { return discountTotal; }
    public void addDiscount(double amount) { discountTotal += amount; }
    public double getGrandTotal() { return Math.max(0, subtotal - discountTotal); }
}
