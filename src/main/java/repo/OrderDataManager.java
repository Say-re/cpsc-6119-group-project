package repo;

import model.*;
import util.CsvUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class OrderDataManager {
    private static final String ORDERS = "orders.csv";
    private static final String ITEMS  = "order_items.csv";

    public void saveOrder(Order order) throws IOException {
        List<String[]> orders = CsvUtil.read(ORDERS);
        orders.removeIf(r -> r.length >= 1 && r[0].equals(order.getId()));
        orders.add(new String[] {
                order.getId(), order.getCustomerId(), order.getCreatedAt().toString(),
                order.getStatus().name(),
                String.format(Locale.US,"%.2f", order.getSubtotal()),
                String.format(Locale.US,"%.2f", order.getDiscountTotal()),
                String.format(Locale.US,"%.2f", order.getGrandTotal())
        });
        CsvUtil.write(ORDERS, orders);

        List<String[]> items = CsvUtil.read(ITEMS);
        items.removeIf(r -> r.length >= 1 && r[0].equals(order.getId()));
        int line = 1;
        for (OrderItem it : order.getItems()) {
            items.add(new String[] {
                    order.getId(), String.valueOf(line++), it.getProductId(),
                    String.valueOf(it.getQty()),
                    String.format(Locale.US,"%.2f", it.getUnitPrice()),
                    it.getName()
            });
        }
        CsvUtil.write(ITEMS, items);
    }

    public Optional<Order> getOrder(String orderId) {
        try {
            for (String[] r : CsvUtil.read(ORDERS)) {
                if (r.length >= 7 && r[0].equals(orderId)) {
                    Order o = new Order(r[0], r[1], Instant.parse(r[2]));
                    o.setStatus(OrderStatus.valueOf(r[3]));
                    loadItemsInto(o);
                    return Optional.of(o);
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    public List<Order> getOrdersByUser(String userId) {
        List<Order> out = new ArrayList<>();
        try {
            for (String[] r : CsvUtil.read(ORDERS)) {
                if (r.length >= 2 && r[1].equals(userId)) {
                    Order o = new Order(r[0], r[1], Instant.parse(r[2]));
                    o.setStatus(OrderStatus.valueOf(r[3]));
                    loadItemsInto(o);
                    out.add(o);
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    private void loadItemsInto(Order o) throws IOException {
        for (String[] r : CsvUtil.read(ITEMS)) {
            if (r.length >= 6 && r[0].equals(o.getId())) {
                int qty = Integer.parseInt(r[3]);
                double price = Double.parseDouble(r[4]);
                String name = r[5];
                o.addItem(new OrderItem(r[2], name, qty, price));
            }
        }
    }

    /**
     * Get all orders from CSV
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            for (String[] r : CsvUtil.read(ORDERS)) {
                if (r.length >= 7) {
                    Order o = new Order(r[0], r[1], Instant.parse(r[2]));
                    o.setStatus(OrderStatus.valueOf(r[3]));
                    loadItemsInto(o);
                    orders.add(o);
                }
            }
        } catch (IOException ignored) {}
        return orders;
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return getAllOrders().stream()
            .filter(o -> o.getStatus() == status)
            .collect(Collectors.toList());
    }

    /**
     * Get orders created after a specific date
     */
    public List<Order> getOrdersAfter(Instant after) {
        return getAllOrders().stream()
            .filter(o -> o.getCreatedAt().isAfter(after))
            .collect(Collectors.toList());
    }

    /**
     * Get today's orders
     */
    public List<Order> getTodaysOrders() {
        Instant startOfToday = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant();
        return getOrdersAfter(startOfToday);
    }
}
