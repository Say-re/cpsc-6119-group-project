
public class GummyCandyFactory implements CandyFactory {
	@Override
    public Candy createCandy(){
        return new GummyCandy ("Gummy", 1.50, .05, 1);
    }
	@Override
	public Candy createCandy(double price) {
		return new GummyCandy("Gummy", price, .05, 25);
	}
	@Override
    public Candy createCandy(double price, int quantity) {
        return new GummyCandy("Gummy", price, 0.05, quantity);
    }
    @Override
    public CandyPackage createPackage(){
        return new GummyBag();
    }
    @Override
    public Candy createCandy(Candy original) {
        return new GummyCandy(
            original.getName(),
            original.getPrice(),
            original.getWeight(),  
            original.getQuantity()
        );
    }
}

