package ui;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import auth.UserAccount;
import util.ColorConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import model.Order;
import service.BackendFacade;
import service.BackendFacade.UiCartItem;

/**
 * Customer Dashboard for Sweet Factory
 * Provides access to shopping, orders, and profile management
 */
public class CustomerDashboard extends Application {

    // Current user
    private UserAccount currentUser;

    // UI Components
    private BorderPane mainLayout;
    private VBox contentArea;

    // Shopping cart - simplified using model classes
    private List<CartItem> cart = new ArrayList<>();
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private Label totalLabel;

    // Simple product catalog (mock data)
    private static final List<Product> PRODUCT_CATALOG = new ArrayList<>();
    static {
        PRODUCT_CATALOG.add(new Product("CHOC001", "Milk Chocolate Bar", 2.50));
        PRODUCT_CATALOG.add(new Product("CHOC002", "Dark Chocolate Truffle", 3.00));
        PRODUCT_CATALOG.add(new Product("GUMMY001", "Gummy Bears", 1.75));
        PRODUCT_CATALOG.add(new Product("GUMMY002", "Sour Gummy Worms", 2.00));
        PRODUCT_CATALOG.add(new Product("HARD001", "Peppermint Candy", 1.50));
        PRODUCT_CATALOG.add(new Product("HARD002", "Butterscotch Discs", 1.75));
    }

    // Inner classes for simplified shopping
    static class Product {
        final String id;
        final String name;
        final double price;

        Product(String id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return name + " - $" + String.format("%.2f", price);
        }
    }

    static class CartItem {
        final Product product;
        int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        double getTotal() {
            return product.price * quantity;
        }

        @Override
        public String toString() {
            return product.name + " x" + quantity + " - $" + String.format("%.2f", getTotal());
        }
    }

    public CustomerDashboard() {
        this.currentUser = null;
        BackendFacade.init();
    }

    public CustomerDashboard(UserAccount user) {
        this.currentUser = user;
        BackendFacade.init();
    }

    @Override
    public void start(Stage primaryStage) {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + ColorConstants.CANDY_SURFACE + ";");

        // Header
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Main content - Dashboard view
        contentArea = createDashboardView();
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1000, 700);

        // Load CSS if available
        try {
            scene.getStylesheets().add(getClass().getResource("/customer-dashboard.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found, using inline styles");
        }

        primaryStage.setTitle("Sweet Factory - Customer Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    /**
     * Creates the header with branding and user info
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
        Label subtitle = new Label("Sweet treats for every occasion");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // User info + logout
        String username = (currentUser != null) ? currentUser.getUsername() : "Guest";
        Label userLabel = new Label("Welcome, " + username + "!");
        userLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";");

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
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: " + ColorConstants.BG_LIGHT_PINK + "; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-font-weight: bold;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-font-weight: bold;"
        ));
        logoutBtn.setOnAction(e -> handleLogout());

        headerContent.getChildren().addAll(logo, titleBox, spacer, userLabel, logoutBtn);
        header.getChildren().add(headerContent);
        return header;
    }

    /**
     * Creates the main dashboard view with navigation options
     */
    private VBox createDashboardView() {
        VBox dashboard = new VBox(32);
        dashboard.setPadding(new Insets(48, 32, 48, 32));
        dashboard.setAlignment(Pos.TOP_CENTER);

        // Welcome section
        VBox welcomeSection = new VBox(12);
        welcomeSection.setAlignment(Pos.CENTER);
        welcomeSection.setMaxWidth(600);

        Label welcomeTitle = new Label("Welcome to The Sweet Factory!");
        welcomeTitle.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        Label welcomeSubtitle = new Label("What would you like to do today?");
        welcomeSubtitle.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";"
        );

        welcomeSection.getChildren().addAll(welcomeTitle, welcomeSubtitle);

        // Navigation cards
        HBox cardsContainer = new HBox(24);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setMaxWidth(900);

        VBox shopCard = createNavigationCard(
            "🛒",
            "Shop Candy",
            "Browse our delicious selection",
            e -> showShopView()
        );

        VBox ordersCard = createNavigationCard(
            "📦",
            "My Orders",
            "View your order history",
            e -> showOrdersView()
        );

        VBox profileCard = createNavigationCard(
            "👤",
            "My Profile",
            "Manage your account",
            e -> showProfileView()
        );

        cardsContainer.getChildren().addAll(shopCard, ordersCard, profileCard);
        HBox.setHgrow(shopCard, Priority.ALWAYS);
        HBox.setHgrow(ordersCard, Priority.ALWAYS);
        HBox.setHgrow(profileCard, Priority.ALWAYS);

        dashboard.getChildren().addAll(welcomeSection, cardsContainer);

        return dashboard;
    }

    /**
     * Creates a navigation card with icon, title, description and action
     */
    private VBox createNavigationCard(String emoji, String title, String description, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(32));
        card.setMaxWidth(280);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16px; " +
            "-fx-border-radius: 16px; " +
            "-fx-border-color: rgba(0,0,0,0.08); " +
            "-fx-border-width: 1px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);"
        );

        // Icon
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 48px;");

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + "; " +
            "-fx-text-alignment: center;"
        );
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(220);

        // Button
        Button actionButton = new Button("Go");
        actionButton.setMaxWidth(Double.MAX_VALUE);
        actionButton.setStyle(
            "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        );
        actionButton.setOnMouseEntered(e -> actionButton.setStyle(
            "-fx-background-color: #E5527D; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        ));
        actionButton.setOnMouseExited(e -> actionButton.setStyle(
            "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        ));
        actionButton.setOnAction(action);

        card.getChildren().addAll(icon, titleLabel, descLabel, actionButton);

        // Hover effect for the card
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16px; " +
            "-fx-border-radius: 16px; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-effect: dropshadow(gaussian, rgba(255,107,157,0.2), 15, 0, 0, 4);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16px; " +
            "-fx-border-radius: 16px; " +
            "-fx-border-color: rgba(0,0,0,0.08); " +
            "-fx-border-width: 1px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);"
        ));

        return card;
    }

    /**
     * Shows the shop/catalog view
     */
    private void showShopView() {
        VBox shopView = new VBox(24);
        shopView.setPadding(new Insets(32));
        shopView.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Browse Our Candy Selection");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        // Main content area with catalog and cart side by side
        HBox mainContent = new HBox(24);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxWidth(1200);

        // Left side - Candy Catalog
        VBox catalogSection = createCatalogSection();
        HBox.setHgrow(catalogSection, Priority.ALWAYS);

        // Right side - Shopping Cart
        VBox cartSection = createCartSection();
        HBox.setHgrow(cartSection, Priority.ALWAYS);

        mainContent.getChildren().addAll(catalogSection, cartSection);

        Button backButton = createBackButton();

        shopView.getChildren().addAll(title, mainContent, backButton);
        mainLayout.setCenter(shopView);
    }

    /**
     * Creates the candy catalog section
     */
    private VBox createCatalogSection() {
        VBox catalogSection = new VBox(16);
        catalogSection.setMaxWidth(500);
        catalogSection.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 14px; " +
            "-fx-padding: 24px;"
        );

        Label catalogTitle = new Label("Available Candy");
        catalogTitle.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        // Product ListView
        ObservableList<Product> catalogList = FXCollections.observableArrayList(PRODUCT_CATALOG);

        ListView<Product> productListView = new ListView<>();
        productListView.setItems(catalogList);
        productListView.setPrefHeight(250);
        productListView.setStyle("-fx-background-radius: 10px; -fx-border-radius: 10px;");

        productListView.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(product.name + " - $" + String.format("%.2f", product.price));
                    setStyle("-fx-padding: 8px; -fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";");
                }
            }
        });

        // Quantity input
        Label quantityLabel = new Label("Quantity (1-19):");
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter quantity");
        quantityField.setMaxWidth(200);
        quantityField.setStyle(
            "-fx-background-radius: 8px; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 8px;"
        );

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

        // Add to Cart button
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setMaxWidth(Double.MAX_VALUE);
        addToCartButton.setStyle(
            "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        );
        addToCartButton.setOnMouseEntered(e -> addToCartButton.setStyle(
            "-fx-background-color: #E5527D; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        ));
        addToCartButton.setOnMouseExited(e -> addToCartButton.setStyle(
            "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px 24px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        ));
        addToCartButton.setOnAction(e -> handleAddToCart(productListView, quantityField));

        catalogSection.getChildren().addAll(
            catalogTitle,
            productListView,
            quantityLabel,
            quantityField,
            addToCartButton
        );

        return catalogSection;
    }

    /**
     * Creates the shopping cart section
     */
    private VBox createCartSection() {
        VBox cartSection = new VBox(16);
        cartSection.setMaxWidth(500);
        cartSection.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 14px; " +
            "-fx-padding: 24px;"
        );

        Label cartTitle = new Label("Your Shopping Cart");
        cartTitle.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        // Cart ListView
        ListView<CartItem> cartListView = new ListView<>(cartItems);
        cartListView.setPrefHeight(250);
        cartListView.setStyle("-fx-background-radius: 10px; -fx-border-radius: 10px;");

        cartListView.setCellFactory(lv -> new ListCell<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-padding: 8px; -fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";");
                }
            }
        });

        // Total label
        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + ";"
        );

        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);

        Button clearCartButton = new Button("Clear Cart");
        clearCartButton.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + "; " +
            "-fx-border-color: " + ColorConstants.TEXT_SECONDARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-font-weight: bold;"
        );
        clearCartButton.setOnMouseEntered(e -> clearCartButton.setStyle(
            "-fx-background-color: #f5f5f5; " +
            "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + "; " +
            "-fx-border-color: " + ColorConstants.TEXT_SECONDARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-font-weight: bold;"
        ));
        clearCartButton.setOnMouseExited(e -> clearCartButton.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + "; " +
            "-fx-border-color: " + ColorConstants.TEXT_SECONDARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-font-weight: bold;"
        ));
        clearCartButton.setOnAction(e -> handleClearCart());

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle(
            "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        );
        checkoutButton.setOnMouseEntered(e -> checkoutButton.setStyle(
            "-fx-background-color: #E5527D; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        ));
        checkoutButton.setOnMouseExited(e -> checkoutButton.setStyle(
            "-fx-background-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 20px; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-radius: 10px;"
        ));
        checkoutButton.setOnAction(e -> handleCheckout());

        buttonBox.getChildren().addAll(clearCartButton, checkoutButton);

        cartSection.getChildren().addAll(cartTitle, cartListView, totalLabel, buttonBox);

        return cartSection;
    }

    /**
     * Shows the orders view
     */
    private void showOrdersView() {
        VBox ordersView = new VBox(24);
        ordersView.setPadding(new Insets(32));
        ordersView.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("My Order History");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        // Orders container
        VBox ordersContainer = new VBox(16);
        ordersContainer.setAlignment(Pos.TOP_CENTER);
        ordersContainer.setMaxWidth(800);

        // Get orders from BackendFacade
        List<Order> customerOrders = new ArrayList<>();
        if (currentUser != null) {
            customerOrders = BackendFacade.ordersForUser(currentUser.getUsername());
        }

        // Check if customer has orders
        if (customerOrders.isEmpty()) {
            VBox emptyState = new VBox(16);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 14px; " +
                "-fx-padding: 32px;"
            );

            Label noOrders = new Label("You haven't placed any orders yet.");
            noOrders.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";"
            );

            Label suggestion = new Label("Start shopping to see your orders here!");
            suggestion.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";"
            );

            emptyState.getChildren().addAll(noOrders, suggestion);
            ordersContainer.getChildren().add(emptyState);
        } else {
            // Display actual orders (newest first)
            for (int i = customerOrders.size() - 1; i >= 0; i--) {
                Order order = customerOrders.get(i);
                VBox orderCard = createOrderCard(order, customerOrders.size() - i);
                ordersContainer.getChildren().add(orderCard);
            }
        }

        Button backButton = createBackButton();

        ordersView.getChildren().addAll(title, ordersContainer, backButton);
        mainLayout.setCenter(ordersView);
    }

    /**
     * Creates a card displaying order information
     */
    private VBox createOrderCard(Order order, int orderNumber) {
        VBox orderCard = new VBox(12);
        orderCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 14px; " +
            "-fx-padding: 24px; " +
            "-fx-border-color: rgba(0,0,0,0.08); " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 14px;"
        );

        // Order header
        HBox orderHeader = new HBox(16);
        orderHeader.setAlignment(Pos.CENTER_LEFT);

        Label orderIdLabel = new Label("Order " + order.getId());
        orderIdLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        LocalDateTime orderTime = LocalDateTime.ofInstant(order.getCreatedAt(), ZoneId.systemDefault());
        String timestamp = orderTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
        Label dateLabel = new Label(timestamp);
        dateLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";"
        );

        orderHeader.getChildren().addAll(orderIdLabel, spacer, dateLabel);

        // Order items
        VBox itemsBox = new VBox(8);
        itemsBox.setStyle("-fx-padding: 12px 0 0 0;");

        for (model.OrderItem item : order.getItems()) {
            Label itemLabel = new Label("• " + item.getName() + " x" + item.getQty());
            itemLabel.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
            );
            itemsBox.getChildren().add(itemLabel);
        }

        // Total
        Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getGrandTotal()));
        totalLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-padding: 8px 0 0 0;"
        );

        orderCard.getChildren().addAll(orderHeader, itemsBox, totalLabel);

        return orderCard;
    }

    /**
     * Shows the profile view
     */
    private void showProfileView() {
        VBox profileView = new VBox(24);
        profileView.setPadding(new Insets(32));
        profileView.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("My Profile");
        title.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        // Profile info card
        VBox profileCard = new VBox(20);
        profileCard.setAlignment(Pos.TOP_LEFT);
        profileCard.setMaxWidth(600);
        profileCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 14px; " +
            "-fx-padding: 32px;"
        );

        String username = (currentUser != null) ? currentUser.getUsername() : "Guest";
        String email = (currentUser != null) ? currentUser.getEmail() : "N/A";
        String role = (currentUser != null) ? currentUser.getRole() : "Customer";

        VBox usernameSection = createProfileField("Username", username);
        VBox emailSection = createProfileField("Email", email);
        VBox roleSection = createProfileField("Account Type", role);

        profileCard.getChildren().addAll(usernameSection, emailSection, roleSection);

        Button backButton = createBackButton();

        profileView.getChildren().addAll(title, profileCard, backButton);
        mainLayout.setCenter(profileView);
    }

    /**
     * Creates a profile field display
     */
    private VBox createProfileField(String label, String value) {
        VBox field = new VBox(6);

        Label fieldLabel = new Label(label);
        fieldLabel.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";"
        );

        Label fieldValue = new Label(value);
        fieldValue.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";"
        );

        field.getChildren().addAll(fieldLabel, fieldValue);
        return field;
    }

    /**
     * Creates a back button to return to dashboard
     */
    private Button createBackButton() {
        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12px 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
            "-fx-background-color: " + ColorConstants.BG_LIGHT_PINK + "; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12px 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-color: " + ColorConstants.CANDY_PRIMARY + "; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12px 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px;"
        ));
        backButton.setOnAction(e -> {
            mainLayout.setCenter(createDashboardView());
        });

        return backButton;
    }

    /**
     * Handles adding items to cart
     */
    private void handleAddToCart(ListView<Product> productListView, TextField quantityField) {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("No Selection", "Please select a product to add to your cart.");
            return;
        }

        int quantity = parseQuantity(quantityField.getText());
        if (quantity <= 0) {
            showAlert("Invalid Quantity", "Please enter a valid quantity (1-19).");
            return;
        }

        // Check if product already in cart
        CartItem existingItem = cart.stream()
            .filter(item -> item.product.id.equals(selectedProduct.id))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            existingItem.quantity += quantity;
        } else {
            CartItem newItem = new CartItem(selectedProduct, quantity);
            cart.add(newItem);
        }

        // Update cart display
        updateCartDisplay();

        // Clear quantity field
        quantityField.clear();
    }

    /**
     * Updates the cart display with current items and total
     */
    private void updateCartDisplay() {
        cartItems.clear();
        double total = 0.0;

        for (CartItem item : cart) {
            cartItems.add(item);
            total += item.getTotal();
        }

        if (totalLabel != null) {
            totalLabel.setText("Total: $" + String.format("%.2f", total));
        }
    }

    /**
     * Handles clearing the cart
     */
    private void handleClearCart() {
        cart.clear();
        cartItems.clear();
        if (totalLabel != null) {
            totalLabel.setText("Total: $0.00");
        }
    }

    /**
     * Handles checkout process
     */
    private void handleCheckout() {
        if (cart.isEmpty()) {
            showAlert("Cart Empty", "Your cart is empty. Please add items before checking out.");
            return;
        }

        // Calculate total
        double total = cart.stream().mapToDouble(CartItem::getTotal).sum();

        // Create order summary
        Map<String, Integer> summary = new HashMap<>();
        for (CartItem item : cart) {
            summary.merge(item.product.name, item.quantity, Integer::sum);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Order Summary:\n\n");
        summary.forEach((name, qty) -> sb.append(name).append(" x").append(qty).append("\n"));
        sb.append("\nTotal: ").append(String.format("$%.2f", total));
        sb.append("\n\nProceed with checkout?");

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Checkout");
        confirm.setHeaderText("Please confirm that your order is correct");
        confirm.setContentText(sb.toString());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Convert cart to UiCartItem list
                    List<UiCartItem> uiCartItems = new ArrayList<>();
                    for (CartItem item : cart) {
                        uiCartItems.add(new UiCartItem(
                            item.product.id,
                            item.product.name,
                            item.quantity,
                            item.product.price
                        ));
                    }

                    // Process checkout through BackendFacade
                    String userId = (currentUser != null) ? currentUser.getUsername() : "guest";
                    Order newOrder = BackendFacade.checkout(userId, uiCartItems, null);

                    // Create receipt
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String receipt = "Order ID: " + newOrder.getId() + "\nDate: " + timestamp + "\n\n" + sb.toString();

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Order Placed");
                    info.setHeaderText("Thank you for your order!");
                    info.setContentText(receipt);
                    info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    info.showAndWait();

                    // Clear cart
                    cart.clear();
                    cartItems.clear();
                    if (totalLabel != null) {
                        totalLabel.setText("Total: $0.00");
                    }
                } catch (Exception ex) {
                    showAlert("Checkout Error", "An error occurred while processing your order: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * Shows an alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Parses quantity from text field
     */
    private int parseQuantity(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Handles logout action
     */
    private void handleLogout() {
        // Close current dashboard
        Stage stage = (Stage) mainLayout.getScene().getWindow();
        stage.close();

        // Reopen login page
        try {
            Stage loginStage = new Stage();
            // Note: LoginPage is in the default package, not ui package
            // Using reflection to instantiate it
            Class<?> loginPageClass = Class.forName("LoginPage");
            Application loginPage = (Application) loginPageClass.getDeclaredConstructor().newInstance();
            loginPage.start(loginStage);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error returning to login page: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
