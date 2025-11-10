package ui.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import managers.DashboardStatsManager;
import model.*;
import util.DateFormatter;

import java.time.Instant;

/**
 * Table view for displaying orders in the admin dashboard
 */
public class OrderTableView extends TableView<OrderDisplayModel> {

    private final DashboardStatsManager statsManager;

    public OrderTableView(DashboardStatsManager statsManager) {
        this.statsManager = statsManager;
        setupColumns();
        loadData();
        getStyleClass().add("data-table");
    }

    private void setupColumns() {
        // Order ID Column
        TableColumn<OrderDisplayModel, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        idCol.setMinWidth(280);

        // Customer Column
        TableColumn<OrderDisplayModel, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerCol.setMinWidth(150);

        // Items Column
        TableColumn<OrderDisplayModel, String> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getItemsSummary())
        );
        itemsCol.setMinWidth(200);

        // Total Column
        TableColumn<OrderDisplayModel, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));
        totalCol.setCellFactory(col -> new TableCell<OrderDisplayModel, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
                setAlignment(Pos.CENTER_RIGHT);
            }
        });
        totalCol.setMinWidth(100);

        // Status Column
        TableColumn<OrderDisplayModel, OrderStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<OrderDisplayModel, OrderStatus>() {
            @Override
            protected void updateItem(OrderStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    setGraphic(new StatusBadge(status));
                }
            }
        });
        statusCol.setMinWidth(120);

        // Date Column
        TableColumn<OrderDisplayModel, Instant> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        dateCol.setCellFactory(col -> new TableCell<OrderDisplayModel, Instant>() {
            @Override
            protected void updateItem(Instant instant, boolean empty) {
                super.updateItem(instant, empty);
                if (empty || instant == null) {
                    setText(null);
                } else {
                    setText(DateFormatter.format(instant));
                }
            }
        });
        dateCol.setMinWidth(120);

        getColumns().addAll(idCol, customerCol, itemsCol, totalCol, statusCol, dateCol);
    }

    public void loadData() {
        ObservableList<OrderDisplayModel> orders = FXCollections.observableArrayList(
            statsManager.getOrdersWithCustomerInfo()
        );
        setItems(orders);
    }
}
