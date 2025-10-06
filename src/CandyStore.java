
/**
 * Write a description of class CandyStore here.
 * Database of available candy products and uses Singleton Pattern
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.ArrayList;
import java.util.List;
import java.util.*;
public class CandyStore
{
   private static CandyStore instance;
   private List<Candy> catalog = new ArrayList<>();
   private Inventory inventory = Inventory.getInstance();
   
   private CandyStore(){
       Candy gummy = new GummyCandy();
       Candy chocolate = new ChocolateCandy();
       Candy hard = new HardCandy();
       catalog.add(gummy);
       catalog.add(chocolate);
       catalog.add(hard);
       //add to inventory stock
       inventory.addCandy(gummy.getName(), 15);
       inventory.addCandy(chocolate.getName(), 20);
       inventory.addCandy(hard.getName(), 10);
   }
   public static CandyStore getInstance(){
       if (instance == null){
           instance = new CandyStore();
       }
       return instance;
   }
   public List<Candy> getCatalog(){
       return catalog;
   }
   public List<InventoryItem> getInventory(){
       List<InventoryItem> inventoryItems = new ArrayList<>();
       for (Candy candy : catalog){
           int qty = inventory.getStock(candy.getName());
           inventoryItems.add(new InventoryItem(candy, qty));
       }
       return inventoryItems;
   }
   public InventoryItem getInventoryItemByIndex(int index){
       List<InventoryItem> inventoryItems = getInventory();
       if (index >= 0 && index < inventoryItems.size()){
           return inventoryItems.get(index);
       }
       return null;
   }
}