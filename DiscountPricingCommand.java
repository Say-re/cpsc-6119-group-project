// DiscountPricingCommand.java
import java.util.List;

public class DiscountPricingCommand implements PricingCommand {
    private final double percentOff; // e.g., 0.10 for 10%
    public DiscountPricingCommand(double percentOff){ this.percentOff = percentOff; }
    @Override public double apply(List<Candy> items){
        double total = items.stream().mapToDouble(Candy::getPrice).sum();
        return total * (1.0 - percentOff);
    }
}
