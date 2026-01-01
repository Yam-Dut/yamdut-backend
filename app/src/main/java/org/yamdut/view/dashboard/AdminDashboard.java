package org.yamdut.view.dashboard;

//preeti patch
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.yamdut.utils.Theme;

public class AdminDashboard extends BaseDashboard {
    private JButton manageUsersButton;
    private JButton manageDriversButton;
    private JButton viewReportsButton;
    private JButton systemSettingsButton;
    private JButton refreshButton;
    private JTextArea activityTextArea;
    private JTable usersTable;
    private DefaultTableModel usersTableModel;

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

        statsPanel.add(createStatCard("Total Users", "0", Theme.COLOR_PRIMARY, totalUsersLabel = new JLabel()));
        statsPanel
                .add(createStatCard("Active Drivers", "0", new Color(46, 204, 113), activeDriversLabel = new JLabel()));
        statsPanel.add(createStatCard("Today's Rides", "0", new Color(52, 152, 219), todaysRidesLabel = new JLabel()));
        statsPanel.add(createStatCard("Revenue", "$0.00", new Color(241, 196, 15), revenueLabel = new JLabel()));

        contentPanel.add(statsPanel, gbc);

        // Admin Controls Panel header with refresh button
        JPanel controlsHeader = new JPanel(new BorderLayout());
        controlsHeader.setOpaque(false);

        JLabel controlsLabel = new JLabel("Administration Controls");
        controlsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        controlsLabel.setForeground(Theme.TEXT_PRIMARY);

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setFocusPainted(false);

        controlsHeader.add(controlsLabel, BorderLayout.WEST);
        controlsHeader.add(refreshButton, BorderLayout.EAST);

        gbc.insets = new Insets(30, 0, 20, 0);
        contentPanel.add(controlsHeader, gbc);

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

        // Users Table
        JLabel usersLabel = new JLabel("All Users");
        usersLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        usersLabel.setForeground(Theme.TEXT_PRIMARY);
        gbc.insets = new Insets(30, 0, 10, 0);
        contentPanel.add(usersLabel, gbc);

        String[] cols = { "ID", "Full Name", "Email", "Username", "Role", "Verified", "Created At" };
        usersTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setRowHeight(24);
        usersTable.setBackground(Color.WHITE);

        JScrollPane usersScroll = new JScrollPane(usersTable);
        usersScroll.setPreferredSize(new Dimension(800, 200));
        usersScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));

        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(usersScroll, gbc);

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

    private JPanel createStatCard(String title, String value, Color color, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Theme.TEXT_SECONDARY);

        valueLabel.setText(value);
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

    public JButton getRefreshButton() {
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
            revenueLabel.setText(String.format("$%.2f", revenue));
    }

    public DefaultTableModel getUsersTableModel() {
        return usersTableModel;
    }
}
