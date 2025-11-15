import java.util.List;
import java.util.ArrayList;
public class Customer {
	private String username;
	private String password;
	private List<Order> orders;
	
	public Customer(String username, String password) {
		this.username = username;
		this.password = password;
		this.orders = new ArrayList<>();
	}
	public boolean authenticate(String inputUser, String inputPass) {
		return username.equals(inputUser) && password.equals(inputPass);
				
	}
	public String getUsername() {
		return username;
	}
	
	public void addOrder(Order order) {
		orders.add(order);
	}
	public List<Order> getOrders(){
		return orders;
	}
}
