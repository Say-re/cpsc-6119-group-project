
/**
 * Write a description of class Checkout here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.List;
public class Checkout
{
    // instance variables - replace the example below with your own
    private PricingCommand pricingCommand;
    
    public Checkout(PricingCommand pricingcommand){
        this.pricingCommand = pricingcommand;
    }
    public void checkout(List<Candy> cartItems){
        double finalPrice = pricingCommand.apply(cartItems);
        System.out.println("Total: $" + finalPrice);
    }
}