import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class Customer {
	private String username;   //private fields encapsulated
	private String password;
	private List<Order> orders = new ArrayList<>();
	private String email;
	private String phone;
	private String address;
	private LocalDate dateJoined;
	
	public Customer(String username, String password) {
		this.username = username;
		this.password = password;
		this.orders = new ArrayList<>();
		this.dateJoined = LocalDate.now();
	}
	public boolean authenticate(String inputUser, String inputPass) {
		return username.equals(inputUser) && password.equals(inputPass);
		//authenticate - abstracts how credentials are checked		
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public LocalDate getDateJoined() {
		return dateJoined;
	}
	public void addOrder(Order order) {
		orders.add(order);  //abstracts how orders are stored
	}
	public List<Order> getOrders(){
		return orders;
	}
}
