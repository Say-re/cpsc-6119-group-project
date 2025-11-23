
import java.util.List;
import java.util.ArrayList;
public class Inventory {
	private static Inventory instance;
   // private Map<String, Integer> stock;
    private List<InventoryItem> inventory;
    private Inventory(){  //Singleton Pattern
        inventory = new ArrayList<>();
    }
    public static Inventory getInstance(){ // exactly one instance is created-Singleton
        if (instance==null){
            instance = new Inventory();
        }
        return instance;
    }
    public List<InventoryItem> getInventory(){
    	return inventory;
    }
    public void addCandy(Candy candy, int quantity){
        for (InventoryItem item : inventory) {
        	if (item.getCandy().getName().equals(candy.getName())) {
        		item.reduceQuantity(-quantity);
        		return;
        	}
        }
        inventory.add(new InventoryItem(candy, quantity));
    }
    public void removeCandy(String candyName, int amount){
        for (InventoryItem item : inventory) {
        	if (item.getCandy().getName().equals(candyName)) {
        		item.reduceQuantity(amount);
        		break;
        	}
        }
    }
    //check stock
    public int getStock(String candyName){
        for (InventoryItem item : inventory) {
        	if (item.getCandy().getName().equals(candyName)){
        		return item.getQuantity();
        	}
        }
        return 0;
    }
    public void displayInventory(){
        System.out.println("\n Current Inventory: ");
        for (InventoryItem item : inventory) {
            System.out.println(item.getCandy().getName() + " : " + item.getQuantity());
        }
    }
}
