//Uses polymorphism --allows for different types of candy to be handled
// Author: Kelly Payne 10/5/25
public interface Candy {
	String getName();
    double getWeight();
    double getPrice();
    int getQuantity();
    void prepare();
    void setPrice(double price); //added setter
}
