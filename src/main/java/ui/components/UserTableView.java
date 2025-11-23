package ui.components;

import auth.UserAccount;
import auth.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import util.ColorConstants;

/**
 * Table view component for displaying and managing user accounts.
 * Allows administrators to view users and change their roles between admin and customer.
 *
 * @author Travis Dagostino
 * @version 1.0
 * @since 2025-11-22
 */
public class UserTableView extends TableView<UserAccount> {

    private final UserManager userManager;

    /**
     * Constructs a UserTableView with the specified user manager.
     *
     * @param userManager The UserManager instance for user operations
     */
    public UserTableView(UserManager userManager) {
        this.userManager = userManager;
        setupColumns();
        loadData();
        getStyleClass().add("data-table");
    }

    /**
     * Sets up the table columns with appropriate cell factories and styling.
     */
    private void setupColumns() {
        // Username Column
        TableColumn<UserAccount, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setMinWidth(150);
        usernameCol.setStyle("-fx-font-weight: bold;");

        // Email Column
        TableColumn<UserAccount, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setMinWidth(200);

        // Role Column
        TableColumn<UserAccount, String> roleCol = new TableColumn<>("Current Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setMinWidth(120);
        roleCol.setCellFactory(col -> new TableCell<UserAccount, String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label roleLabel = new Label(capitalizeFirst(role));
                    roleLabel.setStyle(
                        "-fx-background-color: " + getRoleColor(role) + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 4px 12px; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold;"
                    );
                    setGraphic(roleLabel);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Actions Column
        TableColumn<UserAccount, Void> actionsCol = new TableColumn<>("Change Role To");
        actionsCol.setMinWidth(220);
        actionsCol.setCellFactory(col -> new TableCell<UserAccount, Void>() {
            private final Button setAdminBtn = new Button("Admin");
            private final Button setCustomerBtn = new Button("Customer");

            {
                // Style buttons
                String adminBtnStyle =
                    "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 6px; " +
                    "-fx-padding: 6px 12px; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand;";

                String customerBtnStyle =
                    "-fx-background-color: #3B82F6; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 6px; " +
                    "-fx-padding: 6px 12px; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand;";

                setAdminBtn.setStyle(adminBtnStyle);
                setCustomerBtn.setStyle(customerBtnStyle);

                // Set up event handlers
                setAdminBtn.setOnAction(e -> {
                    UserAccount user = getTableView().getItems().get(getIndex());
                    handleRoleChange(user, "admin");
                });

                setCustomerBtn.setOnAction(e -> {
                    UserAccount user = getTableView().getItems().get(getIndex());
                    handleRoleChange(user, "customer");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    UserAccount user = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(8);
                    buttons.setAlignment(Pos.CENTER);

                    // Only show the button for the role the user is NOT currently in
                    if ("customer".equals(user.getRole())) {
                        buttons.getChildren().add(setAdminBtn);
                    } else if ("admin".equals(user.getRole())) {
                        buttons.getChildren().add(setCustomerBtn);
                    }

                    setGraphic(buttons);
                }
            }
        });

        getColumns().addAll(usernameCol, emailCol, roleCol, actionsCol);
    }

    /**
     * Loads user data from the UserManager and displays it in the table.
     */
    public void loadData() {
        ObservableList<UserAccount> users = FXCollections.observableArrayList(
            userManager.getAllUsers()
        );
        setItems(users);
    }

    /**
     * Handles changing a user's role.
     *
     * @param user The UserAccount to update
     * @param newRole The new role to assign
     */
    private void handleRoleChange(UserAccount user, String newRole) {
        // Confirm the change
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Role Change");
        confirmation.setHeaderText("Change role for " + user.getUsername() + "?");
        confirmation.setContentText(
            "Current role: " + capitalizeFirst(user.getRole()) + "\n" +
            "New role: " + capitalizeFirst(newRole) + "\n\n" +
            "This will change the user's permissions."
        );

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (userManager.updateUserRole(user.getUsername(), newRole)) {
                    loadData();
                    showSuccess("Role updated successfully!");
                } else {
                    showError("Error", "Failed to update user role.");
                }
            }
        });
    }

    /**
     * Gets the background color for a role badge.
     *
     * @param role The user role
     * @return The hex color code for the role
     */
    private String getRoleColor(String role) {
        if ("admin".equals(role)) {
            return ColorConstants.CANDY_PRIMARY;
        } else {
            return "#3B82F6"; // Blue for customer
        }
    }

    /**
     * Displays an error alert dialog.
     *
     * @param title The dialog title
     * @param message The error message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a success information dialog.
     *
     * @param message The success message
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * @param str The string to capitalize
     * @return The capitalized string
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
