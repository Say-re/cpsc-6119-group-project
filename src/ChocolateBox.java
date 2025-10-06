
/**
 * Write a description of class ChocolateBox here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ChocolateBox implements CandyPackage
{
    private double price = 3.00;
    @Override
    public void pack(Candy candy){
        System.out.println("Packing " + candy.getName() + " in a decorative box.");
    }
    public double getPrice(){
        return price;
    }
}