package ui;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import managers.DashboardStatsManager;
import model.OrderStatus;
import patterns.observer.InventorySubject;
import repo.*;
import service.*;
import ui.components.*;
import util.ColorConstants;
import auth.UserManager;
import auth.UserAccount;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin Dashboard for Sweet Factory
 * Displays inventory, orders, and user statistics
 */
public class AdminDashboard extends Application {

    // Services and Data Managers
    private DashboardStatsManager statsManager;
    private OrderDataManager orderData;
    private UserManager userManager;

    // UI Components
    private BorderPane mainLayout;
    private HBox statsCardsContainer;
    private TabPane contentTabs;

    // Stats Cards
    private StatsCard inventoryValueCard;
    private StatsCard lowStockCard;
    private StatsCard ordersCard;
    private StatsCard usersCard;

    // Table Views
    private OrderTableView orderTable;

    // Current user
    private UserAccount currentUser;

    public AdminDashboard() {
        this.currentUser = null; // Will be set from login
    }

    public AdminDashboard(UserAccount user) {
        this.currentUser = user;
    }

    @Override
    public void start(Stage primaryStage) {
        initializeServices();

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + ColorConstants.CANDY_SURFACE + ";");

        // Header
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Content area with stats + tabs
        VBox contentArea = new VBox(24);
        contentArea.setPadding(new Insets(24, 32, 24, 32));

        // Stats Cards
        statsCardsContainer = createStatsCards();

        // Tabs
        contentTabs = createTabs();

        contentArea.getChildren().addAll(statsCardsContainer, contentTabs);
        VBox.setVgrow(contentTabs, Priority.ALWAYS);

        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1400, 900);

        // Load CSS if available
        try {
            scene.getStylesheets().add(getClass().getResource("/admin-dashboard.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found, using inline styles");
        }

        primaryStage.setTitle("Sweet Factory - Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    private void initializeServices() {
        orderData = new OrderDataManager();
        CustomerDataManager customerData = new CustomerDataManager();

        // Initialize inventory service
        InventorySubject inventorySubject = new InventorySubject();
        InventoryService inventoryService = new InventoryService(inventorySubject);

        // Initialize some sample product prices (in real app, load from catalog)
        Map<String, Double> productPrices = new HashMap<>();
        productPrices.put("P001", 2.50);
        productPrices.put("P002", 3.99);
        productPrices.put("P003", 1.99);

        // Set some sample stock levels
        inventoryService.setStock("P001", 150);
        inventoryService.setStock("P002", 8);  // Low stock
        inventoryService.setStock("P003", 200);

        userManager = UserManager.getInstance();

        statsManager = new DashboardStatsManager(
            orderData, customerData, inventoryService, productPrices
        );
    }

    private VBox createHeader() {
        VBox header = new VBox(12);
        header.setPadding(new Insets(16, 32, 16, 32));
        header.setStyle("-fx-background-color: white; -fx-border-color: rgba(0,0,0,0.1); -fx-border-width: 0 0 1 0;");

        HBox headerContent = new HBox(16);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        // Logo + Title
        Label logo = new Label("🍬");
        logo.setStyle("-fx-font-size: 32px;");

        VBox titleBox = new VBox(4);
        Label title = new Label("The Sweet Factory");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";");
        Label subtitle = new Label("Admin Dashboard");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // User info + logout
        String username = (currentUser != null) ? currentUser.getUsername() : "Admin";
        Label userLabel = new Label("Welcome, " + username);
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-font-weight: bold;"
        );
        logoutBtn.setOnAction(e -> handleLogout());

        headerContent.getChildren().addAll(logo, titleBox, spacer, userLabel, logoutBtn);
        header.getChildren().add(headerContent);
        return header;
    }

    private HBox createStatsCards() {
        HBox container = new HBox(16);
        container.setAlignment(Pos.CENTER);

        inventoryValueCard = new StatsCard(
            "Total Inventory Value",
            "📦",
            () -> String.format("$%.2f", statsManager.getTotalInventoryValue()),
            () -> "3 products"
        );

        lowStockCard = new StatsCard(
            "Low Stock Alert",
            "⚠️",
            () -> statsManager.getLowStockItemCount() + " items",
            () -> "Need restock"
        );

        ordersCard = new StatsCard(
            "Today's Orders",
            "🛒",
            () -> statsManager.getTodaysOrderCount() + " orders",
            () -> "Processing"
        );

        usersCard = new StatsCard(
            "Active Users",
            "👥",
            () -> String.valueOf(statsManager.getActiveUserCount()),
            () -> "users"
        );

        container.getChildren().addAll(inventoryValueCard, lowStockCard, ordersCard, usersCard);
        HBox.setHgrow(inventoryValueCard, Priority.ALWAYS);
        HBox.setHgrow(lowStockCard, Priority.ALWAYS);
        HBox.setHgrow(ordersCard, Priority.ALWAYS);
        HBox.setHgrow(usersCard, Priority.ALWAYS);

        return container;
    }

    private TabPane createTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white; -fx-border-radius: 14px; -fx-background-radius: 14px;");

        // Orders Tab
        orderTable = new OrderTableView(statsManager);
        Tab ordersTab = new Tab("Orders", orderTable);
        ordersTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Inventory Tab (placeholder)
        Label inventoryPlaceholder = new Label("Inventory management coming soon...");
        inventoryPlaceholder.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";");
        VBox inventoryBox = new VBox(inventoryPlaceholder);
        inventoryBox.setAlignment(Pos.CENTER);
        inventoryBox.setPadding(new Insets(50));
        Tab inventoryTab = new Tab("Inventory", inventoryBox);

        // Users Tab (placeholder)
        Label usersPlaceholder = new Label("User management coming soon...");
        usersPlaceholder.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";");
        VBox usersBox = new VBox(usersPlaceholder);
        usersBox.setAlignment(Pos.CENTER);
        usersBox.setPadding(new Insets(50));
        Tab usersTab = new Tab("Users", usersBox);

        tabPane.getTabs().addAll(ordersTab, inventoryTab, usersTab);

        return tabPane;
    }

    private void handleLogout() {
        // Close dashboard
        Stage stage = (Stage) mainLayout.getScene().getWindow();
        stage.close();

        // Reopen login page
        try {
            Stage loginStage = new Stage();
            // Note: We can't directly instantiate LoginPage here due to it being in src/main/java
            // This will be handled when integrating with LoginPage
            System.out.println("Logout - returning to login page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
