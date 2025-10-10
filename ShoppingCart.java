//Author: Kelly Payne
// Shopping Cart and Data Holder
import java.util.*;
public class ShoppingCart {
	private List<Candy> items = new ArrayList<>();
	   
	   public void addItem(Candy candy){
	       items.add(candy);
	      // System.out.println(candy.getName() + " was added to cart.");
	   }
	   public void removeItem(Candy candy) {
		   items.remove(candy);
	   }
	   public void viewCart(){
	       System.out.println("\n Your Cart: ");
	       double total = 0;
	       for (Candy candy : items){
	           System.out.println("* " + candy.getName() + ": $" + candy.getPrice());
	           total += candy.getPrice();
	       }
	       System.out.println("Total: $" + total);
	   }
	   public List<Candy> getItems(){
	       return items;
	   }
}
