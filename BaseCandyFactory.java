
public class BaseCandyFactory implements CandyFactory{
	@Override
	public Candy createCandy() {
		return new BaseCandy("Generic Candy", 1.20, .05, 12);
	}
	@Override
	public Candy createCandy(double price) {
		return new BaseCandy("Generic Candy", price, .05, 12);
	}
	@Override
    public Candy createCandy(double price, int quantity) {
        return new BaseCandy("Generic Candy", price, 0.05, quantity);
    }
	@Override
	public CandyPackage createPackage() {
		return new BasicCandyPackage();
	}
	@Override
	public Candy createCandy(Candy original) {
		return new BaseCandy(
				original.getName(), 
				original.getPrice(), 
				original.getWeight(),
				original.getQuantity()
    	);
	}
}
