// CandyStoreApp.java
public class CandyStoreApp {
    public static void main(String[] args) {
        // Factories (Abstract Factory already exists)
        CandyFactory chocolateFactory = new ChocolateCandyFactory();
        CandyFactory gummyFactory     = new GummyCandyFactory();

        // Create products
        Candy choco = chocolateFactory.createCandy();   // ChocolateCandy
        Candy gummy = gummyFactory.createCandy();       // GummyCandy

        // Cart
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(choco);
        cart.addItem(gummy);

        // Checkout with regular pricing
        Checkout checkout = new Checkout(cart, new RegPricingCommand());
        checkout.review();
        checkout.placeOrder();

        // Example: run again with a discount
        cart.addItem(choco);
        cart.addItem(gummy);
        checkout.setPricing(new DiscountPricingCommand(0.10)); // 10% off
        checkout.review();
        checkout.placeOrder();
    }
}
