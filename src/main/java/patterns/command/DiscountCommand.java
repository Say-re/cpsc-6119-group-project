package patterns.command;

import model.Order;

public interface DiscountCommand {
    String name();
    void apply(Order order);
}
