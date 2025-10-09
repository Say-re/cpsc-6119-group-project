
/**
 * Write a description of class Inventory here.
 * Singleton Pattern used here when we created one shared instance 
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.Map;
import java.util.HashMap;
public class Inventory
{
    private static Inventory instance;
    private Map<String, Integer> stock;
    
    private Inventory(){
        stock = new HashMap<>();
    }
    public static Inventory getInstance(){
        if (instance==null){
            instance = new Inventory();
        }
        return instance;
    }
    public void addCandy(String candyName, int quantity){
        stock.put(candyName, stock.getOrDefault(candyName, 0) + quantity);
    }
    public void removeCandy(String candyName, int quantity){
        if (stock.containsKey(candyName)){
            int current = stock.get(candyName);
            int updated = Math.max(0, current - quantity);
            stock.put(candyName, updated);
        }
    }
    //check stock
    public int getStock(String candyName){
        return stock.getOrDefault(candyName, 0);
    }
    public void displayInventory(){
        System.out.println("\n Current Inventory: ");
        for (Map.Entry<String, Integer> entry : stock.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}