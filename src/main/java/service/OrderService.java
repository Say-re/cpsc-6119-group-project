package service;

import model.*;
import patterns.command.DiscountCommand;
import repo.OrderDataManager;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderService {
    private final OrderDataManager orders;
    private final InventoryService inventory;

    public OrderService(OrderDataManager orders, InventoryService inventory) {
        this.orders = orders; this.inventory = inventory;
    }

    public Order createOrder(String customerId, List<OrderItem> items) throws IOException {
        Order order = new Order(newId(), customerId, Instant.now());
        for (OrderItem it : items) order.addItem(it);
        orders.saveOrder(order);
        return order;
    }

    public void applyDiscounts(Order order, List<DiscountCommand> discounts) throws IOException {
        for (DiscountCommand d : discounts) d.apply(order);
        orders.saveOrder(order);
    }

    public void advanceStatus(Order order, OrderStatus next) throws IOException {
        order.setStatus(next);
        orders.saveOrder(order);
    }

    public void decrementInventoryFor(Order order) {
        for (OrderItem it : order.getItems()) {
            inventory.decrement(it.getProductId(), it.getQty());
        }
    }

    public Order reload(String orderId) { return orders.getOrder(orderId).orElse(null); }

    private String newId() { return UUID.randomUUID().toString(); }
}
