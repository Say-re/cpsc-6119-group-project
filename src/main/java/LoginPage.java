import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.BackendFacade;
import ui.AdminDashboard;
import ui.CustomerDashboard;
import auth.UserManager;
import auth.UserAccount;

/**
 * Login Page for Candy Store Application
 * @description Follows mobile-first design principles with responsive layout
 * Integrates with UserManager for authentication
 */
public class LoginPage extends Application {

    private static final String[] RECOVERY_QUESTIONS = {
        "What was your first pet's name?",
        "What city were you born in?",
        "What is your mother's maiden name?",
        "What was the name of your first school?",
        "What is your favorite book?",
        "What was the make of your first car?",
        "What is your favorite food?"
    };

    private VBox mainContainer;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button createAccountButton;
    private Hyperlink forgotPasswordLink;
    private Label errorLabel;
    private Label usernameErrorLabel;
    private Label passwordErrorLabel;
    private UserManager userManager;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userManager = UserManager.getInstance();

        primaryStage.setTitle("The Sweet Factory - Login");

        // Main container with responsive layout
        mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(40, 30, 40, 30));
        mainContainer.getStyleClass().add("main-container");

        // Create content container with max width
        VBox contentContainer = new VBox();
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setSpacing(32);
        contentContainer.setMaxWidth(448); // max-w-md equivalent

        // Logo/Brand section
        VBox brandSection = createBrandSection();

        // Login form card
        VBox loginCard = createLoginCard();

        // Footer
        Label footer = new Label("© 2025 Sweet Factory. All rights reserved.");
        footer.getStyleClass().add("footer-text");
        footer.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 12px;");

        contentContainer.getChildren().addAll(brandSection, loginCard, footer);
        mainContainer.getChildren().add(contentContainer);

        // Create scene with mobile-first dimensions
        Scene scene = new Scene(mainContainer, 450, 700);

        // Load CSS stylesheet
        try {
            scene.getStylesheets().add(getClass().getResource("login-styles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS file");
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(320);
        primaryStage.setMinHeight(568);
        primaryStage.show();
    }

    /**
     * Creates the brand/logo section at the top
     */
    private VBox createBrandSection() {
        VBox brandBox = new VBox();
        brandBox.setAlignment(Pos.CENTER);
        brandBox.setSpacing(16);

        // Logo icon in circular container
        StackPane logoContainer = new StackPane();
        logoContainer.setStyle(
            "-fx-background-color: #ff6b9d;" +
            "-fx-background-radius: 50px;" +
            "-fx-min-width: 64px;" +
            "-fx-min-height: 64px;" +
            "-fx-max-width: 64px;" +
            "-fx-max-height: 64px;"
        );

        Text logoIcon = new Text("🍬");
        logoIcon.setStyle("-fx-font-size: 32px;");
        logoContainer.getChildren().add(logoIcon);

        // App title
        Label appTitle = new Label("The Sweet Factory");
        appTitle.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2C3E50;"
        );

        brandBox.getChildren().addAll(logoContainer, appTitle);
        return brandBox;
    }

    /**
     * Creates the login card with form
     */
    private VBox createLoginCard() {
        VBox card = new VBox();
        card.setAlignment(Pos.TOP_LEFT);
        card.setSpacing(16);
        card.setPadding(new Insets(32, 32, 32, 32));
        card.getStyleClass().add("form-container");
        card.setMaxWidth(448);

        // Title
        Label cardTitle = new Label("Welcome Back");
        cardTitle.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2C3E50;"
        );

        // Username field with error
        VBox usernameBox = createInputFieldWithError("Username", false);
        usernameField = (TextField) ((VBox) usernameBox.getChildren().get(1)).getUserData();
        usernameErrorLabel = (Label) usernameBox.getChildren().get(2);

        // Password field with error
        VBox passwordBox = createInputFieldWithError("Password", true);
        passwordField = (PasswordField) ((VBox) passwordBox.getChildren().get(1)).getUserData();
        passwordErrorLabel = (Label) passwordBox.getChildren().get(2);

        // Forgot password link
        forgotPasswordLink = new Hyperlink("Forgot password?");
        forgotPasswordLink.getStyleClass().add("forgot-password-link");
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());

        HBox forgotPasswordBox = new HBox(forgotPasswordLink);
        forgotPasswordBox.setAlignment(Pos.CENTER_RIGHT);

        // Sign In button
        loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("login-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());

        // Divider with text
        VBox divider = createDivider("New to Sweet Factory?");

        // Create Account button
        createAccountButton = new Button("Create New Account");
        createAccountButton.getStyleClass().add("create-account-button");
        createAccountButton.setMaxWidth(Double.MAX_VALUE);
        createAccountButton.setOnAction(e -> handleSignUp());

        card.getChildren().addAll(
            cardTitle,
            usernameBox,
            passwordBox,
            forgotPasswordBox,
            loginButton,
            divider,
            createAccountButton
        );

        return card;
    }

    /**
     * Create a divider with centered text
     */
    private VBox createDivider(String text) {
        VBox dividerBox = new VBox();
        dividerBox.setAlignment(Pos.CENTER);
        dividerBox.setPadding(new Insets(8, 0, 8, 0));

        // Top line
        Separator topLine = new Separator();
        topLine.setMaxWidth(Double.MAX_VALUE);

        // Text
        Label dividerText = new Label(text);
        dividerText.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #7F8C8D;" +
            "-fx-padding: 8px 0;"
        );

        dividerBox.getChildren().addAll(topLine, dividerText);
        return dividerBox;
    }

    /**
     * Creates an input field with label and error message
     */
    private VBox createInputFieldWithError(String labelText, boolean isPassword) {
        VBox fieldBox = new VBox();
        fieldBox.setSpacing(8);

        // Label
        Label label = new Label(labelText);
        label.getStyleClass().add("input-label");

        // Input field container
        VBox inputContainer = new VBox();
        Control inputControl;

        if (isPassword) {
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Enter your " + labelText.toLowerCase());
            passwordField.getStyleClass().add("input-field");
            inputControl = passwordField;
            inputContainer.getChildren().add(passwordField);
            inputContainer.setUserData(passwordField); // Store reference

            // Clear error on input
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.trim().isEmpty()) {
                    clearFieldError(passwordField);
                }
            });
        } else {
            TextField textField = new TextField();
            textField.setPromptText("Enter your " + labelText.toLowerCase());
            textField.getStyleClass().add("input-field");
            inputControl = textField;
            inputContainer.getChildren().add(textField);
            inputContainer.setUserData(textField); // Store reference

            // Clear error on input
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.trim().isEmpty()) {
                    clearFieldError(textField);
                }
            });
        }

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle(
            "-fx-text-fill: #E74C3C;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 4px 0 0 0;"
        );
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        fieldBox.getChildren().addAll(label, inputContainer, errorLabel);
        return fieldBox;
    }

    /**
     * Clear field error styling
     */
    private void clearFieldError(Control field) {
        field.setStyle(field.getStyle().replace("-fx-border-color: #E74C3C;", ""));
    }

    /**
     * Handles login button click with authentication
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Clear previous errors
        hideFieldError(usernameErrorLabel);
        hideFieldError(passwordErrorLabel);
        clearFieldError(usernameField);
        clearFieldError(passwordField);

        // Validate inputs
        boolean hasErrors = false;

        if (username.isEmpty()) {
            showFieldError(usernameErrorLabel, "Username is required", usernameField);
            hasErrors = true;
        }

        if (password.isEmpty()) {
            showFieldError(passwordErrorLabel, "Password is required", passwordField);
            hasErrors = true;
        }

        if (hasErrors) {
            return;
        }

        // Attempt authentication
        UserAccount account = userManager.authenticate(username, password);

       if (account != null) {
    // Successful login
    System.out.println("Login successful for user: " + username + " (Role: " + account.getRole() + ")");

    // Initialize backend services once the user is known
    BackendFacade.init();

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Login Successful");
    alert.setHeaderText(null);
    String welcomeMessage = "Welcome, " + username + "!\n\n" +
        "Role: " + account.getRole() + "\n" +
        "Email: " + account.getEmail() + "\n\n" +
        "You would now be redirected to the main application.";
    alert.setContentText(welcomeMessage);
    styleDialog(alert.getDialogPane(), "Login Successful", welcomeMessage);
    alert.showAndWait();

    // Transition to main application based on role
    if ("admin".equalsIgnoreCase(account.getRole())) {
        showAdminDashboard(account);
    } else {
        showCustomerDashboard(account);
    }
} else {
    // Failed login
    showFieldError(passwordErrorLabel, "Invalid username or password", passwordField);
    usernameField.setStyle(usernameField.getStyle() + "-fx-border-color: #E74C3C; -fx-border-width: 2px;");
    passwordField.setStyle(passwordField.getStyle() + "-fx-border-color: #E74C3C; -fx-border-width: 2px;");
}
    }


    /**
     * Handles forgot password link click with recovery question flow
     */
    private void handleForgotPassword() {
        System.out.println("Forgot password clicked");
    
        // Step 1: Ask for username
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Password Recovery");
        usernameDialog.setHeaderText(null);

        // Style the dialog
        styleDialog(usernameDialog.getDialogPane(), "Reset Password - Step 1 of 3", "Enter your username to begin password recovery");

        usernameDialog.showAndWait().ifPresent(username -> {
            UserAccount account = userManager.getUser(username);

            if (account == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("User Not Found");
                alert.setHeaderText(null);
                String errorMsg = "No account found with username: " + username;
                alert.setContentText(errorMsg);

                // Style the alert
                styleDialog(alert.getDialogPane(), "User Not Found", errorMsg);

                alert.showAndWait();
                return;
            }

            if (account.getRecoveryQuestion() == null || account.getRecoveryQuestion().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Recovery Question");
                alert.setHeaderText(null);
                String errorMsg = "This account does not have a recovery question set up.\nPlease contact an administrator for assistance.";
                alert.setContentText(errorMsg);

                // Style the alert
                styleDialog(alert.getDialogPane(), "No Recovery Question", errorMsg);

                alert.showAndWait();
                return;
            }

            // Step 2: Show recovery question and ask for answer
            TextInputDialog answerDialog = new TextInputDialog();
            answerDialog.setTitle("Password Recovery");
            answerDialog.setHeaderText(null);

            // Style the dialog
            styleDialog(answerDialog.getDialogPane(), "Reset Password - Step 2 of 3",
                       "Security Question:\n" + account.getRecoveryQuestion() + "\n\nPlease provide your answer:");

            answerDialog.showAndWait().ifPresent(answer -> {
                // Validate answer
                if (userManager.validateRecoveryAnswer(username, answer)) {
                    // Step 3: Ask for new password
                    Dialog<String> passwordDialog = new Dialog<>();
                    passwordDialog.setTitle("Password Recovery");
                    passwordDialog.setHeaderText(null);

                    ButtonType resetButtonType = new ButtonType("Reset Password", ButtonBar.ButtonData.OK_DONE);
                    passwordDialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

                    VBox passwordBox = new VBox(16);
                    passwordBox.setPadding(new Insets(20, 30, 20, 30));

                    Label newPasswordLabel = new Label("New Password");
                    newPasswordLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
                    PasswordField newPasswordField = new PasswordField();
                    newPasswordField.setPromptText("Enter new password (min 6 characters)");
                    newPasswordField.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 12px; -fx-font-size: 15px;");

                    Label confirmPasswordLabel = new Label("Confirm Password");
                    confirmPasswordLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
                    PasswordField confirmPasswordField = new PasswordField();
                    confirmPasswordField.setPromptText("Confirm new password");
                    confirmPasswordField.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 12px; -fx-font-size: 15px;");

                    passwordBox.getChildren().addAll(
                        newPasswordLabel, newPasswordField,
                        confirmPasswordLabel, confirmPasswordField
                    );

                    passwordDialog.getDialogPane().setContent(passwordBox);

                    // Style the dialog
                    styleDialog(passwordDialog.getDialogPane(), "Reset Password - Step 3 of 3", "Enter your new password");

                    passwordDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == resetButtonType) {
                            String newPassword = newPasswordField.getText();
                            String confirmPassword = confirmPasswordField.getText();

                            if (newPassword.isEmpty()) {
                                showValidationError("New password is required!");
                                return null;
                            }

                            if (newPassword.length() < 6) {
                                showValidationError("Password must be at least 6 characters!");
                                return null;
                            }

                            if (!newPassword.equals(confirmPassword)) {
                                showValidationError("Passwords do not match!");
                                return null;
                            }

                            // Update password
                            if (userManager.updatePassword(username, newPassword)) {
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Success");
                                successAlert.setHeaderText(null);
                                String successMsg = "Your password has been updated.\n\n" +
                                    "You can now log in with your new password.";
                                successAlert.setContentText(successMsg);

                                // Style the alert
                                styleDialog(successAlert.getDialogPane(), "Password Reset Successfully!", successMsg);

                                successAlert.showAndWait();
                            } else {
                                showValidationError("Failed to update password. Please try again.");
                            }
                        }
                        return null;
                    });

                    passwordDialog.showAndWait();
                } else {
                    // Incorrect answer
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Incorrect Answer");
                    alert.setHeaderText(null);
                    String errorMsg = "The answer you provided is incorrect.\nPlease try again or contact an administrator.";
                    alert.setContentText(errorMsg);

                    // Style the alert
                    styleDialog(alert.getDialogPane(), "Incorrect Answer", errorMsg);

                    alert.showAndWait();
                }
            });
        });
    }

    /**
     * Handles sign up / create account button click
     */
    private void handleSignUp() {
        System.out.println("Sign up clicked");

        // Create signup dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Join Sweet Factory");
        dialog.setHeaderText(null);

        // Set button types
        ButtonType createButtonType = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create form using VBox for vertical layout
        VBox formBox = new VBox(16);
        formBox.setPadding(new Insets(20, 30, 20, 30));
        formBox.setMaxWidth(400);

        // Username field
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
        TextField newUsername = new TextField();
        newUsername.setPromptText("Choose a username");
        newUsername.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 12px; -fx-font-size: 15px;");
        VBox usernameBox = new VBox(8, usernameLabel, newUsername);

        // Password field
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Create a password (min 6 characters)");
        newPassword.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 12px; -fx-font-size: 15px;");
        VBox passwordBox = new VBox(8, passwordLabel, newPassword);

        // Confirm Password field
        Label confirmLabel = new Label("Confirm Password");
        confirmLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm your password");
        confirmPassword.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 12px; -fx-font-size: 15px;");
        VBox confirmBox = new VBox(8, confirmLabel, confirmPassword);

        // Email field (kept for account management)
        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
        TextField email = new TextField();
        email.setPromptText("your.email@example.com");
        email.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 12px; -fx-font-size: 15px;");
        VBox emailBox = new VBox(8, emailLabel, email);

        // Recovery Question dropdown
        Label questionLabel = new Label("Recovery Question");
        questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
        ComboBox<String> questionCombo = new ComboBox<>();
        questionCombo.getItems().addAll(RECOVERY_QUESTIONS);
        questionCombo.setPromptText("Select a security question");
        questionCombo.setMaxWidth(Double.MAX_VALUE);
        questionCombo.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 8px; -fx-font-size: 15px;");
        VBox questionBox = new VBox(8, questionLabel, questionCombo);

        // Recovery Answer field
        Label answerLabel = new Label("Answer");
        answerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #252525;");
        TextField recoveryAnswer = new TextField();
        recoveryAnswer.setPromptText("Your answer");
        recoveryAnswer.setStyle("-fx-background-color: #f3f3f5; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 12px; -fx-font-size: 15px;");
        VBox answerBox = new VBox(8, answerLabel, recoveryAnswer);

        formBox.getChildren().addAll(usernameBox, passwordBox, confirmBox, emailBox, questionBox, answerBox);

        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background: #ffffff; -fx-border-color: transparent;");
        dialog.getDialogPane().setContent(scrollPane);

        // Style the dialog
        styleDialog(dialog.getDialogPane(), "Create Your Account", "Join Sweet Factory and start shopping!");

        // Handle create button
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String username = newUsername.getText().trim();
                String password = newPassword.getText();
                String confirmPwd = confirmPassword.getText();
                String emailText = email.getText().trim();
                String question = questionCombo.getValue();
                String answer = recoveryAnswer.getText().trim();

                // Validation
                if (username.isEmpty()) {
                    showValidationError("Username is required!");
                    return null;
                }

                if (username.length() < 3) {
                    showValidationError("Username must be at least 3 characters!");
                    return null;
                }

                if (password.isEmpty()) {
                    showValidationError("Password is required!");
                    return null;
                }

                if (password.length() < 6) {
                    showValidationError("Password must be at least 6 characters!");
                    return null;
                }

                if (confirmPwd.isEmpty()) {
                    showValidationError("Please confirm your password!");
                    return null;
                }

                if (!password.equals(confirmPwd)) {
                    showValidationError("Passwords do not match!");
                    return null;
                }

                if (emailText.isEmpty()) {
                    showValidationError("Email is required!");
                    return null;
                }

                if (question == null || question.isEmpty()) {
                    showValidationError("Please select a recovery question!");
                    return null;
                }

                if (answer.isEmpty()) {
                    showValidationError("Recovery answer is required!");
                    return null;
                }

                // Create account (always as customer role)
                if (userManager.createUser(username, password, "customer", emailText, question, answer)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    String successMsg = "Your account has been created successfully!\n\n" +
                        "Username: " + username + "\n" +
                        "Email: " + emailText + "\n\n" +
                        "You can now log in.";
                    alert.setContentText(successMsg);

                    // Style the alert
                    styleDialog(alert.getDialogPane(), "Account Created!", successMsg);

                    alert.showAndWait();
                } else {
                    showValidationError("Username already exists!");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Style a dialog to match the login page design
     * TODO: Move styles into css file instead of being hard coded
     */
    private void styleDialog(DialogPane dialogPane, String title, String description) {
        // Style the dialog pane background
        dialogPane.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-border-color: rgba(0, 0, 0, 0.1); " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 14px; " +
            "-fx-background-radius: 14px; " +
            "-fx-padding: 20px;"
        );

        // Create custom header
        VBox header = new VBox(8);
        header.setPadding(new Insets(0, 0, 16, 0));

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2C3E50;"
        );

        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #7F8C8D; " +
            "-fx-wrap-text: true;"
        );
        descLabel.setMaxWidth(400);
        descLabel.setWrapText(true);

        header.getChildren().addAll(titleLabel, descLabel);

        // Insert header at the top of the content
        if (dialogPane.getContent() != null) {
            VBox wrapper = new VBox(16);
            wrapper.getChildren().addAll(header, dialogPane.getContent());
            dialogPane.setContent(wrapper);
        } else {
            dialogPane.setContent(header);
        }

        // Style buttons
        dialogPane.lookupButton(ButtonType.OK);
        dialogPane.lookupButton(ButtonType.CANCEL);

        // Apply button styling after dialog is shown
        javafx.application.Platform.runLater(() -> {
            dialogPane.lookupAll(".button").forEach(node -> {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    String buttonText = btn.getText();

                    if (buttonText != null && (buttonText.equals("OK") ||
                        buttonText.equals("Create Account") ||
                        buttonText.equals("Reset Password"))) {
                        // Primary button style (pink)
                        btn.setMinWidth(100);
                        btn.setStyle(
                            "-fx-background-color: #ff6b9d; " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-font-size: 15px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 12px 24px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-min-width: 100px; " +
                            "-fx-cursor: hand;"
                        );

                        btn.setOnMouseEntered(e -> btn.setStyle(
                            "-fx-background-color: #E5527D; " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-font-size: 15px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 12px 24px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-min-width: 100px; " +
                            "-fx-cursor: hand;"
                        ));

                        btn.setOnMouseExited(e -> btn.setStyle(
                            "-fx-background-color: #ff6b9d; " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-font-size: 15px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 12px 24px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-min-width: 100px; " +
                            "-fx-cursor: hand;"
                        ));
                    } else if (buttonText != null && buttonText.equals("Cancel")) {
                        // Secondary button style (outlined)
                        btn.setMinWidth(100);
                        btn.setStyle(
                            "-fx-background-color: #ffffff; " +
                            "-fx-text-fill: #ff6b9d; " +
                            "-fx-font-size: 15px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 12px 24px; " +
                            "-fx-border-color: #ff6b9d; " +
                            "-fx-border-width: 2px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-min-width: 100px; " +
                            "-fx-cursor: hand;"
                        );

                        btn.setOnMouseEntered(e -> btn.setStyle(
                            "-fx-background-color: #FFF0F5; " +
                            "-fx-text-fill: #ff6b9d; " +
                            "-fx-font-size: 15px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 12px 24px; " +
                            "-fx-border-color: #ff6b9d; " +
                            "-fx-border-width: 2px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-min-width: 100px; " +
                            "-fx-cursor: hand;"
                        ));

                        btn.setOnMouseExited(e -> btn.setStyle(
                            "-fx-background-color: #ffffff; " +
                            "-fx-text-fill: #ff6b9d; " +
                            "-fx-font-size: 15px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 12px 24px; " +
                            "-fx-border-color: #ff6b9d; " +
                            "-fx-border-width: 2px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-min-width: 100px; " +
                            "-fx-cursor: hand;"
                        ));
                    }
                }
            });
        });
    }

    /**
     * Show validation error alert
     */
    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        styleDialog(alert.getDialogPane(), "Validation Error", message);

        alert.showAndWait();
    }

    /**
     * Show field error
     */
    private void showFieldError(Label errorLabel, String message, Control field) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        field.setStyle(field.getStyle() + "-fx-border-color: #E74C3C; -fx-border-width: 2px;");
    }

    /**
     * Hide field error
     */
  /**
 * Hide field error
 */
private void hideFieldError(Label errorLabel) {
    errorLabel.setVisible(false);
    errorLabel.setManaged(false);
}

/**
 * Redirects admin users to the Admin Dashboard
 */
private void showAdminDashboard(UserAccount account) {
    try {
        // Create AdminDashboard instance with current user
        AdminDashboard dashboard = new AdminDashboard(account);

        // Reuse the same stage the login page is using
        Stage stage = (Stage) mainContainer.getScene().getWindow();

        // Start the admin dashboard UI on that stage
        dashboard.start(stage);

        stage.setTitle("Sweet Factory — Admin Dashboard");
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(
                Alert.AlertType.ERROR,
                "Could not open Admin Dashboard:\n" + e.getMessage(),
                ButtonType.OK
        );
        alert.showAndWait();
    }
}

/**
 * Redirects customer users to the Customer Dashboard
 */
private void showCustomerDashboard(UserAccount account) {
    try {
        // Create CustomerDashboard instance with current user
        CustomerDashboard dashboard = new CustomerDashboard(account);

        // Reuse the same stage the login page is using
        Stage stage = (Stage) mainContainer.getScene().getWindow();

        // Start the customer dashboard UI on that stage
        dashboard.start(stage);

        stage.setTitle("Sweet Factory — Customer Dashboard");
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(
                Alert.AlertType.ERROR,
                "Could not open Customer Dashboard:\n" + e.getMessage(),
                ButtonType.OK
        );
        alert.showAndWait();
    }
}

public static void main(String[] args) {
    launch(args);
}
}

