//Author: Kelly Payne
// Shopping Cart and Data Holder
import java.util.*;
public class ShoppingCart {
	private List<Candy> items = new ArrayList<>();
	private List<CandyPackage> packages = new ArrayList<>();
	   
	   public void addItem(Candy candy, CandyPackage candyPackage){
	       items.add(candy);
	       packages.add(candyPackage);
	       System.out.println(candy.getName() + " was added to cart.");
	   }
	   public void removeItem(Candy candy) {
		   int index = items.indexOf(candy);
		   if (index >= 0) {
			   items.remove(index);
			   packages.remove(index);
		   }
		}
	   public void viewCart(){
	       System.out.println("\nYour Cart: ");
	       double total = 0;
	       for (int i=0; i<items.size(); i++) {
	    	   Candy candy = items.get(i);
	    	   CandyPackage candyPackage = packages.get(i);
	    	   
	    	   double candyPrice = candy.getPrice();
	    	   double packagePrice = (candyPackage != null)? candyPackage.getPrice() : 0;
	    	   System.out.printf("* %s: $%.2f%s%n", candy.getName(), candyPrice, (candyPackage != null ? " + Packaging: $" + packagePrice: ""));
	    	   total += candyPrice + packagePrice;
	       }
	       System.out.printf("Total: $%.2f%n", total);
	   }
	   public List<Candy> getItems(){
	       return items;
	   }
	   public CandyPackage getPackageFor(Candy candy) {//helper method chatgpt
		    int index = items.indexOf(candy);
		    if (index >= 0) {
		        return packages.get(index);
		    }
		    return null;
	   }
	   public void clear() {
		   items.clear();
		   packages.clear();
		   System.out.println("Cart has been cleared.");
	   }
	   public List<CandyPackage> getPackages(){
		   return packages;
	   }
	   public double getTotalPrice() {
		   double total = 0.0;
		   for (int i=0; i<items.size(); i++) {
			   Candy c = items.get(i);
			   CandyPackage cp = packages.get(i);
			   total += c.getPrice() + (cp !=null ? cp.getPrice() : 0);
		   }
		   return total;
	   }
	   
}
