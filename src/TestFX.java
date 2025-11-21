// chatgpt helped me greatly with this class as I have never done an UI other than text based.
import javafx.application.Application;
import javafx.scene.layout.Pane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ListCell;
public class TestFX extends Application {
	private VBox cartBox;
	private Customer loggedInCustomer;
	private ShoppingCart shoppingCart = new ShoppingCart();
	private List<Candy> cart = new ArrayList<>();
	{cart.size();}
	private double totalPrice = 0.0;
	//private TextFormatter<Integer> formatter;
	private void clearCart(ObservableList<Candy> cartItems, Label totalLabel) {
		cartItems.clear(); // clears UI list
		totalPrice = 0.0; // resets total
		totalLabel.setText("Total: $0.00");
		System.out.println("Your cart has been cleared.");
	}
	private void processCheckout(ObservableList<Candy> cartItems, Label totalLabel,
			ListView<Candy> cartListView, BorderPane borderPane, VBox loginBox, Customer loggedInCustomer, Pane dashboardView) {
    	if (cartItems.isEmpty()) {
    		showAlert("Cart Empty", "Your cart is empty. You can add items before checking out.");
    		return;
    	}
    	Map<String, Long> summary = cartItems.stream()
    			.collect(Collectors.groupingBy(Candy::getName, Collectors.counting()));
    	StringBuilder sb = new StringBuilder();
    	sb.append("Order Summary:\n\n");
    	summary.forEach((name, qty) -> sb.append(name).append(" x").append(qty).append("\n"));
    	sb.append("\nTotal: ").append(String.format("$%.2f", totalPrice));
    	sb.append("\n\nProceed with checkout?");
    	
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    	confirm.setTitle("Confirm Checkout");
    	confirm.setHeaderText("Please confirm that your order is correct");
    	confirm.setContentText(sb.toString());
    	
    	confirm.showAndWait().ifPresent(response -> {
    		if (response == ButtonType.OK) {
    			Order newOrder = new Order(new ArrayList<>(cartItems));
    			loggedInCustomer.addOrder(newOrder);
    			
    			String orderId = UUID.randomUUID().toString().substring(0,8).toUpperCase();
    			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    			// process order
    			String receipt = "Order ID: " + orderId + "\nDate: " + timestamp + "\n\n" + sb.toString();
    			
    			Alert info = new Alert(Alert.AlertType.INFORMATION);
    			info.setTitle("Order Placed");
    			info.setHeaderText("Thank you for your order!");
    			info.setContentText(receipt);
    			info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    			info.showAndWait();
    			
    			cartItems.clear();
    			totalPrice = 0.0;
    			totalLabel.setText("Total: $0.00");
    			borderPane.setCenter(dashboardView);
    		}
    	});
	}
       
    private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	private int parseQuantity(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	@Override
	public void start(Stage stage) {
		// setting up UI 
		TextField quantityField = new TextField();
		quantityField.setPromptText("Quantity");
		
		TextFormatter<Integer> formatter = new TextFormatter<>(change -> {
			String newText = change.getControlNewText();
			if (newText.matches("[1-9]|1[0-9]")) {
				return change;
				}
			return null;
			});
		quantityField.setTextFormatter(formatter);
				
		quantityField.setOnMouseClicked(e -> quantityField.clear());
		quantityField.setOnKeyPressed(e -> {
			if (!quantityField.getText().isEmpty()) {
				quantityField.selectAll();
			}
		});
		//Login UI
		Label titleLabel = new Label("Customer Login");
		TextField usernameField = new TextField();
		usernameField.setPromptText("Username");
		        
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		        
		Label messageLabel = new Label();
		Button loginButton = new Button("Login");
								
		VBox loginBox = new VBox(10,titleLabel, usernameField, passwordField, loginButton, messageLabel);
		loginBox.setAlignment(Pos.CENTER);
		loginBox.setPadding(new Insets(20));
	    // Candy Store & Inventory
		CandyStore store = CandyStore.getInstance();
		ObservableList<InventoryItem> catalogList = FXCollections.observableList(Inventory.getInstance().getInventory());
		
		ListView<InventoryItem> candyListView = new ListView<>();
		candyListView.setItems(catalogList);
		candyListView.setPrefHeight(200);
		
		candyListView.setCellFactory(lv -> new ListCell<InventoryItem>() {
			@Override
			protected void updateItem(InventoryItem item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				}else {
					Candy candy = item.getCandy();
					setText(candy.getName() + " - $" + String.format("%.2f", candy.getPrice())
                    + " (Stock: " + item.getQuantity() + ")");
				}
			}
		});
		Label packageLabel = new Label("Choose package type:");
		RadioButton boxOption = new RadioButton("Box ($3.00)");
		RadioButton bagOption = new RadioButton("Bag ($2.00)");
		
		RadioButton noPackageOption = new RadioButton("No Box/Bag");
		ToggleGroup packageGroup = new ToggleGroup();
		noPackageOption.setToggleGroup(packageGroup);
		boxOption.setToggleGroup(packageGroup);
		bagOption.setToggleGroup(packageGroup);
		noPackageOption.setSelected(true);
		
		ObservableList<Candy> cartItems = FXCollections.observableArrayList();
		ListView<Candy> cartListView = new ListView<>(cartItems);
		cartListView.setPrefHeight(150);
		Label totalLabel = new Label("Total: $0.00");
		
		Button addToCartButton = new Button("Add to Cart");
		Button clearCartButton = new Button("Clear Cart");
		clearCartButton.setOnAction(e -> {
			shoppingCart.clear();
			cartItems.clear(); 
			totalLabel.setText("Total: $0.00");
		});
			   
		addToCartButton.setOnAction(e -> {
			InventoryItem selectedItem = candyListView.getSelectionModel().getSelectedItem();
			if (selectedItem == null) {
		    	showAlert("No Selection,", "Please add candy to your cart.");
		    	return;
		    }
			int quantity = parseQuantity(quantityField.getText());

		    if (quantity <= 0) {
		    	showAlert("Invalid Quantity", "Please enter a valid quantity.");
		    	return;
		    }
		    String candyName = selectedItem.getCandy().getName();
		    int stock = Inventory.getInstance().getStock(candyName);
		    if (quantity > stock) {
		    	showAlert("Not enough in stock", "Only" + stock + " is left in stock.");
		    	return;
		    }
			Inventory.getInstance().removeCandy(candyName, quantity);
			candyListView.refresh();
			
		    CandyPackage packageObj = null;
		    Candy candy = selectedItem.getCandy();
		    		
		    if (boxOption.isSelected()) {
		    	packageObj = new ChocolateBox();
		    	packageObj.pack(candy);
		    	System.out.println(candy.getName() + " packaged in a box.");
		   	} else if (bagOption.isSelected()) {
		   		if (candy instanceof GummyCandy) {
		   			packageObj = new GummyBag();
		   		} else if (candy instanceof HardCandy) {
		   			packageObj = new HardCandyBag();
		   		} else {
		   			packageObj = new CandyBag();
		   			System.out.println("Generic CandyBag for: " + candy.getName());
		   		}
		   		if (packageObj != null) {
		   		packageObj.pack(candy);
		   		System.out.println(candy.getName() + " packaged in a bag.");
		   		}
		   	}else {
		   		System.out.println(candy.getName() + " packaging has not been chosen");
		   	}
		    if (selectedItem.getQuantity() < quantity) {
		    	showAlert("Not enough in stock", "Only" + selectedItem.getQuantity() + " left in stock.");
		    	return;
		    }
		   		   
		   double total = 0.0; 	
		   for (int i = 0; i<quantity; i++) {
			   shoppingCart.addItem(candy, packageObj);
		   }
		   cartItems.clear();
		   totalPrice = 0.0;
			  
			   for (Candy c : shoppingCart.getItems()) {
				   CandyPackage cp = shoppingCart.getPackageFor(c);
				   double itemTotal = c.getPrice() + (cp != null ? cp.getPrice() : 0);
				   totalPrice += itemTotal;
				   
				   String displayText = c.getName();
				   if (cp != null) {
					   displayText += " [" + (cp instanceof ChocolateBox ? "Box" :
                           cp instanceof GummyBag ? "Gummy Bag" :
                           cp instanceof HardCandyBag ? "Hard Candy Bag" :
                           "Unknown") + "]";
				   }
				   displayText += " $" + String.format("%.2f", itemTotal);
				   cartItems.add(c);
				  
			   }
		   
		       totalLabel.setText("Total: $" + String.format("%.2f", totalPrice));
		     
		    //   packageGroup.selectToggle(noPackageOption);
		});
		 
		//Layout
		BorderPane borderPane = new BorderPane();
		
		Region top = new Region();
        top.setPrefHeight(50);
        top.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        Region bottom = new Region();
        bottom.setPrefHeight(50);
        bottom.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        Region left = new Region();
        left.setPrefWidth(50);
        left.setBackground(new Background(new BackgroundFill(Color.PURPLE, CornerRadii.EMPTY, Insets.EMPTY)));

        Region right = new Region();
        right.setPrefWidth(50);
        right.setBackground(new Background(new BackgroundFill(Color.PURPLE, CornerRadii.EMPTY, Insets.EMPTY)));
        
        Button logOutButton = new Button("Log Out");
		logOutButton.setOnAction(e -> {
			clearCart(cartItems, totalLabel); // clears cart when log out
			borderPane.setCenter(loginBox); // goes back to login screen
		});
		HBox packageBox = new HBox(10, packageLabel, boxOption, bagOption);
		packageBox.setAlignment(Pos.CENTER);
		
		Button checkoutButton = new Button("Checkout");
		
		cartBox = new VBox(10,
				 new Label("Available Candy:"), candyListView,
		         quantityField, addToCartButton, clearCartButton,logOutButton, checkoutButton,
		         packageBox, noPackageOption,
		         new Label("Your Cart:"), cartListView, totalLabel);
		cartBox.setPadding(new Insets(20));  
		cartBox.setAlignment(Pos.CENTER);
		    
        borderPane.setTop(top);
		borderPane.setBottom(bottom);
		borderPane.setLeft(left);
		borderPane.setRight(right);
		borderPane.setCenter(loginBox);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(new Customer("mary", "5432"));
		customers.add(new Customer("bob", "password"));
		
		borderPane.setCenter(loginBox); 
        //Handle login
        loginButton.setOnAction(e->{
        	String username = usernameField.getText().trim();
        	String password = passwordField.getText().trim();
        	boolean valid = false;
        	
        	for (Customer c : customers) {
        		if (c.authenticate(username, password)) {
        			messageLabel.setText("Welcome, " + c.getUsername() + "!");
        			messageLabel.setTextFill(Color.DARKGREY);
        			valid = true;
        			
        			CustomerDashboard dashboardScreen = new CustomerDashboard(c,
        					borderPane, cartBox, loginBox);
        			
        			checkoutButton.setOnAction(ev -> processCheckout(cartItems, totalLabel,
        					cartListView, borderPane, loginBox, c,
        			dashboardScreen.getView()
        			));
        			// CartView
        			borderPane.setCenter(dashboardScreen.getView());
        			break;
        		}
        	}
        	if (!valid) {
        		messageLabel.setText("Invalid username or password");
        		messageLabel.setTextFill(Color.RED);
        	}
        });
        
        //Scene setup
        Scene scene = new Scene(borderPane, 400, 300);
		stage.setScene(scene);
		stage.setTitle("Candy Store Login");
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
