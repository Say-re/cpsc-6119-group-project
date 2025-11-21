// Author: Kelly Payne 10/5/25
public class ChocolateCandy implements Candy {
	private String name;
	private double price;
	private double weight;
	private int quantity;
	
	public ChocolateCandy(String name, double price, double weight, int quantity) {
		this.name = name;
		this.price = price;
		this.weight = weight;
		this.quantity = quantity;
	}
	@Override
    public String getName() {
        return name;
    }
    @Override
    public double getWeight(){
        return weight;
    }
    @Override
    public double getPrice(){ 
        return price;
    }
    @Override
    public void setPrice(double price) {
    	this.price = price;
    }
    @Override
    public void prepare(){ 
        System.out.println("Chocolate is melted and shaped into bars.");
    }
    @Override
    public int getQuantity() {
    	return quantity;
    }
    public void reduceQuantity(int amount) {
        if (amount <= quantity) {
            quantity -= amount;
        } else {
            quantity = 0; // or throw an exception
        }
    }
    @Override
    public String toString(){
        return getName() + " $" + getPrice() + ", " + getWeight() + "grams" + ", " + "Qty: " + getQuantity();
    }
}
