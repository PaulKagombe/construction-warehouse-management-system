package ui.util;

/**
 * Centralized UI constants for consistent theming across the application.
 */
public class UIConstants {

    // Color Palette
    public static final String PRIMARY_COLOR = "#2c3e50";
    public static final String PRIMARY_LIGHT = "#34495e";
    public static final String PRIMARY_DARK = "#1a252f";

    public static final String SUCCESS_COLOR = "#27ae60";
    public static final String SUCCESS_LIGHT = "#229954";

    public static final String DANGER_COLOR = "#e74c3c";
    public static final String DANGER_LIGHT = "#c0392b";

    public static final String WARNING_COLOR = "#f39c12";
    public static final String WARNING_LIGHT = "#d68910";

    public static final String INFO_COLOR = "#3498db";
    public static final String INFO_LIGHT = "#2980b9";

    public static final String SECONDARY_COLOR = "#95a5a6";
    public static final String SECONDARY_LIGHT = "#7f8c8d";

    public static final String BACKGROUND_COLOR = "#f8f9fa";
    public static final String BORDER_COLOR = "#E5E7EB";
    public static final String TEXT_PRIMARY = "#2c3e50";
    public static final String TEXT_SECONDARY = "#34495e";
    public static final String TEXT_LIGHT = "#6B7280";

    public static final String SIDEBAR_BG = "#1E293B";
    public static final String SIDEBAR_HOVER = "#334155";

    // Font Sizes
    public static final int FONT_LARGE_TITLE = 28;
    public static final int FONT_TITLE = 22;
    public static final int FONT_SUBTITLE = 18;
    public static final int FONT_BODY = 14;
    public static final int FONT_SMALL = 12;

    // Button Sizes
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 40;
    public static final int BUTTON_PADDING_H = 20;
    public static final int BUTTON_PADDING_V = 10;

    // Spacing
    public static final int SPACING_SMALL = 5;
    public static final int SPACING_MEDIUM = 10;
    public static final int SPACING_LARGE = 15;
    public static final int SPACING_XL = 20;

    // Border Radius
    public static final int RADIUS_SMALL = 4;
    public static final int RADIUS_MEDIUM = 6;
    public static final int RADIUS_LARGE = 12;

    // Common Styles
    public static String getButtonStyle(String bgColor) {
        return String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-size: 13;
                -fx-background-radius: %d;
                -fx-padding: %d %d;
                -fx-cursor: hand;
                """, bgColor, RADIUS_MEDIUM, BUTTON_PADDING_V, BUTTON_PADDING_H);
    }

    public static String getCardStyle() {
        return String.format("""
                -fx-background-color: white;
                -fx-background-radius: %d;
                -fx-border-radius: %d;
                -fx-border-color: %s;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.12), 8, 0, 0, 2);
                """, RADIUS_LARGE, RADIUS_LARGE, BORDER_COLOR);
    }

    public static String getTextFieldStyle() {
        return """
                -fx-font-size: 12;
                -fx-padding: 8;
                -fx-border-color: #ddd;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                """;
    }
}

