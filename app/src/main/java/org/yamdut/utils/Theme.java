package org.yamdut.utils;

import java.awt.*;

public class Theme {
    // Primary Colors
    // premium Dark Theme
    public static final Color COLOR_PRIMARY = new Color(0, 122, 255);    // iOS Blue
    public static final Color COLOR_SECONDARY = new Color(192, 192, 192);// Silver
    public static final Color COLOR_ACCENT = new Color(255, 69, 0);      // Orange-Red
    
    // Background Colors
    public static final Color BACKGROUND_PRIMARY = new Color(18, 18, 18); // Deep Black
    public static final Color BACKGROUND_SECONDARY = new Color(30, 30, 30); // Dark Gray
    public static final Color BACKGROUND_CARD = new Color(30, 30, 30);
    
    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(255, 255, 255);    // White
    public static final Color TEXT_SECONDARY = new Color(179, 179, 179);  // Light Gray
    public static final Color TEXT_TERTIARY = new Color(128, 128, 128);   // Gray
    
    // Border Colors
    public static final Color BORDER_COLOR = new Color(50, 50, 50);
    public static final Color BORDER_FOCUS = new Color(0, 122, 255);      // Blue
    
    // Status Colors
    public static final Color SUCCESS_COLOR = new Color(75, 181, 67);     // Green
    public static final Color WARNING_COLOR = new Color(255, 165, 0);     // Orange
    public static final Color ERROR_COLOR = new Color(255, 82, 82);       // Red
    public static final Color INFO_COLOR = new Color(33, 150, 243);       // Blue
    
    // Fonts
    public static Font getTitleFont() {
        return new Font("Segoe UI", Font.BOLD, 28);
    }
    
    public static Font getSubtitleFont() {
        return new Font("Segoe UI", Font.PLAIN, 14);
    }

    public static Font getHeadingFont() {
        return new Font("Segoe UI", Font.BOLD, 18);
    }
    
    public static Font getBodyFont() {
        return new Font("Segoe UI", Font.PLAIN, 14);
    }
    
    public static Font getButtonFont() {
        return new Font("Segoe UI", Font.BOLD, 16);
    }
    
    public static Font getCaptionFont() {
        return new Font("Segoe UI", Font.PLAIN, 12);
    }

    // Spacing
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    public static final int PADDING_XLARGE = 32;
    
    // Border radius
    public static final int BORDER_RADIUS_SMALL = 6;
    public static final int BORDER_RADIUS_MEDIUM = 12;
    public static final int BORDER_RADIUS_LARGE = 20;
    
}
