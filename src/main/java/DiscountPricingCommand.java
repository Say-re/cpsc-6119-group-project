import java.util.List;

public class DiscountPricingCommand implements PricingCommand {
    private final double percentOff;

    public DiscountPricingCommand(double percentOff) {
        this.percentOff = percentOff;
    }

    @Override
    public double apply(List<Candy> items) {
        double total = items.stream().mapToDouble(Candy::getPrice).sum();
        return total * (1.0 - percentOff);
    }
}
