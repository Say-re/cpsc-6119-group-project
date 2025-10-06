
/**
 * Write a description of class HardCandy here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class HardCandy implements Candy
{   @Override
    public String getName() {
        return "Hard Candy";
    }
    public double getWeight(){
        return 25.0;
    }
    public double getPrice(){ 
        return 1.5;
    }
    @Override
    public void prepare() {
        System.out.println("This is hard candy.");
    }
}