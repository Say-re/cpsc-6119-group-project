package service;

import patterns.observer.InventorySubject;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final InventorySubject subject;
    private final Map<String,Integer> stock = new HashMap<>();
    private int lowStockThreshold = 5;

    public InventoryService(InventorySubject subject) {
        this.subject = subject;
    }

    public void setLowStockThreshold(int t) { this.lowStockThreshold = t; }

    public void setStock(String productId, int qty) {
        stock.put(productId, qty);
        if (qty <= lowStockThreshold) subject.notifyLowStock(productId, qty);
    }

    public void decrement(String productId, int by) {
        int newQty = stock.getOrDefault(productId, 0) - by;
        setStock(productId, Math.max(0, newQty));
    }

    public int getStock(String productId) { return stock.getOrDefault(productId, 0); }
}
