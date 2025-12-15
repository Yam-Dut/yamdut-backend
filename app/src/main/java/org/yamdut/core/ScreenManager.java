package org.yamdut.core;


import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private final JFrame frame;
    private final Map<String, JPanel> screens = new HashMap<>();
    
    public ScreenManager(JFrame frame) {
        this.frame = frame;
    }

    public void register(String name, JPanel panel) {
        screens.put(name, panel);
    }

    public void show(String name) {
        JPanel panel = screens.get(name);
        if (panel == null) {
            throw new IllegalArgumentException("Screen not found: " + name);
        }
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
