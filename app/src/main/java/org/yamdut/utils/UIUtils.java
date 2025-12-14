package org.yamdut.utils;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    
    public static void addPadding(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
    
    public static void addMargin(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(top, left, bottom, right),
            component.getBorder()
        ));
    }
    
    public static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }
    
    public static JPanel createFormPanel(JComponent... components) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        for (JComponent component : components) {
            panel.add(component, gbc);
        }
        
        return panel;
    }
    
    public static void makeTransparent(JComponent component) {
        component.setOpaque(false);
        if (component instanceof JPanel) {
            ((JPanel) component).setOpaque(false);
        }
    }
    
    public static void centerOnScreen(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        window.setLocation(x, y);
    }
    
    public static void showLoadingDialog(Component parent, String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(message);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.pack();
        centerOnScreen(dialog);
        
        dialog.setVisible(true);
    }
}