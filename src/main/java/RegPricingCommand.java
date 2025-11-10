
/**
 * Write a description of class RegPricingCommand here.
 * Uses command pattern this class just gives us regular prices.
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.List;
public class RegPricingCommand implements PricingCommand

{
    public double apply(List<Candy> items){
        return items.stream().mapToDouble(Candy::getPrice).sum();
    }
}