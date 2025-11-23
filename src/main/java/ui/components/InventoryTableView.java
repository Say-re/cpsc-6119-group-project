package ui.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.InventoryItem;
import model.StockStatus;
import service.InventoryService;
import ui.components.factory.StockStatusBadgeFactory;
import util.ColorConstants;

import java.io.IOException;
import java.util.Optional;

/**
 * Table view component for displaying and managing inventory items.
 * Provides columns for item details and action buttons for CRUD operations.
 *
 * @author Travis Dagostino
 * @version 1.0
 * @since 2025-11-22
 */
public class InventoryTableView extends TableView<InventoryItem> {

    private final InventoryService inventoryService;
    private final int lowStockThreshold;

    /**
     * Constructs an InventoryTableView with the specified inventory service.
     *
     * @param inventoryService The service for inventory operations
     */
    public InventoryTableView(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.lowStockThreshold = inventoryService.getLowStockThreshold();
        setupColumns();
        loadData();
        getStyleClass().add("data-table");
    }

    /**
     * Sets up the table columns with appropriate cell factories and styling.
     */
    private void setupColumns() {
        // Name Column
        TableColumn<InventoryItem, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(200);
        nameCol.setStyle("-fx-font-weight: bold;");

        // Type Column
        TableColumn<InventoryItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setMinWidth(120);
        typeCol.setCellFactory(col -> new TableCell<InventoryItem, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(capitalizeFirst(type));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Price Column
        TableColumn<InventoryItem, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setMinWidth(100);
        priceCol.setCellFactory(col -> new TableCell<InventoryItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
                setAlignment(Pos.CENTER_RIGHT);
            }
        });

        // Quantity Column
        TableColumn<InventoryItem, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setMinWidth(100);
        quantityCol.setCellFactory(col -> new TableCell<InventoryItem, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty || quantity == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(quantity));
                    setAlignment(Pos.CENTER);

                    // Color code based on stock level
                    StockStatus status = StockStatus.fromQuantity(quantity, lowStockThreshold);
                    if (status == StockStatus.CRITICAL || status == StockStatus.OUT_OF_STOCK) {
                        setStyle("-fx-text-fill: " + ColorConstants.STOCK_CRITICAL_TEXT + "; -fx-font-weight: bold;");
                    } else if (status == StockStatus.LOW) {
                        setStyle("-fx-text-fill: " + ColorConstants.STOCK_LOW_TEXT + "; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: " + ColorConstants.STOCK_IN_STOCK_TEXT + ";");
                    }
                }
            }
        });

        // Stock Status Column
        TableColumn<InventoryItem, Integer> statusCol = new TableColumn<>("Stock Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusCol.setMinWidth(130);
        statusCol.setCellFactory(col -> new TableCell<InventoryItem, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                if (empty || quantity == null) {
                    setGraphic(null);
                } else {
                    setGraphic(StockStatusBadgeFactory.getInstance().createBadgeFromQuantity(quantity, lowStockThreshold));
                }
            }
        });

        // Actions Column
        TableColumn<InventoryItem, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setMinWidth(120);
        actionsCol.setCellFactory(col -> new TableCell<InventoryItem, Void>() {
            private final Button editQtyBtn = new Button("Edit Quantity");

            {
                // Style button
                String buttonStyle =
                    "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 6px; " +
                    "-fx-padding: 6px 12px; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand;";

                editQtyBtn.setStyle(buttonStyle);

                // Set up event handler
                editQtyBtn.setOnAction(e -> {
                    InventoryItem item = getTableView().getItems().get(getIndex());
                    handleEditQuantity(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(8);
                    buttons.setAlignment(Pos.CENTER);
                    buttons.getChildren().add(editQtyBtn);
                    setGraphic(buttons);
                }
            }
        });

        getColumns().addAll(nameCol, typeCol, priceCol, quantityCol, statusCol, actionsCol);
    }

    /**
     * Loads inventory data from the service and displays it in the table.
     */
    public void loadData() {
        ObservableList<InventoryItem> items = FXCollections.observableArrayList(
            inventoryService.getAllItems()
        );
        setItems(items);
    }

    /**
     * Handles editing the quantity of an inventory item.
     *
     * @param item The InventoryItem to edit
     */
    private void handleEditQuantity(InventoryItem item) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(item.getQuantity()));
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText("Edit quantity for: " + item.getName());
        dialog.setContentText("New quantity:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qtyStr -> {
            try {
                int newQty = Integer.parseInt(qtyStr);
                if (newQty < 0) {
                    showError("Invalid quantity", "Quantity cannot be negative.");
                    return;
                }
                item.setQuantity(newQty);
                inventoryService.updateItem(item);
                loadData();
                showSuccess("Quantity updated successfully!");
            } catch (NumberFormatException e) {
                showError("Invalid input", "Please enter a valid number.");
            } catch (IOException e) {
                showError("Error", "Failed to update quantity: " + e.getMessage());
            }
        });
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
