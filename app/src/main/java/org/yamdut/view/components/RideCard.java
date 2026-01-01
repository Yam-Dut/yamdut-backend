package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.yamdut.utils.Theme;

public class RideCard extends JPanel {
    private JLabel distanceLabel;
    private JButton actionButton;

    private JLabel driverLabel;
    private JPanel driverPanel;

    public RideCard(String pickup, String destination, String actionText) {
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BACKGROUND_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                new EmptyBorder(16, 16, 16, 16)));

        // Pickup
        JPanel pickupPanel = createLocationRow("📍", pickup, "Pickup");

        // Arrow
        JLabel arrowLabel = new JLabel("↓");
        arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        arrowLabel.setForeground(Theme.TEXT_SECONDARY);
        arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        arrowLabel.setBorder(new EmptyBorder(4, 0, 4, 0));

        // Destination
        JPanel destPanel = createLocationRow("🎯", destination, "Destination");

        // Driver Info (Visible by default)
        driverPanel = createDriverInfoRow();
        driverPanel.setVisible(true);

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.add(pickupPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(arrowLabel, BorderLayout.NORTH);
        centerPanel.add(destPanel, BorderLayout.CENTER);
        centerPanel.add(driverPanel, BorderLayout.SOUTH);

        infoPanel.add(centerPanel, BorderLayout.CENTER);

        // Action button
        actionButton = new JButton(actionText);
        styleButton(actionButton);

        add(infoPanel, BorderLayout.CENTER);
        add(actionButton, BorderLayout.SOUTH);
    }

    private JLabel infoLabel;

    private JPanel createDriverInfoRow() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel iconLabel = new JLabel("\uD83D\uDE95"); // Car icon
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        infoLabel = new JLabel("Driver");
        infoLabel.setFont(Theme.getCaptionFont());
        infoLabel.setForeground(Theme.TEXT_SECONDARY);

        driverLabel = new JLabel("Waiting for driver...");
        driverLabel.setFont(Theme.getBodyFont());
        driverLabel.setForeground(Theme.TEXT_PRIMARY);

        textPanel.add(infoLabel, BorderLayout.NORTH);
        textPanel.add(driverLabel, BorderLayout.CENTER);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    public void setInfoLabel(String text) {
        if (infoLabel != null) {
            infoLabel.setText(text);
        }
    }

    public void setDriverName(String name) {
        if (driverLabel != null) {
            driverLabel.setText(name);
        }
    }

    private JPanel createLocationRow(String icon, String address, String label) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Theme.getCaptionFont());
        labelComp.setForeground(Theme.TEXT_SECONDARY);

        JLabel addressComp = new JLabel(address);
        addressComp.setFont(Theme.getBodyFont());
        addressComp.setForeground(Theme.TEXT_PRIMARY);

        textPanel.add(labelComp, BorderLayout.NORTH);
        textPanel.add(addressComp, BorderLayout.CENTER);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button) {
        button.setFont(Theme.getButtonFont());
        button.setForeground(Color.WHITE);
        // Set background based on button text
        if (button.getText().contains("Cancel")) {
            button.setBackground(Theme.ERROR_COLOR);
        } else {
            button.setBackground(Theme.COLOR_PRIMARY);
        }
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 44));
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public void setButtonText(String text) {
        actionButton.setText(text);
        // Update color based on text
        if (text.contains("Cancel")) {
            actionButton.setBackground(Theme.ERROR_COLOR);
        } else {
            actionButton.setBackground(Theme.COLOR_PRIMARY);
        }
    }

    public JButton getActionButton() {
        return actionButton;
    }

    public void setDistance(String distance) {
        if (distanceLabel != null) {
            distanceLabel.setText(distance);
        }
    }
}
