// Checkout.java
import java.util.List;

public class Checkout {
    private final ShoppingCart cart;
    private PricingCommand pricing;

    public Checkout(ShoppingCart cart, PricingCommand pricing){
        this.cart = cart;
        this.pricing = pricing;
    }

    public void setPricing(PricingCommand pricing){ this.pricing = pricing; }

    public void review(){
        System.out.println("=== Review Cart ===");
        cart.viewCart(); // prints each item and subtotal
    }

    public void placeOrder(){
        List<Candy> items = cart.getItems();
        double total = pricing.apply(items);
        System.out.printf("Total Due: $%.2f%n", total);
        System.out.println("Order placed. Thank you!");
        cart.clear();
    }
}
