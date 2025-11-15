import java.time.LocalDateTime;
import java.util.*;
public class Order {
	private List<Candy> items;
	private LocalDateTime orderTime;

	public Order(List<Candy> items) {
		this.items = new ArrayList<>(items);
		this.orderTime = LocalDateTime.now();
	}
	public List<Candy> getItems() { return items; }
	public LocalDateTime getOrderTime() { return orderTime; }
	
	@Override
	public String toString() {
		return "Order at " + orderTime + ": " + items;
	}
}
