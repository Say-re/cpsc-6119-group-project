
/**
 * Write a description of class GummyCandyFactory here.
 * abstract Factory Pattern
 * @author (your name)
 * @version (a version number or a date)
 */
public class GummyCandyFactory implements CandyFactory
{
    @Override
    public Candy createCandy(){
        return new GummyCandy();
    }
    @Override
    public CandyPackage createPackage(){
        return new GummyBag();
    }
}