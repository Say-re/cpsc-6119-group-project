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
 * BackendFacade
 * Simple bridge the UI can call without knowing service internals.
 * Usage:
 *   BackendFacade.init();
 *   Order o = BackendFacade.checkout(userId, cartItems, discounts);
 */
public final class BackendFacade {
    private static InventorySubject subject;
    private static InventoryService inventory;
    private static OrderService orders;

    private BackendFacade() {}

    /** Call once after login (or app startup) */
    public static void init() {
        if (orders != null) return; // already initialized
        subject = new InventorySubject();
        inventory = new InventoryService(subject);
        inventory.setLowStockThreshold(3); // tweak in Admin later if needed
        orders = new OrderService(new OrderDataManager(), inventory);
    }

    /** UI can subscribe to low-stock alerts (e.g., Admin dashboard toast) */
    public static void onLowStock(BiConsumer<String,Integer> handler) {
        if (subject == null) init();
        subject.attach((productId, qty) -> handler.accept(productId, qty));
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
