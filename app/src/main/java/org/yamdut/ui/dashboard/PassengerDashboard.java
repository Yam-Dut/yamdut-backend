package org.yamdut.ui.dashboard;

import javax.swing.*;
import java.awt.*;

import org.yamdut.utils.Theme;

public class PassengerDashboard extends BaseDashboard {
    private JButton bookRideButton;
    private JButton rideHistoryButton;
    private JButton paymentMethodsButton;
    private JButton showMapButton;
    private JLabel statusLabel;

    public PassengerDashboard() {
        super();
        initContent();
        setWelcomeMessage("Welcome, Passenger!");
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
        JLabel titleLabel = new JLabel("Passenger Dashboard");
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

        statusLabel = new JLabel("Ready to book a ride!");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(Theme.TEXT_PRIMARY);
        statusPanel.add(statusLabel);

        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(statusPanel, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        buttonsPanel.setPreferredSize(new Dimension(300, 200));

        // Book Ride Button
        bookRideButton = createDashboardButton("🚖 Book a Ride", Theme.COLOR_PRIMARY);
        buttonsPanel.add(bookRideButton);

        // Ride History Button
        rideHistoryButton = createDashboardButton("📋 Ride History", new Color(52, 152, 219));
        buttonsPanel.add(rideHistoryButton);

        // Payment Methods Button
        paymentMethodsButton = createDashboardButton("💳 Payment Methods", new Color(46, 204, 113));
        buttonsPanel.add(paymentMethodsButton);

        // Map Button
        showMapButton = createDashboardButton("🗺️ View Map", new Color(155, 89, 182));
        buttonsPanel.add(showMapButton);

        contentPanel.add(buttonsPanel, gbc);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        statsPanel.setPreferredSize(new Dimension(400, 100));

        statsPanel.add(createStatCard("Total Rides", "0", Theme.COLOR_PRIMARY));
        statsPanel.add(createStatCard("5★ Ratings", "0.0", new Color(241, 196, 15)));
        statsPanel.add(createStatCard("Wallet", "$0.00", new Color(46, 204, 113)));

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

    public JButton getBookRideButton() {
        return bookRideButton;
    }

    public JButton getRideHistoryButton() {
        return rideHistoryButton;
    }

    public JButton getPaymentMethodsButton() {
        return paymentMethodsButton;
    }

    public JButton getShowMapButton() {
        return showMapButton;
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }
}


