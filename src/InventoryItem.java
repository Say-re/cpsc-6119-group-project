
/**
 * Write a description of class InventoryItem here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class InventoryItem
{
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