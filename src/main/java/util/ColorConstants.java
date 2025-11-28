package util;

/**
 * Color constants for the admin dashboard matching the candy factory theme
 */
public class ColorConstants {
    // Candy Factory Brand Colors
    public static final String CANDY_PRIMARY = "#ff6b9d";
    public static final String CANDY_PRIMARY_DARK = "#E5527D";
    public static final String CANDY_PRIMARY_LIGHT = "#FFB3D1";
    public static final String CANDY_SECONDARY = "#C44569";
    public static final String CANDY_ACCENT = "#FFA07A";
    public static final String CANDY_SURFACE = "#FAFAFA";

    // Text Colors
    public static final String TEXT_PRIMARY = "#2C3E50";
    public static final String TEXT_SECONDARY = "#7F8C8D";

    // Background Colors
    public static final String BG_WHITE = "#ffffff";
    public static final String BG_LIGHT_GRAY = "#f3f3f5";
    public static final String BG_LIGHT_PINK = "#FFF0F5";

    // Status Colors (matching OrderStatus)
    // PLACED - Pending (yellow bg, dark text)
    public static final String STATUS_PENDING_BG = "#FEF3C7";
    public static final String STATUS_PENDING_TEXT = "#92400E";

    // PAID & PACKING - Processing (blue bg, dark text)
    public static final String STATUS_PROCESSING_BG = "#DBEAFE";
    public static final String STATUS_PROCESSING_TEXT = "#1E40AF";

    // SHIPPED - In Transit (indigo bg, dark text)
    public static final String STATUS_IN_TRANSIT_BG = "#E0E7FF";
    public static final String STATUS_IN_TRANSIT_TEXT = "#3730A3";

    // DELIVERED - Completed (green bg, dark text)
    public static final String STATUS_COMPLETED_BG = "#D1FAE5";
    public static final String STATUS_COMPLETED_TEXT = "#065F46";

    // CANCELLED - Cancelled (red bg, dark text)
    public static final String STATUS_CANCELLED_BG = "#FEE2E2";
    public static final String STATUS_CANCELLED_TEXT = "#991B1B";

    // Stock Status Colors
    public static final String STOCK_IN_STOCK_BG = "#D1FAE5";
    public static final String STOCK_IN_STOCK_TEXT = "#065F46";

    public static final String STOCK_LOW_BG = "#FEF3C7";
    public static final String STOCK_LOW_TEXT = "#92400E";

    public static final String STOCK_CRITICAL_BG = "#FEE2E2";
    public static final String STOCK_CRITICAL_TEXT = "#991B1B";

    // Border Colors
    public static final String BORDER_LIGHT = "rgba(0, 0, 0, 0.1)";
    public static final String BORDER_MEDIUM = "rgba(0, 0, 0, 0.2)";

    private ColorConstants() {
        // Utility class, prevent instantiation
    }
}
