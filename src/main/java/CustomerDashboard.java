//chatgpt helped me some with this class - some of it was learned while doing TestFx
import javafx.geometry.Pos;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.stage.Stage;
import service.BackendFacade;
import model.Order;
import model.OrderItem;
// chatgpt helped with dugging and some code, but I used the TestFX example also
public class CustomerDashboard implements UIScreen{ //Factory Pattern
	private VBox dashboard;
	private VBox cartBox;
	
	public CustomerDashboard(Customer customer, BorderPane borderPane, VBox cartBox, VBox loginBox){
		this.cartBox = cartBox;
		Label welcomeLabel = new Label("Welcome, " + customer.getUsername() + "!");
		welcomeLabel.setFont(Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, 20));
		
		Button shopButton = new Button("Shop Candy");
        Button ordersButton = new Button("View Orders");
        Button profileButton = new Button("Profile");
        Button logoutButton = new Button("Log Out");

        shopButton.setOnAction(e -> borderPane.setCenter(cartBox)); //Strategy Pattern
        ordersButton.setOnAction(e -> {  //strategy pattern
        	VBox ordersBox = new VBox(10);
        	ordersBox.setAlignment(Pos.CENTER);
        	ordersBox.setPadding(new Insets(20));

        	Label title = new Label("Order History");
        	title.setFont(Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, 18));
        	ordersBox.getChildren().add(title);

            // Get orders from BackendFacade instead of Customer object
            List<model.Order> customerOrders = BackendFacade.ordersForUser(customer.getUsername());
            if (customerOrders.isEmpty()) {
            	ordersBox.getChildren().add(new Label("Order history is empty"));
            } else {
            	for (model.Order order : customerOrders) {
            		StringBuilder orderDetails = new StringBuilder();
            		orderDetails.append("Order ID: ").append(order.getId()).append("\n");
            		orderDetails.append("Order placed: ").append(
            			order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
            			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            		).append("\n");
            		orderDetails.append("Items:\n");
            		for (OrderItem item : order.getItems()) {
            			orderDetails.append("- ").append(item.getName())
            						.append(" x").append(item.getQty())
            						.append(" ($").append(String.format("%.2f", item.getUnitPrice())).append(")\n");
            		}
            		orderDetails.append("Total: $").append(String.format("%.2f", order.getGrandTotal()));

            		Label orderLabel = new Label(orderDetails.toString());
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
        
        profileButton.setOnAction(e -> { //strategy pattern
        	Label title = new Label("Profile");
        	title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        	
        	Label profileInfo = new Label("Username: " + customer.getUsername());
        	Label emailInfo = new Label("Email: " + (customer.getEmail() != null ? customer.getEmail() : "Not set"));
        	Label phoneInfo = new Label("Phone: " + (customer.getPhone() != null ? customer.getPhone() : "Not set"));
        	Label addressInfo = new Label("Address: " + (customer.getAddress() != null ? customer.getAddress() : "Not set"));
        	Label joinedInfo = new Label("Member since: " + (customer.getDateJoined()));
        	
        	Button editButton = new Button ("Edit Profile");
        	Button backButton = new Button("Back");
        	
        	backButton.setOnAction(ev -> borderPane.setCenter(dashboard));
        	editButton.setOnAction(ev -> openEditProfileScreen(customer, borderPane));
        	
            VBox profileBox = new VBox(10, title, profileInfo, emailInfo,
            		phoneInfo, addressInfo, joinedInfo, editButton, logoutButton, backButton);
            profileBox.setAlignment(Pos.CENTER);
            profileBox.setPadding(new Insets(20));
            borderPane.setCenter(profileBox);
        });
        
        logoutButton.setOnAction(e -> {
            // Close the application instead of returning to login
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
            Platform.exit();
        });

        dashboard = new VBox(15, welcomeLabel, shopButton, ordersButton, profileButton, logoutButton);
        dashboard.setAlignment(Pos.CENTER);
        dashboard.setPadding(new Insets(20));
	}
	private void openEditProfileScreen(Customer customer, BorderPane borderPane) {
	Label title = new Label("Edit Profile");
	title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
	
	TextField emailField = new TextField(customer.getEmail());
	emailField.setPromptText("Email");
	
	TextField phoneField = new TextField(customer.getPhone());
	phoneField.setPromptText("Phone number");
	
	TextField addressField = new TextField(customer.getAddress());
	addressField.setPromptText("Address");
	
	Button saveButton = new Button("Save");
	Button cancelButton = new Button("Cancel");
	
	saveButton.setOnAction(ev -> {
		customer.setEmail(emailField.getText());
		customer.setPhone(phoneField.getText());
		customer.setAddress(addressField.getText());
		borderPane.setCenter(dashboard); // back to dashboard
	});
    cancelButton.setOnAction(ev -> borderPane.setCenter(dashboard));
    
    VBox editBox = new VBox(10, title, emailField, phoneField, addressField,
    		saveButton, cancelButton);
    editBox.setAlignment(Pos.CENTER);
    editBox.setPadding(new Insets(20));
    borderPane.setCenter(editBox);
	}
	@Override
	public Pane getView() {
		return dashboard;
	}
}
		