package org.yamdut.view.dashboard;

import javax.swing.*;
import java.awt.*;

import org.yamdut.utils.Theme;

public class AdminDashboard extends BaseDashboard {
    private JButton manageUsersButton;
    private JButton manageDriversButton;
    private JButton viewReportsButton;
    private JButton systemSettingsButton;

    public AdminDashboard() {
        super();
        initContent();
        setWelcomeMessage("Welcome, Administrator!");
    }

    @Override
    protected void initContent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(titleLabel, gbc);

        // Quick Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        statsPanel.setPreferredSize(new Dimension(600, 100));

        statsPanel.add(createStatCard("Total Users", "0", Theme.COLOR_PRIMARY));
        statsPanel.add(createStatCard("Active Drivers", "0", new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Today's Rides", "0", new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Revenue", "$0.00", new Color(241, 196, 15)));

        contentPanel.add(statsPanel, gbc);

        // Admin Controls Panel
        JLabel controlsLabel = new JLabel("Administration Controls");
        controlsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        controlsLabel.setForeground(Theme.TEXT_PRIMARY);
        gbc.insets = new Insets(30, 0, 20, 0);
        contentPanel.add(controlsLabel, gbc);

        JPanel adminButtonsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        adminButtonsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        adminButtonsPanel.setPreferredSize(new Dimension(500, 200));

        manageUsersButton = createAdminButton("👥 Manage Users", Theme.COLOR_PRIMARY);
        adminButtonsPanel.add(manageUsersButton);

        manageDriversButton = createAdminButton("🚗 Manage Drivers", new Color(52, 152, 219));
        adminButtonsPanel.add(manageDriversButton);

        viewReportsButton = createAdminButton("📊 View Reports", new Color(155, 89, 182));
        adminButtonsPanel.add(viewReportsButton);

        systemSettingsButton = createAdminButton("⚙️ System Settings", new Color(241, 196, 15));
        adminButtonsPanel.add(systemSettingsButton);

        contentPanel.add(adminButtonsPanel, gbc);

        // Recent Activity Panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        activityPanel.setPreferredSize(new Dimension(600, 150));

        JLabel activityLabel = new JLabel("Recent Activity");
        activityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityLabel.setForeground(Theme.TEXT_PRIMARY);

        JTextArea activityTextArea = new JTextArea();
        activityTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activityTextArea.setEditable(false);
        activityTextArea.setText("No recent activity to display.");
        activityTextArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(activityTextArea);
        scrollPane.setBorder(null);

        activityPanel.add(activityLabel, BorderLayout.NORTH);
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        gbc.insets = new Insets(30, 0, 0, 0);
        contentPanel.add(activityPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createAdminButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 80));
        return button;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Theme.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public JButton getManageUsersButton() {
        return manageUsersButton;
    }

    public JButton getManageDriversButton() {
        return manageDriversButton;
    }

    public JButton getViewReportsButton() {
        return viewReportsButton;
    }

    public JButton getSystemSettingsButton() {
        return systemSettingsButton;
    }
}


