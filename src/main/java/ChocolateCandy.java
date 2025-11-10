
/**
 * Write a description of class ChocolateCandy here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ChocolateCandy implements Candy
{   @Override
    public String getName() {
        return "Chocolate Candy";
    }
    @Override
    public double getWeight(){
        return 40.0;
    }
    @Override
    public double getPrice(){ 
        return 2.0;
    }
    @Override
    public void prepare(){ 
        System.out.println("Chocolate is melted and shaped into bars.");
    }
    @Override
    public String toString(){
        return getName() + " $" + getPrice() + ", " + getWeight() + "grams";
    }
}
