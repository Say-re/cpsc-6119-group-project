//chatgpt helped me some with this class - some of it was learned while doing TestFx
import javafx.geometry.Pos;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class CustomerDashboard implements UIScreen{
	private VBox dashboard;
	
	public CustomerDashboard(Customer customer, BorderPane borderPane, VBox cartBox, VBox loginBox){
		Label welcomeLabel = new Label("Welcome, " + customer.getUsername() + "!");
		welcomeLabel.setFont(Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, 20));
		
		Button shopButton = new Button("Shop Candy");
        Button ordersButton = new Button("View Orders");
        Button profileButton = new Button("Profile");
        Button logoutButton = new Button("Log Out");

        shopButton.setOnAction(e -> borderPane.setCenter(cartBox));
        ordersButton.setOnAction(e -> {
        	VBox ordersBox = new VBox(10);
        	ordersBox.setAlignment(Pos.CENTER);
        	ordersBox.setPadding(new Insets(20));
        	
        	Label title = new Label("Order History");
        	title.setFont(Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, 18));
        	ordersBox.getChildren().add(new Label("Order History"));
        	
            List<Order> customerOrders = customer.getOrders();
            if (customerOrders.isEmpty()) {
            	ordersBox.getChildren().add(new Label("Order history is empty"));
            } else {
            	for (Order order : customerOrders) {
            		Label orderLabel = new Label(order.toString());
            		orderLabel.setWrapText(true);
            		ordersBox.getChildren().add(orderLabel);
            	}
            }
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            
            Button backButton = new Button("Back to Dashboard");
            backButton.setOnAction(ev -> borderPane.setCenter(dashboard));
            
            buttonBox.getChildren().addAll(backButton, logoutButton);
            ordersBox.getChildren().add(buttonBox);
            borderPane.setCenter(ordersBox);
        });
        
        profileButton.setOnAction(e -> {
            Label profileInfo = new Label("Username: " + customer.getUsername());
            VBox profileBox = new VBox(10, new Label("Profile"), profileInfo, logoutButton);
            profileBox.setAlignment(Pos.CENTER);
            profileBox.setPadding(new Insets(20));
            borderPane.setCenter(profileBox);
        });
        
        logoutButton.setOnAction(e -> borderPane.setCenter(loginBox));
		
        dashboard = new VBox(15, welcomeLabel, shopButton, ordersButton, profileButton, logoutButton);
        dashboard.setAlignment(Pos.CENTER);
        dashboard.setPadding(new Insets(20));
	}
        
	@Override
	public Pane getView() {
		return dashboard;
	}
}
		