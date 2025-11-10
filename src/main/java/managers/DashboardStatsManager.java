package managers;

import model.*;
import repo.*;
import service.*;
import auth.UserManager;

import java.util.*;

/**
 * Aggregates statistics from various services for dashboard display
 * Implements caching to avoid excessive file I/O
 */
public class DashboardStatsManager {
    private final OrderDataManager orderData;
    private final CustomerDataManager customerData;
    private final InventoryService inventoryService;
    private final Map<String, Double> productPrices;

    // Cache with TTL
    private StatsCache cache;
    private static final long CACHE_TTL_MS = 60_000; // 1 minute

    public DashboardStatsManager(OrderDataManager orderData,
                                  CustomerDataManager customerData,
                                  InventoryService inventoryService,
                                  Map<String, Double> productPrices) {
        this.orderData = orderData;
        this.customerData = customerData;
        this.inventoryService = inventoryService;
        this.productPrices = productPrices;
        refreshCache();
    }

    /**
     * Get total inventory value (stock * price for all products)
     */
    public double getTotalInventoryValue() {
        if (cache.isExpired()) refreshCache();
        return cache.totalInventoryValue;
    }

    /**
     * Get count of low stock items
     */
    public int getLowStockItemCount() {
        if (cache.isExpired()) refreshCache();
        return cache.lowStockCount;
    }

    /**
     * Get today's order count
     */
    public int getTodaysOrderCount() {
        if (cache.isExpired()) refreshCache();
        return cache.todaysOrders;
    }

    /**
     * Get active user count (from UserManager)
     */
    public int getActiveUserCount() {
        // Note: This would need to be implemented in UserManager
        // For now, return a placeholder
        return UserManager.getInstance() != null ? 5 : 0;
    }

    /**
     * Get all orders for display
     */
    public List<Order> getAllOrders() {
        return orderData.getAllOrders();
    }

    /**
     * Get orders with customer info enriched for table display
     */
    public List<OrderDisplayModel> getOrdersWithCustomerInfo() {
        List<OrderDisplayModel> result = new ArrayList<>();
        for (Order order : getAllOrders()) {
            Optional<Customer> customer = customerData.findById(order.getCustomerId());
            String customerName = customer.map(Customer::getUsername).orElse("Unknown");
            String customerEmail = customer.map(Customer::getEmail).orElse("");

            result.add(new OrderDisplayModel(order, customerName, customerEmail));
        }
        return result;
    }

    /**
     * Manually trigger cache refresh
     */
    public void refreshData() {
        refreshCache();
    }

    /**
     * Refresh statistics cache
     */
    private void refreshCache() {
        double totalValue = 0.0;
        int lowStockCount = 0;

        for (Map.Entry<String, Double> entry : productPrices.entrySet()) {
            String productId = entry.getKey();
            double price = entry.getValue();
            int stock = inventoryService.getStock(productId);

            totalValue += stock * price;

            // Check if stock is low (using a default threshold of 10)
            if (stock > 0 && stock <= 10) {
                lowStockCount++;
            }
        }

        int todaysOrders = orderData.getTodaysOrders().size();

        cache = new StatsCache(totalValue, lowStockCount, todaysOrders);
    }

    /**
     * Inner cache class with timestamp
     */
    private static class StatsCache {
        final double totalInventoryValue;
        final int lowStockCount;
        final int todaysOrders;
        final long timestamp;

        StatsCache(double totalInventoryValue, int lowStockCount, int todaysOrders) {
            this.totalInventoryValue = totalInventoryValue;
            this.lowStockCount = lowStockCount;
            this.todaysOrders = todaysOrders;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > CACHE_TTL_MS;
        }
    }
}
