import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
// chatgpt helped with some parts of this code and a lot of debugging.
public class CandyStoreApp {

	public static void main(String[] args) {
		//loadInventFromCSV("data.csv");
		CandyStore store = CandyStore.getInstance(); //Singleton Inventory
		        
		Catalog catalogView = new Catalog();   // View Catalog
        ShoppingCart cart = new ShoppingCart(); // User's Cart
        
        List<Customer> customers = List.of(
        new Customer("mary", "5432"),
        new Customer("william", "1234"));
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Candy Store!");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        Customer loginCustomer = null;
        boolean login = false;
        
        for (Customer customer : customers) {
        	if (customer.authenticate(username,  password)) {
        		loginCustomer = customer;
        		login = true;
        		break;
        	}
        }
        if (!login) {
        	System.out.println("Invalid login");
        	scanner.close();
        	return;
        }
        System.out.println("Logged in as: " + loginCustomer.getUsername());
        
        boolean running = true;
        while (running){
        	System.out.println("\nAvailable Candy: ");
        	List<InventoryItem> inventoryItems = store.getInventory();
        	for (int i=0; i<inventoryItems.size(); i++) {
        		InventoryItem item = inventoryItems.get(i);
        		Candy candy = item.getCandy();
        		int qty = item.getQuantity();
        		System.out.printf("%d. %s - $%.2f (Stock: %d)%n", i + 1, candy.getName(), candy.getPrice(), qty);
        	}
            
            System.out.print("\nEnter candy quantity to add to your cart (0 to checkout): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 0){
                running = false;
                break;}
                else{
                    InventoryItem selectedItem = store.getInventoryItemByIndex(choice - 1);  
                    if (selectedItem != null && selectedItem.getQuantity()>0){
                        CandyFactory factory = getFactoryForCandy(selectedItem.getCandy());
                        Candy candy = factory.createCandy(selectedItem.getCandy());// pass real candy
                        candy.setPrice(selectedItem.getCandy().getPrice());
                        
                        System.out.print("Would you like this candy packaged? (yes/no): ");
                        String packChoice = scanner.nextLine().trim().toLowerCase();
                        
                        CandyPackage pack = null;
                        if (packChoice.equals("yes")|| packChoice.equals("y")) {
                        	pack = factory.createPackage();
                        	pack.pack(candy);
                        }
                      
                        
                        cart.addItem(candy, pack);//add candy to cart
                        
                        Inventory.getInstance().removeCandy(selectedItem.getCandy().getName(), 1);
                        System.out.println(selectedItem.getCandy().getName() + " was added to your cart.");
                    }else{
                            System.out.println("Invalid selection or out of stock");
                    }
            }
        }
        scanner.close();
        
        cart.viewCart();
        PricingCommand pricing = new RegPricingCommand();
        Checkout processor = new Checkout(pricing);
        processor.checkout(loginCustomer, cart);
       
        Order order = new Order(cart.getItems());
        loginCustomer.addOrder(order);
    }
    public static CandyFactory getFactoryForCandy(Candy candy){
        if (candy instanceof GummyCandy){
            return new GummyCandyFactory();
        }else if (candy instanceof ChocolateCandy){
            return new ChocolateCandyFactory();
        }else if (candy instanceof HardCandy){
            return new HardCandyFactory();
        }else  if(candy instanceof BaseCandy){
        	return new BaseCandyFactory();
        } else {
            throw new IllegalArgumentException("No factory found for this candy: " + candy.getClass().getSimpleName());
        }

	}
    public static CandyFactory getFactoryForCandyType(String candyType) {
    	switch (candyType.toLowerCase()) {
    	case "gummy":
    	case "gummy candy":
    		return new GummyCandyFactory();
    	case "chocolate":
    	case "chocolate candy":
    		return new ChocolateCandyFactory();
    	case "hard":
    	case "hard candy":
    		return new HardCandyFactory();
    	default:
    		throw new IllegalArgumentException("No factory found for candy: " + candyType);
    	}
    }
    public static void loadInventFromCSV(String filename) {
        try (InputStream input = CandyStoreApp.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                System.out.println("File is not found in classpath: " + filename);
                return;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
                String line;
                Inventory inventory = Inventory.getInstance();
                int totalAdded = 0;
                br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 4) continue;

                    String name = parts[0].trim();
                    String type = parts[1].trim();

                    double price;
                    try {
                        price = Double.parseDouble(parts[2].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price value: " + parts[2]);
                        continue;
                    }
                    int quantity = Integer.parseInt(parts[3].trim());

                    CandyFactory factory = getFactoryForCandyType(type);
                    Candy candy = factory.createCandy();
                    candy.prepare();
                    for (int i = 0; i < quantity; i++) {
                        inventory.addCandy(name, 1);
                        totalAdded++;
                    }
                }
                System.out.println("Loaded inventory: " + totalAdded + "candies");
            }
        } catch (Exception e) {
            System.out.println("Error loading inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


