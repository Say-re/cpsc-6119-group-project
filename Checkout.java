import java.util.List;

public class Checkout {

    // Optional cart reference (can be null if using the list-based API)
    private final ShoppingCart cart;
    private PricingCommand pricingCommand;

    // Your original constructor: Checkout with a ShoppingCart
    public Checkout(ShoppingCart cart, PricingCommand pricingCommand){
        this.cart = cart;
        this.pricingCommand = pricingCommand;
    }

    // Alternative constructor (matches origin/master style): no cart, just a pricing command
    public Checkout(PricingCommand pricingCommand){
        this(null, pricingCommand);
    }

    public void setPricing(PricingCommand pricing){ 
        this.pricingCommand = pricing; 
    }

    /** Shows cart contents when a cart is attached */
    public void review(){
        if (cart != null){
            System.out.println("=== Review Cart ===");
            cart.viewCart(); // prints each item and subtotal
        } else {
            System.out.println("No ShoppingCart attached. Use checkout(List<Candy>) instead.");
        }
    }

    /** Method signature that matches the incoming version from origin/master */
    public void checkout(List<Candy> cartItems){
        double finalPrice = pricingCommand.apply(cartItems);
        System.out.println("Total: $" + finalPrice);
    }

    /** Places an order using the attached ShoppingCart (your original flow) */
    public void placeOrder(){
        if (cart == null){
            System.out.println("No ShoppingCart attached. Use checkout(List<Candy>) instead.");
            return;
        }
        List<Candy> items = cart.getItems();
        double total = pricingCommand.apply(items);
        System.out.printf("Total Due: $%.2f%n", total);
        System.out.println("Order placed. Thank you!");
        cart.clear();
    }
}
