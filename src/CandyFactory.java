//Author: Kelly Payne 10/5/25
// Abstract Factory Interface - declares methods
// to create families of related objects
public interface CandyFactory {
	Candy createCandy();
	Candy createCandy(double price);//candy price from CSV
	Candy createCandy(double price, int quantity); //price + qty
    CandyPackage createPackage();
    
    Candy createCandy(Candy original);
}
