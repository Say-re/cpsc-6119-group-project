package patterns.command;

import model.Order;

public class CouponCodeCommand implements DiscountCommand {
    private final String code;
    private final double flatAmount;

    public CouponCodeCommand(String code, double flatAmount) {
        this.code = code; this.flatAmount = flatAmount;
    }

    @Override public String name() { return "Coupon " + code; }

    @Override public void apply(Order order) {
        order.addDiscount(flatAmount);
    }
}
