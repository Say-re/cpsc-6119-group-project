// chatgpt helped me greatly with this class as I have never done an UI other than text based.
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import service.BackendFacade;
import service.BackendFacade.UiCartItem;
import model.InventoryItem;
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

	// Store username passed from LoginPage
	private String externalUsername = null;

	/**
	 * Sets the username from the external login (CustomerDashboard)
	 */
	public void setExternalUsername(String username) {
		this.externalUsername = username;
	}
	//private TextFormatter<Integer> formatter;
	private void clearCart(ObservableList<Candy> cartItems, Label totalLabel) {
		cartItems.clear(); // clears UI list
		shoppingCart.clear(); // clears backend cart
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
    			try {
    				// Convert cart items to BackendFacade format
    				List<UiCartItem> uiCartItems = new ArrayList<>();
    				Map<String, Integer> itemCounts = new java.util.HashMap<>();
    				Map<String, Double> itemPrices = new java.util.HashMap<>();

    				// Aggregate items and include packaging costs
    				for (Candy c : shoppingCart.getItems()) {
    					String name = c.getName();
    					CandyPackage cp = shoppingCart.getPackageFor(c);
    					double itemPrice = c.getPrice() + (cp != null ? cp.getPrice() : 0);

    					itemCounts.put(name, itemCounts.getOrDefault(name, 0) + 1);
    					itemPrices.put(name, itemPrice);
    				}

    				// Create UiCartItem for each unique product
    				for (String name : itemCounts.keySet()) {
    					uiCartItems.add(new UiCartItem(
    						name, // productId
    						name, // name
    						itemCounts.get(name), // quantity
    						itemPrices.get(name) // unit price including packaging
    					));
    				}

    				// Process checkout through BackendFacade
    				model.Order newOrder = BackendFacade.checkout(loggedInCustomer.getUsername(), uiCartItems, null);

    				String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    				String receipt = "Order ID: " + newOrder.getId() + "\nDate: " + timestamp + "\n\n" + sb.toString();

    				Alert info = new Alert(Alert.AlertType.INFORMATION);
    				info.setTitle("Order Placed");
    				info.setHeaderText("Thank you for your order!");
    				info.setContentText(receipt);
    				info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    				info.showAndWait();

    				// Clear cart
    				cartItems.clear();
    				shoppingCart.clear();
    				totalPrice = 0.0;
    				totalLabel.setText("Total: $0.00");
    				borderPane.setCenter(dashboardView);
    			} catch (Exception ex) {
    				showAlert("Checkout Error", "An error occurred during checkout: " + ex.getMessage());
    				ex.printStackTrace();
    			}
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
	    // Candy Store & Inventory - Use BackendFacade for inventory management
		ObservableList<model.InventoryItem> catalogList = FXCollections.observableList(
			BackendFacade.getInventoryService().getAllItems()
		);

		ListView<model.InventoryItem> candyListView = new ListView<>();
		candyListView.setItems(catalogList);
		candyListView.setPrefHeight(200);

		candyListView.setCellFactory(lv -> new ListCell<model.InventoryItem>() {
			@Override
			protected void updateItem(model.InventoryItem item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				}else {
					setText(item.getName() + " - $" + String.format("%.2f", item.getPrice())
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
			model.InventoryItem selectedItem = candyListView.getSelectionModel().getSelectedItem();
			if (selectedItem == null) {
		    	showAlert("No Selection", "Please select candy to add to your cart.");
		    	return;
		    }
			int quantity = parseQuantity(quantityField.getText());

		    if (quantity <= 0) {
		    	showAlert("Invalid Quantity", "Please enter a valid quantity.");
		    	return;
		    }

		    // Check stock using BackendFacade
		    String candyName = selectedItem.getName();
		    int stock = BackendFacade.getInventoryService().getStock(candyName);
		    if (quantity > stock) {
		    	showAlert("Not enough in stock", "Only " + stock + " left in stock.");
		    	return;
		    }

		    // Determine packaging cost
		    double packageCost = 0.0;
		    String packageType = "None";
		    if (boxOption.isSelected()) {
		    	packageCost = 3.00;
		    	packageType = "Box";
		    	System.out.println(candyName + " packaged in a box.");
		   	} else if (bagOption.isSelected()) {
		   		packageCost = 2.00;
		   		packageType = "Bag";
		   		System.out.println(candyName + " packaged in a bag.");
		   	} else {
		   		System.out.println(candyName + " - no packaging chosen");
		   	}

		    // Add items to cart with packaging
		    for (int i = 0; i < quantity; i++) {
		   		// Create candy object for display
		   		Candy candy = new BaseCandy(candyName, selectedItem.getPrice(), 0.0, 1);

		   		// Create package if needed
		   		CandyPackage packageObj = null;
		   		if (boxOption.isSelected()) {
		   			packageObj = new ChocolateBox();
		   			packageObj.pack(candy);
		   		} else if (bagOption.isSelected()) {
		   			packageObj = new CandyBag();
		   			packageObj.pack(candy);
		   		}

		   		shoppingCart.addItem(candy, packageObj);
		    }

		    // Refresh catalog to show updated stock
		    catalogList.setAll(BackendFacade.getInventoryService().getAllItems());

		    // Update cart display
		    cartItems.clear();
		    totalPrice = 0.0;

		    for (Candy c : shoppingCart.getItems()) {
		   		CandyPackage cp = shoppingCart.getPackageFor(c);
		   		double itemTotal = c.getPrice() + (cp != null ? cp.getPrice() : 0);
		   		totalPrice += itemTotal;
		   		cartItems.add(c);
		    }

		    totalLabel.setText("Total: $" + String.format("%.2f", totalPrice));
		    quantityField.clear();
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
			// Close the application instead of returning to login
			stage.close();
			Platform.exit();
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

		// Use the external username if provided from LoginPage, otherwise use a guest
		if (externalUsername != null && !externalUsername.isEmpty()) {
			loggedInCustomer = new Customer(externalUsername, "");
		} else {
			loggedInCustomer = new Customer("guest", "");
		}

		// Create CustomerDashboard with the logged-in customer
		CustomerDashboard dashboardScreen = new CustomerDashboard(loggedInCustomer, borderPane, cartBox, loginBox);

		// Configure checkout button with the logged-in customer and dashboard view
		checkoutButton.setOnAction(ev -> {
			processCheckout(cartItems, totalLabel, cartListView, borderPane, loginBox, loggedInCustomer, dashboardScreen.getView());
		});

		// Display the CustomerDashboard view instead of login
		borderPane.setCenter(dashboardScreen.getView());

		// Comment out login flow - not needed when launched from CustomerDashboard
        // borderPane.setCenter(loginBox);
        // //Handle login
        // loginButton.setOnAction(e->{
        // 	String username = usernameField.getText().trim();
        // 	String password = passwordField.getText().trim();
        // 	boolean valid = false;
        //
        // 	for (Customer c : customers) {
        // 		if (c.authenticate(username, password)) {
        // 			messageLabel.setText("Welcome, " + c.getUsername() + "!");
        // 			messageLabel.setTextFill(Color.DARKGREY);
        // 			valid = true;
        //
        // 			CustomerDashboard dashboardScreen = new CustomerDashboard(c,
        // 					borderPane, cartBox, loginBox);
        //
        // 			checkoutButton.setOnAction(ev -> processCheckout(cartItems, totalLabel,
        // 					cartListView, borderPane, loginBox, c,
        // 			dashboardScreen.getView()
        // 			));
        // 			// CartView
        // 			borderPane.setCenter(dashboardScreen.getView());
        // 			break;
        // 		}
        // 	}
        // 	if (!valid) {
        // 		messageLabel.setText("Invalid username or password");
        // 		messageLabel.setTextFill(Color.RED);
        // 	}
        // });
        
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
