import java.util.List;

public class RemoveItemCommand implements CartCommand {
	private ShoppingCart cart;
	private Candy item;
	private CandyPackage candyPackage;
	private int quantity;
	
	public RemoveItemCommand(ShoppingCart cart, Candy item, CandyPackage candyPackage, int quantity) {
		this.cart = cart;
		this.item = item;
		this.candyPackage = candyPackage;
		this.quantity = quantity;
	}
	@Override 
	public void execute() {
		int removeCount = 0;
		for (int i = 0; i<quantity; i++) {
			if (cart.getItems().contains(item)) {
				List<Candy> items = cart.getItems();
				List<CandyPackage> packages = cart.getPackages();
				for (int j=0; j < items.size(); j++) {
					if (items.get(j) == item && packages.get(j) == candyPackage) {
					items.remove(j);
					packages.remove(j);
					removeCount++;
					break;
					}
				}
			}
		}
		//cart.removeItem(item);
		System.out.println(removeCount + getDescription() + " removed from cart.");
	}
	@Override
    public String getName() {
        return item.getName() + (candyPackage != null ? " [" + candyPackage.getClass().getSimpleName() + "]" : "");
    }
	 @Override
	    public double getPrice() {
	        double candyPrice = item.getPrice() * quantity;
	        double packagePrice = (candyPackage != null ? candyPackage.getPrice() * quantity : 0);
	        return candyPrice + packagePrice;
	    }
}
