
/**
 * 
 *Abstract Factory Interface - declares methods to creaete families of related objects
 * @author (your name)
 * @version (a version number or a date)
 */
public interface CandyFactory
{
    Candy createCandy();
    CandyPackage createPackage();
}