package org.yamdut.view.dashboard;

//preeti patch
import java.awt.BorderLayout;
import java.awt.Color;
// removed unused import
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.yamdut.utils.Theme;
import org.yamdut.controller.AdminDashboardController;
import org.yamdut.view.components.ModernButton;

public class AdminDashboard extends BaseDashboard {
    private AdminDashboardController controller; // Keep reference to prevent GC
    private ModernButton manageUsersButton;
    private ModernButton manageDriversButton;
    private ModernButton viewReportsButton;
    private ModernButton systemSettingsButton;
    private ModernButton refreshButton;
    private JTextArea activityTextArea;

    // Stat card value labels for refreshing
    private JLabel totalUsersLabel;
    private JLabel activeDriversLabel;
    private JLabel todaysRidesLabel;
    private JLabel revenueLabel;

    public AdminDashboard() {
        super();
        initContent();
        setWelcomeMessage("Welcome, Administrator!");
    }

    @Override
    protected void initContent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(Theme.getTitleFont());
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.insets = new Insets(0, 0, 40, 0);
        contentPanel.add(titleLabel, gbc);

        // Quick Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        statsPanel.setPreferredSize(new Dimension(600, 100));

        statsPanel.add(createStatCard("Users", "0", Theme.COLOR_PRIMARY, totalUsersLabel = new JLabel()));
        statsPanel.add(createStatCard("Drivers", "0", new Color(46, 204, 113), activeDriversLabel = new JLabel()));
        statsPanel.add(createStatCard("Revenue", "Rs. 0", new Color(241, 196, 15), revenueLabel = new JLabel()));

        contentPanel.add(statsPanel, gbc);

        // Admin Controls Panel header with refresh button
        JPanel controlsHeader = new JPanel(new BorderLayout());
        controlsHeader.setOpaque(false);

        JLabel controlsLabel = new JLabel("Administration Controls");
        controlsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        controlsLabel.setForeground(Theme.TEXT_PRIMARY);

        refreshButton = new ModernButton("Refresh", Theme.COLOR_PRIMARY);
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setPreferredSize(new Dimension(100, 30));

        controlsHeader.add(controlsLabel, BorderLayout.WEST);
        controlsHeader.add(refreshButton, BorderLayout.EAST);

        gbc.insets = new Insets(30, 0, 20, 0);
        contentPanel.add(controlsHeader, gbc);

        JPanel adminButtonsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        adminButtonsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        adminButtonsPanel.setPreferredSize(new Dimension(500, 200));

        manageUsersButton = createAdminButton("Manage Users", Theme.COLOR_PRIMARY);
        adminButtonsPanel.add(manageUsersButton);

        manageDriversButton = createAdminButton("Manage Drivers", new Color(52, 152, 219));
        adminButtonsPanel.add(manageDriversButton);

        viewReportsButton = createAdminButton("View Reports", new Color(155, 89, 182));
        adminButtonsPanel.add(viewReportsButton);

        systemSettingsButton = createAdminButton("System Settings", new Color(241, 196, 15));
        adminButtonsPanel.add(systemSettingsButton);

        contentPanel.add(adminButtonsPanel, gbc);

        // Recent Activity Panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        activityPanel.setPreferredSize(new Dimension(600, 150));

        JLabel activityLabel = new JLabel("Recent Activity");
        activityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityLabel.setForeground(Theme.TEXT_PRIMARY);

        activityTextArea = new JTextArea();
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

        // Initialize controller
        this.controller = new AdminDashboardController(this);
    }

    private ModernButton createAdminButton(String text, Color color) {
        ModernButton button = new ModernButton(text, color);
        button.setPreferredSize(new Dimension(200, 80));
        return button;
    }

    private JPanel createStatCard(String title, String value, Color color, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Theme.BACKGROUND_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Theme.TEXT_SECONDARY);
        titleLabel.setOpaque(false);

        valueLabel.setText(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setOpaque(false);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // Add a subtle bottom border accent
        JPanel accent = new JPanel();
        accent.setBackground(color);
        accent.setPreferredSize(new Dimension(0, 4));
        card.add(accent, BorderLayout.SOUTH);

        return card;
    }

    public ModernButton getManageUsersButton() {
        return manageUsersButton;
    }

    public ModernButton getManageDriversButton() {
        return manageDriversButton;
    }

    public ModernButton getViewReportsButton() {
        return viewReportsButton;
    }

    public ModernButton getSystemSettingsButton() {
        return systemSettingsButton;
    }

    public ModernButton getRefreshButton() {
        return refreshButton;
    }

    public JTextArea getActivityTextArea() {
        return activityTextArea;
    }

    /**
     * Refresh dashboard statistics display.
     */
    public void refreshData() {
        repaint();
    }

    /**
     * Update stat card values.
     */
    public void updateStats(int totalUsers, int activeDrivers, int todaysRides, double revenue) {
        if (totalUsersLabel != null)
            totalUsersLabel.setText(String.valueOf(totalUsers));
        if (activeDriversLabel != null)
            activeDriversLabel.setText(String.valueOf(activeDrivers));
        if (todaysRidesLabel != null)
            todaysRidesLabel.setText(String.valueOf(todaysRides));
        if (revenueLabel != null)
            revenueLabel.setText(String.format("Rs. %.0f", revenue));
    }
}
