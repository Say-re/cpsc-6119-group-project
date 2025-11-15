
public class ChocolateCandyFactory implements CandyFactory{
	@Override
    public Candy createCandy(){
        return new ChocolateCandy("chocolate", 1.50, .05, 25);            
    }
	public Candy createCandy(double price) {
		return new ChocolateCandy("chocolate", price, .05, 25);
	}
	@Override
    public Candy createCandy(double price, int quantity) {
        return new ChocolateCandy("chocolate", price, 0.05, quantity);
    }
    @Override
    public CandyPackage createPackage(){
        return new ChocolateBox();
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
