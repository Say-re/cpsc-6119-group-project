
/**
 * Write a description of class ChocolateCandyFactory here.
 * Abstract Factory Pattern
 * @author (your name)
 * @version (a version number or a date)
 */
public class ChocolateCandyFactory implements CandyFactory
{
    @Override
    public Candy createCandy(){
        return new ChocolateCandy();            
    }
    @Override
    public CandyPackage createPackage(){
        return new ChocolateBox();
    }
}