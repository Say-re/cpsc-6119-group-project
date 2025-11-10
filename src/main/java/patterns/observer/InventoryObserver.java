package patterns.observer;

public interface InventoryObserver {
    void onLowStock(String productId, int qty);
}
