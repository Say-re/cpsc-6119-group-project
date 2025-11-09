package model;

public class OrderItem {
    private final String productId;
    private final String name;
    private final int qty;
    private final double unitPrice;

    public OrderItem(String productId, String name, int qty, double unitPrice) {
        this.productId = productId; this.name = name; this.qty = qty; this.unitPrice = unitPrice;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getQty() { return qty; }
    public double getUnitPrice() { return unitPrice; }
    public double lineTotal() { return qty * unitPrice; }
}
