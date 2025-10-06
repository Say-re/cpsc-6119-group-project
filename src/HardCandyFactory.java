
/**
 * Write a description of class HardCandyFactory here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class HardCandyFactory implements CandyFactory
{
    @Override
    public Candy createCandy(){
        return new HardCandy();
    }
    @Override
    public CandyPackage createPackage(){
        return new HardCandyBag();
    }
}