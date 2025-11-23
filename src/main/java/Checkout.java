import java.util.List;
public class Checkout {
	
    private PricingCommand pricingCommand;
    
    public Checkout(PricingCommand pricingcommand){
        this.pricingCommand = pricingcommand;
    }
    public void checkout(Customer customer, ShoppingCart cart){
    	List<Candy> cartItems = cart.getItems();
    	List<CandyPackage> packages = cart.getPackages();
        double finalPrice = pricingCommand.apply(cartItems, packages);
        
        Order newOrder = new Order(cartItems);
        customer.addOrder(newOrder);
        
        System.out.println("Order placed! Total: $" + finalPrice);
        cart.clear();
    }
}