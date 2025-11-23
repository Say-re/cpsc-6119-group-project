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
 * Admin Dashboard for Sweet Factory.
 * Displays inventory, orders, and user statistics with management capabilities.
 * Integrates with BackendFacade for inventory management and observer pattern.
 *
 * @author Travis Dagostino
 * @since 2025-11-22
 */
public class AdminDashboard extends Application {

    // Services and Data Managers
    private DashboardStatsManager statsManager;
    private OrderDataManager orderData;
    private UserManager userManager;
    private InventoryService inventoryService;

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
    private InventoryTableView inventoryTable;
    private UserTableView userTable;

    // Current user
    private UserAccount currentUser;

    /**
     * Default constructor for AdminDashboard.
     * Current user will be set from login.
     */
    public AdminDashboard() {
        this.currentUser = null; // Will be set from login
    }

    /**
     * Constructor with specified user.
     *
     * @param user The currently logged-in user account
     */
    public AdminDashboard(UserAccount user) {
        this.currentUser = user;
    }

    /**
     * Starts the JavaFX application and displays the admin dashboard.
     *
     * @param primaryStage The primary stage for this application
     */
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

    /**
     * Initializes backend services and subscribes to inventory notifications.
     * Sets up BackendFacade, InventoryService, and DashboardStatsManager.
     */
    private void initializeServices() {
        // Initialize backend facade
        BackendFacade.init();
        BackendFacade.setLowStockThreshold(10);

        // Get inventory service from facade
        inventoryService = BackendFacade.getInventoryService();

        // Subscribe to low stock notifications
        BackendFacade.onLowStock((productId, qty) -> {
            System.out.println("⚠️ LOW STOCK ALERT: " + productId + " has only " + qty + " units!");
            // Refresh stats cards when low stock is detected
            javafx.application.Platform.runLater(() -> {
                if (inventoryValueCard != null) inventoryValueCard.refresh();
                if (lowStockCard != null) lowStockCard.refresh();
            });
        });

        orderData = new OrderDataManager();
        CustomerDataManager customerData = new CustomerDataManager();
        userManager = UserManager.getInstance();

        // Build product prices map from inventory data for stats manager
        Map<String, Double> productPrices = new HashMap<>();
        for (model.InventoryItem item : inventoryService.getAllItems()) {
            productPrices.put(item.getName(), item.getPrice());
        }

        statsManager = new DashboardStatsManager(
            orderData, customerData, inventoryService, productPrices
        );
    }

    /**
     * Creates the header section with logo, title, and logout button.
     *
     * @return VBox containing the header components
     */
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

    /**
     * Creates the statistics cards for inventory, low stock, orders, and users.
     *
     * @return HBox containing all stats cards
     */
    private HBox createStatsCards() {
        HBox container = new HBox(16);
        container.setAlignment(Pos.CENTER);

        inventoryValueCard = new StatsCard(
            "Total Inventory Value",
            "📦",
            () -> String.format("$%.2f", inventoryService.getTotalInventoryValue()),
            () -> inventoryService.getAllItems().size() + " products"
        );

        lowStockCard = new StatsCard(
            "Low Stock Alert",
            "⚠️",
            () -> inventoryService.getLowStockItemCount() + " items",
            () -> "Threshold: ≤ 10"
        );

        ordersCard = new StatsCard(
            "Today's Orders",
            "🛒",
            () -> statsManager.getTodaysOrderCount() + " orders",
            () -> "Processing"
        );

        usersCard = new StatsCard(
            "Total Users",
            "👥",
            () -> String.valueOf(userManager.getAllUsers().size()),
            () -> "registered"
        );

        container.getChildren().addAll(inventoryValueCard, lowStockCard, ordersCard, usersCard);
        HBox.setHgrow(inventoryValueCard, Priority.ALWAYS);
        HBox.setHgrow(lowStockCard, Priority.ALWAYS);
        HBox.setHgrow(ordersCard, Priority.ALWAYS);
        HBox.setHgrow(usersCard, Priority.ALWAYS);

        return container;
    }

    /**
     * Creates the tabbed pane with Orders, Inventory, and Users tabs.
     *
     * @return TabPane containing all management tabs
     */
    private TabPane createTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white; -fx-border-radius: 14px; -fx-background-radius: 14px;");

        // Orders Tab
        orderTable = new OrderTableView(statsManager);
        Tab ordersTab = new Tab("Orders", orderTable);
        ordersTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Inventory Tab
        inventoryTable = new InventoryTableView(inventoryService);
        Tab inventoryTab = new Tab("Inventory", inventoryTable);
        inventoryTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Users Tab
        userTable = new UserTableView(userManager);
        Tab usersTab = new Tab("Users", userTable);
        usersTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        tabPane.getTabs().addAll(ordersTab, inventoryTab, usersTab);

        return tabPane;
    }

    /**
     * Handles the logout action.
     * Closes the dashboard and returns to login page.
     */
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

    /**
     * Main method to launch the Admin Dashboard application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
