
/**
 * Write a description of class GummyBag here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GummyBag implements CandyPackage
{
    private double price = 2.00;
    @Override
    public void pack(Candy candy){
        System.out.println("Packing " + candy.getName() + " in clear plastic bag.");
    }
    public double getPrice(){
        return price;
    }
}