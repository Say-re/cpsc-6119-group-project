
public class RemoveItemCommand implements CartCommand {
	private ShoppingCart cart;
	private Candy item;
	
	public RemoveItemCommand(ShoppingCart cart, Candy item) {
		this.cart = cart;
		this.item = item;
	}
	@Override 
	public void execute() {
		cart.removeItem(item);
		System.out.println(item.getName() + " removed from cart.");
	}
}
