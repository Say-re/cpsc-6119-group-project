//Author: Kelly Payne Command Pattern used
import java.util.List;
public interface PricingCommand {
	double apply(List<Candy> items, List<CandyPackage> packages);
}
