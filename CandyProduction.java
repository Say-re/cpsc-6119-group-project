
/**
 * Write a description of class CandyProduction here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class CandyProduction
{
   public static void main(String[] args){
       CandyFactory factory = new GummyCandyFactory();
       Candy candy = factory.createCandy();
       candy.prepare();
       
       CandyPackage pack = factory.createPackage();
       pack.pack(candy);
       
       Inventory inventory = Inventory.getInstance();
       inventory.addCandy(candy.getName(), 1);
       // show current inventory
       inventory.displayInventory();
   }
}