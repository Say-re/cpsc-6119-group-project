//chatgpt helped with some of this coding particularly bugs
import java.util.Scanner;

public class ConsoleUI {
	private CandyStore store;
	private Catalog catalogView;
	private ShoppingCart cart;
	private Scanner scanner;

	public ConsoleUI(CandyStore store, Catalog catalogView, ShoppingCart cart) {
		this.store = store;
		this.catalogView = catalogView;
		this.cart = cart;
		this.scanner = new Scanner(System.in);
	}
	public void run() {
		boolean running = true;
		while (running) {
			catalogView.showCatalog(store.getInventory());
			System.out.print("\nEnter candy quantity to add to your cart (0 to checkout): ");
			int choice = scanner.nextInt();
		
			if (choice ==0) {
				running = false;}
			else {
				handleAddToCart(choice);
			}
	}
		cart.viewCart();
		PricingCommand pricing = new RegPricingCommand();
		Checkout processor = new Checkout(pricing);
		processor.checkout(cart.getItems());
	}
	private void handleAddToCart(int choice) {
		InventoryItem selectedItem = store.getInventoryItemByIndex(choice -1);
		if (selectedItem !=null && selectedItem.getQuantity()>0) {
			CandyFactory factory = getFactoryForCandy(selectedItem.getCandy());
			Candy candy = factory.createCandy();
			CandyPackage candyPackage = factory.createPackage();
		
			cart.addItem(candy, candyPackage);
			Inventory.getInstance().removeCandy(selectedItem.getCandy().getName(), 1);
			System.out.println(selectedItem.getCandy().getName() + " was added to your cart.");
		}else {
			System.out.println("Invalid selection or this item is out of stock.");
		}
	}

	private CandyFactory getFactoryForCandy(Candy candy) {
		if (candy instanceof GummyCandy) {
			return new GummyCandyFactory();
		} else if (candy instanceof ChocolateCandy) {
			return new ChocolateCandyFactory();
		} else if (candy instanceof HardCandy){
			return new HardCandyFactory();
		} else if (candy instanceof BaseCandy){
			return new BaseCandyFactory();
		} else {
			throw new IllegalArgumentException("No factory found for this candy: " + candy.getClass().getSimpleName());
		}
	}	
}