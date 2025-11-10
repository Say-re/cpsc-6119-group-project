package app;

import model.*;
import patterns.command.PercentOff;
import patterns.observer.InventoryObserver;
import patterns.observer.InventorySubject;
import repo.OrderDataManager;
import service.InventoryService;
import service.OrderService;

import java.io.IOException;
import java.util.List;

public class Demo {
    public static void main(String[] args) throws IOException {
        InventorySubject subject = new InventorySubject();
        subject.attach((productId, qty) ->
                System.out.println("LOW STOCK: " + productId + " → " + qty));
        InventoryService inventory = new InventoryService(subject);
        inventory.setLowStockThreshold(3);
        inventory.setStock("P001", 4);

        OrderService svc = new OrderService(new OrderDataManager(), inventory);

        OrderItem bar = new OrderItem("P001", "Chocolate Bar", 2, 2.50);
        Order order = svc.createOrder("U123", List.of(bar));
        svc.applyDiscounts(order, List.of(new PercentOff(0.10)));
        svc.decrementInventoryFor(order);
        svc.advanceStatus(order, OrderStatus.PAID);

        System.out.println("Order " + order.getId() + " total = $" + order.getGrandTotal());
    }
}
