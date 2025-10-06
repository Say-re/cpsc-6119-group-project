
/**
 * Write a description of class CandyStoreApp here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.List;
import java.util.Scanner;
public class CandyStoreApp
{
    public static void main(String[] args) {
        CandyStore store = CandyStore.getInstance(); //Singleton Inventory
        Catalog catalogView = new Catalog();   // View Catalog
        ShoppingCart cart = new ShoppingCart(); // User's Cart
               
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running){
            catalogView.showCatalog(store.getInventory());
            System.out.print("\nEnter candy quantity to add to cart (0 to checkout): ");
            int choice = scanner.nextInt();
            
            if (choice == 0){
                running = false;}
                else{
                    InventoryItem selectedItem = store.getInventoryItemByIndex(choice - 1);
                    if (selectedItem != null && selectedItem.getQuantity()>0){
                        CandyFactory factory = getFactoryForCandy(selectedItem.getCandy());
                        Candy candy = factory.createCandy();
                        CandyPackage pack = factory.createPackage();
                        
                        cart.addItem(candy);//add candy to cart
                        
                        Inventory.getInstance().removeCandy(selectedItem.getCandy().getName(), 1);
                        System.out.println(selectedItem.getCandy().getName() + " was added to your cart.");
                    }else{
                            System.out.println("Invalid selection or out of stock");
                    }
            }
        }
        cart.viewCart();
        PricingCommand pricing = new RegPricingCommand();
        Checkout processor = new Checkout(pricing);
        processor.checkout(cart.getItems());
    }
    public static CandyFactory getFactoryForCandy(Candy candy){
        if (candy instanceof GummyCandy){
            return new GummyCandyFactory();
        }else if (candy instanceof ChocolateCandy){
            return new ChocolateCandyFactory();
        }else if (candy instanceof HardCandy){
            return new HardCandyFactory();
        }else {
            throw new IllegalArgumentException("No factory found for this candy: " + candy.getClass().getSimpleName());
        }
    }
}
        
    
