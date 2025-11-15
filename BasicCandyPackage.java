
public class BasicCandyPackage implements CandyPackage{
	private double price = 2.00;
	@Override
	public void pack(Candy candy) {
		System.out.println("Packing " + candy.getName() + " in a cleaar wrapper.");
	}
	@Override
	public double getPrice() {
		return price;
	}

}
