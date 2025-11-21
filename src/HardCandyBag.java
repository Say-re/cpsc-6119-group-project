
public class HardCandyBag implements CandyPackage{
	private double price = 2.00;
    @Override 
    public void pack(Candy candy){
        System.out.println("Packing " + candy.getName() + " is in a clear plastic bag.");
    }
    @Override
    public double getPrice(){
        return price;
    }

}
