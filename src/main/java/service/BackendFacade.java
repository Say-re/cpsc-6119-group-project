package service;

import model.Order;
import model.OrderItem;
import model.OrderStatus;
import patterns.command.DiscountCommand;
import patterns.observer.InventorySubject;
import repo.OrderDataManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * BackendFacade - Facade pattern implementation.
 * Simple bridge the UI can call without knowing service internals.
 * Implements Observer pattern for low stock notifications.
 *
 * Usage:
 *   BackendFacade.init();
 *   Order o = BackendFacade.checkout(userId, cartItems, discounts);
 *
 * @author Travis Dagostino
 * @version 1.0
 * @since 2025-11-22
 */
public final class BackendFacade {
    private static InventorySubject subject;
    private static InventoryService inventory;
    private static OrderService orders;

    private BackendFacade() {}

    /**
     * Initializes the backend services.
     * Call once after login (or app startup).
     */
    public static void init() {
        if (orders != null) return; // already initialized
        subject = new InventorySubject();
        inventory = new InventoryService(subject);
        inventory.setLowStockThreshold(10); // Default threshold for low stock alerts
        orders = new OrderService(new OrderDataManager(), inventory);
    }

    /**
     * Allows UI to subscribe to low-stock alerts (e.g., Admin dashboard notifications).
     *
     * @param handler Callback function that receives productId and quantity
     */
    public static void onLowStock(BiConsumer<String,Integer> handler) {
        if (subject == null) init();
        subject.attach((productId, qty) -> handler.accept(productId, qty));
    }

    /**
     * Gets the inventory service instance.
     * Initializes if not already done.
     *
     * @return The InventoryService instance
     */
    public static InventoryService getInventoryService() {
        if (inventory == null) init();
        return inventory;
    }

    /**
     * Gets the inventory subject for observer pattern.
     *
     * @return The InventorySubject instance
     */
    public static InventorySubject getInventorySubject() {
        if (subject == null) init();
        return subject;
    }

    /**
     * Sets the low stock threshold.
     *
     * @param threshold The quantity threshold for low stock alerts
     */
    public static void setLowStockThreshold(int threshold) {
        if (inventory == null) init();
        inventory.setLowStockThreshold(threshold);
    }

    /** Convert UI cart → place order → apply discounts → decrement inventory → mark PAID */
    public static Order checkout(String userId, List<UiCartItem> cart, List<DiscountCommand> discounts) throws IOException {
        if (orders == null) init();
        List<OrderItem> items = new ArrayList<>();
        for (UiCartItem c : cart) {
            items.add(new OrderItem(c.productId(), c.name(), c.qty(), c.unitPrice()));
        }
        Order o = orders.createOrder(userId, items);
        if (discounts != null && !discounts.isEmpty()) {
            orders.applyDiscounts(o, discounts);
        }
        orders.decrementInventoryFor(o);
        orders.advanceStatus(o, OrderStatus.PAID); // mock payment success
        return o;
    }

    /** Load all orders for a user (Order History screen) */
    public static List<Order> ordersForUser(String userId) {
        return new OrderDataManager().getOrdersByUser(userId);
    }

    /** Reload single order (Order Tracking screen) */
    public static Order reload(String orderId) {
        if (orders == null) init();
        return orders.reload(orderId);
    }

    /** Minimal shape the UI can map its cart item into */
    public static record UiCartItem(String productId, String name, int qty, double unitPrice) {}
}
