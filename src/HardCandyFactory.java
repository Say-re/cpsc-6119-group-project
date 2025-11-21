
public class HardCandyFactory implements CandyFactory{
	@Override
    public Candy createCandy(){
        return new HardCandy("hard", 1.75, .05, 20);
    }
	@Override 
	public Candy createCandy(double price) {
		return new HardCandy("hard", price, .05, 20);
	}
	@Override
    public Candy createCandy(double price, int quantity) {
        return new HardCandy("hard", price, 0.05, quantity);
    }
    @Override
    public CandyPackage createPackage(){
        return new HardCandyBag();
    }
    @Override
    public Candy createCandy(Candy original) {
        return new HardCandy(
            original.getName(),
            original.getPrice(),
            original.getWeight(),  
            original.getQuantity()
        );
    }
}
