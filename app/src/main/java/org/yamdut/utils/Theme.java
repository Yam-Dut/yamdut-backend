package org.yamdut.utils;

import java.awt.*;

public class Theme {
    // Primary Colors
    public static final Color COLOR_PRIMARY = new Color(0, 122, 255);  // iOS Blue
    public static final Color COLOR_SECONDARY = new Color(52, 199, 89); // Green
    public static final Color COLOR_ACCENT = new Color(255, 149, 0);   // Orange
    
    // Background Colors
    public static final Color BACKGROUND_PRIMARY = new Color(242, 242, 247);
    public static final Color BACKGROUND_SECONDARY = Color.WHITE;
    public static final Color BACKGROUND_CARD = Color.WHITE;
    
    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    public static final Color TEXT_SECONDARY = new Color(142, 142, 147);
    public static final Color TEXT_TERTIARY = new Color(174, 174, 178);
    
    // Border Colors
    public static final Color BORDER_COLOR = new Color(230, 230, 235);
    public static final Color BORDER_FOCUS = new Color(0, 122, 255);
    
    // Status Colors
    public static final Color SUCCESS_COLOR = new Color(52, 199, 89);
    public static final Color WARNING_COLOR = new Color(255, 149, 0);
    public static final Color ERROR_COLOR = new Color(255, 59, 48);
    public static final Color INFO_COLOR = new Color(0, 122, 255);
    
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
