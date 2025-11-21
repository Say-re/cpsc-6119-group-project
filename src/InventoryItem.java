
public class InventoryItem {
	private Candy candy;
    private int quantity;
    
    public InventoryItem(Candy candy, int quantity){
        this.candy = candy;
        this.quantity = quantity;
    }
    public Candy getCandy(){
        return candy;
    }
    public int getQuantity(){
        return quantity;
    }
    public void reduceQuantity(int amount){
        if (amount <= quantity){
            quantity -=amount;
        }
    }
}
