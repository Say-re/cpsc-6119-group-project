
/**
 * Write a description of interface PricingCommand here.
 * Using Command Pattern
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.List;
public interface PricingCommand
{
    double apply(List<Candy> items);
}