package org.yamdut.view.dashboard;

import javax.swing.*;
import java.awt.*;

import org.yamdut.utils.Theme;

public abstract class BaseDashboard extends JPanel {
    protected JPanel headerPanel;
    protected JLabel welcomeLabel;
    protected JButton logoutButton;

    public BaseDashboard() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_PRIMARY);
        initHeader();
    }

    private void initHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(Theme.COLOR_PRIMARY);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(true);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.COLOR_PRIMARY, 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logoutButton.setBackground(new Color(245, 245, 250));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                logoutButton.setBackground(Color.WHITE);
            }
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    public void setWelcomeMessage(String message) {
        welcomeLabel.setText(message);
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    protected abstract void initContent();
}


