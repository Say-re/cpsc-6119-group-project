
/**
 * Write a description of class HardCandyBag here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class HardCandyBag implements CandyPackage
{
    private double price = 1.50;
    @Override 
    public void pack(Candy candy){
        System.out.println("Packing " + candy.getName() + " is in a clear plastic bag.");
    }
    @Override
    public double getPrice(){
        return price;
    }
}