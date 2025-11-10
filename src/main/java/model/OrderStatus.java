package model;

public enum OrderStatus {
    PLACED("Pending", "#FEF3C7", "#92400E"),      // yellow bg, dark text
    PAID("Processing", "#DBEAFE", "#1E40AF"),      // blue bg, dark text
    PACKING("Processing", "#DBEAFE", "#1E40AF"),   // blue bg, dark text
    SHIPPED("In Transit", "#E0E7FF", "#3730A3"),   // indigo bg, dark text
    DELIVERED("Completed", "#D1FAE5", "#065F46"),  // green bg, dark text
    CANCELLED("Cancelled", "#FEE2E2", "#991B1B");  // red bg, dark text

    private final String displayName;
    private final String backgroundColor;
    private final String textColor;

    OrderStatus(String displayName, String backgroundColor, String textColor) {
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    public String getDisplayName() { return displayName; }
    public String getBackgroundColor() { return backgroundColor; }
    public String getTextColor() { return textColor; }
}
