package org.yamdut.view.dashboards;

import javax.swing.*;
import java.awt.*;

import org.yamdut.utils.Theme;
import org.yamdut.view.components.BaseDashboard;

public class DriverDashboard extends BaseDashboard {
    private JButton goOnlineButton;
    private JButton viewRequestsButton;
    private JButton earningsButton;
    private JLabel statusLabel;
    private boolean isOnline = false;

    public DriverDashboard() {
        super();
        initContent();
        setWelcomeMessage("Welcome, Driver!");
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
        JLabel titleLabel = new JLabel("Driver Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(titleLabel, gbc);

        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(Theme.BACKGROUND_SECONDARY);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        statusLabel = new JLabel("Status: OFFLINE");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(Color.RED);
        statusPanel.add(statusLabel);

        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(statusPanel, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        buttonsPanel.setPreferredSize(new Dimension(300, 200));

        goOnlineButton = createDashboardButton("🚗 Go Online", new Color(46, 204, 113));
        buttonsPanel.add(goOnlineButton);

        viewRequestsButton = createDashboardButton("📱 View Ride Requests", Theme.COLOR_PRIMARY);
        buttonsPanel.add(viewRequestsButton);

        earningsButton = createDashboardButton("💰 View Earnings", new Color(241, 196, 15));
        buttonsPanel.add(earningsButton);

        contentPanel.add(buttonsPanel, gbc);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        statsPanel.setPreferredSize(new Dimension(400, 100));

        statsPanel.add(createStatCard("Total Rides", "0", Theme.COLOR_PRIMARY));
        statsPanel.add(createStatCard("Rating", "0.0", new Color(241, 196, 15)));
        statsPanel.add(createStatCard("Earnings", "$0.00", new Color(46, 204, 113)));

        contentPanel.add(statsPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createDashboardButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 50));
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
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void toggleOnlineStatus() {
        isOnline = !isOnline;
        if (isOnline) {
            statusLabel.setText("Status: ONLINE");
            statusLabel.setForeground(new Color(46, 204, 113));
            goOnlineButton.setText("🚗 Go Offline");
            goOnlineButton.setBackground(new Color(231, 76, 60));
        } else {
            statusLabel.setText("Status: OFFLINE");
            statusLabel.setForeground(Color.RED);
            goOnlineButton.setText("🚗 Go Online");
            goOnlineButton.setBackground(new Color(46, 204, 113));
        }
    }

    public JButton getGoOnlineButton() {
        return goOnlineButton;
    }

    public JButton getViewRequestsButton() {
        return viewRequestsButton;
    }

    public JButton getEarningsButton() {
        return earningsButton;
    }

    public boolean isOnline() {
        return isOnline;
    }
}


