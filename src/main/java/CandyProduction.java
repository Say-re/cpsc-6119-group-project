
public class CandyProduction {
	public static void main(String[] args){
	       CandyFactory factory = new GummyCandyFactory(); // Abstract Factory 
	       Candy candy = factory.createCandy();
	       candy.prepare();
	       
	       CandyPackage pack = factory.createPackage();
	       pack.pack(candy);
	       
	       Inventory inventory = Inventory.getInstance(); // Singleton pattern
	       inventory.addCandy(candy.getName(), 1);
	       // show current inventory
	       inventory.displayInventory();
	   }
}
