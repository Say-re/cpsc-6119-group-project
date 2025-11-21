
public class AddItemCommand implements CartCommand{
	private ShoppingCart cart;
	private Candy item;
	private CandyPackage candyPackage;
	private int quantity;
	
	public AddItemCommand(ShoppingCart cart, Candy item, CandyPackage candyPackage,int quantity) {
		this.cart = cart;
		this.item = item;
		this.candyPackage = candyPackage;
		this.quantity = quantity;
	}
	@Override
	public void execute() {
		if (candyPackage != null) {
			candyPackage.pack(item);
		}
		for (int i=0; i < quantity; i++) {
			cart.addItem(item, candyPackage);
		}
		
		System.out.println(quantity + " " + item.getName() + (candyPackage != null ? " [" +
		candyPackage.getClass().getSimpleName() + "]" : "") + " added to cart.");
	}
	@Override
    public String getName() {
        return item.getName() + (candyPackage != null ? " [" + candyPackage.getClass().getSimpleName() + "]" : "");
    }

	@Override
	public double getPrice() {
		double candyPrice = item.getPrice() * quantity;
		double packagePrice = (candyPackage != null ? candyPackage.getPrice() * quantity: 0);
		return candyPrice + packagePrice;
	}
	@Override
	public String getDescription() {
		String pkg = candyPackage != null ? candyPackage.getClass().getSimpleName() : "None";
		return item.getName() + " [" + pkg + "] x"+ quantity;
	}
}
