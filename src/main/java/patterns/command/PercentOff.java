package patterns.command;

import model.Order;

public class PercentOff implements DiscountCommand {
    private final double percent; // 0.10 = 10%

    public PercentOff(double percent) { this.percent = percent; }

    @Override public String name() { return Math.round(percent * 100) + "% Off"; }

    @Override public void apply(Order order) {
        order.addDiscount(order.getSubtotal() * percent);
    }
}
