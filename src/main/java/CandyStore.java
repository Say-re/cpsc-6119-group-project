import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;

public class CandyStore {
	private static CandyStore instance= null;
	private List<Candy> catalog = new ArrayList<>();
	private Inventory inventory = Inventory.getInstance();
	   
	   private CandyStore(){
		   loadInventFromCSV("data.csv");
	       
	   }
	   public void loadInventFromCSV(String filepath) {
		   try (InputStream input = CandyStore.class.getResourceAsStream("/src/main/java/data/inventory.csv")){
			   if (input == null) {
				   System.err.println("File not found in classpath: /src/main/java/data/inventory.csv");
				   loadDefaultCatalogAndInvent();
				   return;
			   }
		   
		   try (CSVReader reader = new CSVReader(new InputStreamReader(input))){
		   	   String[] line;
			   reader.readNext();
			   while ((line = reader.readNext()) != null) {
				   if (line.length < 4) {
					   continue; }
				   
				   String name = line[0].trim();
				   String type = line[1].trim();
				   double price = Double.parseDouble(line[2].trim());
				   int stock = Integer.parseInt(line[3].trim());
				   
				   double weight;
				   String lowerType = type.toLowerCase();
				   if (lowerType.contains("gummy")) {
					   weight = 0.05;
				   } else if (lowerType.contains("chocolate")) {
					   weight = .15;
				   } else if (lowerType.contains("hard")) {
					   weight = .10;
				   } else {
					   weight = .05; //default weight
				   }
				   Candy candy = createCandy(name, price, weight, stock);
				   catalog.add(candy);
				   inventory.addCandy(candy.getName(), stock);
				}
			  }
		   }catch (Exception e) {
			   System.err.println("Error loading data from CSV: " + e.getMessage());
			   e.printStackTrace();
			   loadDefaultCatalogAndInvent();
		  }
		   System.out.println("Load inventory: " + catalog.size() + " candies");
		}
	   
	   private Candy createCandy(String name, double price, double weight, int quantity) {
		   String lowerName = name.toLowerCase();
		   if (lowerName.contains("gummy")) {
			   return new GummyCandy(name, price, weight, quantity);
		   }else if (lowerName.contains("chocolate")) {
			   return new ChocolateCandy(name, price, weight, quantity);
		   }else if (lowerName.contains("hard")) {
			   return new HardCandy(name, price, weight, quantity);
		   }else {
			   return new BaseCandy(name, price, weight, quantity);
		   }
	   }
	   private void loadDefaultCatalogAndInvent() {
		   Candy gummy = new GummyCandy("Gummy Bear", 1.00, .05, 20);
		   Candy chocolate = new ChocolateCandy("Chocolate Bar", 1.50, .05, 25);
		   Candy hard = new HardCandy("Lollipop", .75,.05, 20);
		   catalog.add(gummy);
		   catalog.add(chocolate);
		   catalog.add(hard);
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
