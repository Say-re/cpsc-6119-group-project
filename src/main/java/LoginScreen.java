
import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginScreen implements UIScreen{
	private VBox loginBox;
	private Map<String, Customer> allCustomers;
	private Map<String, Customer> userDatabase = new HashMap<>();
	
	public LoginScreen(BorderPane borderPane, VBox cartBox) {
		TextField usernameField = new TextField();
		usernameField.setPromptText("Username");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		Button loginButton = new Button("Log In");
		
		allCustomers = new HashMap<>();
		allCustomers.put("mary", new Customer("mary", "mary@gmail.com"));
		allCustomers.put("josh", new Customer("josh", "josh@gmail.com"));
		
		loginButton.setOnAction( e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			
			if(userDatabase.containsKey(username) && userDatabase.get(username).
					getPassword().equals(password)) {
				Customer customer = userDatabase.get(username);
				CustomerDashboard dashboard = new CustomerDashboard(customer, borderPane,
						cartBox, loginBox);
				borderPane.setCenter(dashboard.getView());
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Login Failed");
				alert.setContentText("Incorrect username or password.");
				alert.showAndWait();
			}
		});
		VBox loginBox = new VBox(10, usernameField, passwordField, loginButton);
		loginBox.setAlignment(Pos.CENTER);
		loginBox.setPadding(new Insets(20));
		
	}
@Override
public Pane getView() {
	return loginBox;
}
}
