package org.example.view;

import javax.swing.*;
import java.awt.*;

public abstract class BaseView extends JFrame {
    protected static final Color BACKGROUND_SILVER = new Color(240, 242, 245);
    protected static final Color PANEL_WHITE = new Color(255, 255, 255);
    protected static final Color ACCENT_RED = new Color(220, 53, 69);
    protected static final Color ACCENT_BLUE = new Color(70, 130, 180);
    protected static final Color ACCENT_GREEN = new Color(40, 167, 69);
    protected static final Color LIGHT_GRAY = new Color(220, 220, 220);
    protected static final Color BORDER_GRAY = new Color(200, 200, 200);
    protected static final Color TEXT_DARK = new Color(33, 37, 41);
    protected static final Color SIDEBAR_DARK = new Color(45, 50, 60);

    public BaseView(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public abstract void initializeComponents();

    @Override
    public void dispose() {
        super.dispose();
    }
}
