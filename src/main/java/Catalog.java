import java.util.List;
public class Catalog {
	public void showCatalog(List<InventoryItem> inventory){
        System.out.println(" Available Candy:\n");
        int index = 1;
        for (InventoryItem item : inventory){
            Candy candy = item.getCandy();
            System.out.printf("%d. %-20s $%.2f (%s grams)- Qty: %d%n", index++, candy.getName(), candy.getPrice(), candy.getWeight(), item.getQuantity());
        }
    }
}
