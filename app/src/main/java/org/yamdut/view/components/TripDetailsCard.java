package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.yamdut.utils.Theme;

public class TripDetailsCard extends JPanel {
    private JLabel fareLabel;
    private JLabel etaLabel;
    private JLabel driverNameLabel;
    private JLabel statusLabel;
    private JButton adjustButton;
    private JPanel driverInfoPanel;

    public TripDetailsCard() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BACKGROUND_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)));

        // Header: Status
        statusLabel = new JLabel("Searching for drivers...");
        statusLabel.setFont(Theme.getHeadingFont());
        statusLabel.setForeground(Theme.COLOR_PRIMARY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Main info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        infoPanel.setOpaque(false);

        JPanel farePanel = new JPanel(new BorderLayout(8, 0));
        farePanel.setOpaque(false);
        fareLabel = createInfoLabel("Estimated Fare", "--", Theme.SUCCESS_COLOR);

        adjustButton = new JButton("Adjust");
        adjustButton.setFont(new Font("Segoe UI", Font.BOLD, 10));
        adjustButton.setMargin(new Insets(2, 5, 2, 5));
        adjustButton.setBackground(Color.WHITE);
        adjustButton.setVisible(false); // Only visible when trip is previewed

        farePanel.add(fareLabel.getParent(), BorderLayout.CENTER);
        farePanel.add(adjustButton, BorderLayout.EAST);

        etaLabel = createInfoLabel("Estimated Time", "--", Theme.COLOR_ACCENT);

        infoPanel.add(farePanel);
        infoPanel.add(etaLabel.getParent());

        // Driver Info (hidden initially)
        driverInfoPanel = new JPanel(new BorderLayout(12, 0));
        driverInfoPanel.setOpaque(false);
        driverInfoPanel.setVisible(false);

        JLabel driverIcon = new JLabel("Driver"); // Placeholder for an icon
        driverIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        driverIcon.setForeground(Theme.TEXT_SECONDARY);

        driverNameLabel = new JLabel("Loading...");
        driverNameLabel.setFont(Theme.getHeadingFont());
        driverNameLabel.setForeground(Theme.TEXT_PRIMARY);

        driverInfoPanel.add(driverIcon, BorderLayout.WEST);
        driverInfoPanel.add(driverNameLabel, BorderLayout.CENTER);

        add(statusLabel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(driverInfoPanel, BorderLayout.SOUTH);
    }

    private JLabel createInfoLabel(String title, String value, Color valueColor) {
        JPanel container = new JPanel(new BorderLayout(0, 4));
        container.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Theme.getCaptionFont());
        titleLbl.setForeground(Theme.TEXT_SECONDARY);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(Theme.getHeadingFont());
        valueLbl.setForeground(valueColor);

        container.add(titleLbl, BorderLayout.NORTH);
        container.add(valueLbl, BorderLayout.CENTER);

        // We return the value label so it can be updated
        return valueLbl;
    }

    public void setSearching(boolean searching) {
        if (searching) {
            statusLabel.setText("Searching for drivers...");
            statusLabel.setForeground(Theme.COLOR_PRIMARY);
            driverInfoPanel.setVisible(false);
        } else {
            statusLabel.setText("Driver Assigned!");
            statusLabel.setForeground(Theme.SUCCESS_COLOR);
            driverInfoPanel.setVisible(true);
        }
        revalidate();
        repaint();
    }

    public void setTripDetails(String fare, String eta) {
        fareLabel.setText(fare);
        etaLabel.setText(eta);
    }

    public void setDriverInfo(String name) {
        driverNameLabel.setText(name);
    }

    public void setAdjustable(boolean adjustable) {
        adjustButton.setVisible(adjustable);
    }

    public JButton getAdjustButton() {
        return adjustButton;
    }

    public void reset() {
        fareLabel.setText("--");
        etaLabel.setText("--");
        statusLabel.setText("Enter locations to see details");
        statusLabel.setForeground(Theme.TEXT_SECONDARY);
        driverInfoPanel.setVisible(false);
        adjustButton.setVisible(false);
    }
}
