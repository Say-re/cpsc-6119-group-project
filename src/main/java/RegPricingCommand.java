// Author: Kelly Payne  
// Uses Command pattern - this class gives us regular prices
import java.util.List;
public class RegPricingCommand implements PricingCommand{
	public double apply(List<Candy> items, List<CandyPackage> packages){
        return items.stream().mapToDouble(Candy::getPrice).sum();
    }
}
