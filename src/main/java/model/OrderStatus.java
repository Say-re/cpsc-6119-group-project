package model;
import util.ColorConstants;
public enum OrderStatus {
    PLACED("Pending", ColorConstants.STATUS_PENDING_BG, ColorConstants.STATUS_PENDING_TEXT),      // yellow bg, dark text
    PAID("Processing", ColorConstants.STATUS_PROCESSING_BG, ColorConstants.STATUS_PROCESSING_TEXT),      // blue bg, dark text
    PACKING("Processing", ColorConstants.STATUS_PROCESSING_BG, ColorConstants.STATUS_PROCESSING_TEXT),   // blue bg, dark text
    SHIPPED("In Transit", ColorConstants.STATUS_IN_TRANSIT_BG, ColorConstants.STATUS_IN_TRANSIT_TEXT),   // indigo bg, dark text
    DELIVERED("Completed", ColorConstants.STATUS_COMPLETED_BG, ColorConstants.STATUS_COMPLETED_TEXT),  // green bg, dark text
    CANCELLED("Cancelled", ColorConstants.STATUS_CANCELLED_BG, ColorConstants.STATUS_CANCELLED_TEXT);  // red bg, dark text

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
