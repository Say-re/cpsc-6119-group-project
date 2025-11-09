package patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class InventorySubject {
    private final List<InventoryObserver> observers = new ArrayList<>();

    public void attach(InventoryObserver o) { observers.add(o); }
    public void detach(InventoryObserver o) { observers.remove(o); }
    public void notifyLowStock(String productId, int qty) {
        for (InventoryObserver o : observers) o.onLowStock(productId, qty);
    }
}
