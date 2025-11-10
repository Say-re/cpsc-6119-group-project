
/**
 * Write a description of class GummyCandy here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GummyCandy implements Candy
{   @Override
    public String getName() {
        return "Gummy Candy";
    }
    @Override
    public double getWeight(){
        return 25.0;
    }
    @Override
    public double getPrice(){ 
        return 1.75;
    }
    @Override
    public void prepare(){
        System.out.println("This is soft candy.");
    }
}