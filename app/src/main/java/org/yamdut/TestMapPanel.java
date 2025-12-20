package org.yamdut;

import javax.swing.*;
import org.yamdut.view.components.MapPanel;

public class TestMapPanel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yamdut Map Test");
            frame.setSize(1000, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            
            MapPanel mapPanel = new MapPanel();
            frame.add(mapPanel);
            
            frame.setVisible(true);
        });
    }
}
