
public class AddItemCommand implements CartCommand{
	private ShoppingCart cart;
	private Candy item;
	
	public AddItemCommand(ShoppingCart cart, Candy item) {
		this.cart = cart;
		this.item = item;
	}
	@Override
	public void execute() {
		cart.addItem(item);
		System.out.println(item.getName() + " added to cart.");
	}
}
