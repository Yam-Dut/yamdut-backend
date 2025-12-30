package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.yamdut.utils.Theme;

public class RideCard extends JPanel {
    private JLabel distanceLabel;
    private JButton actionButton;
    
    public RideCard(String pickup, String destination, String actionText) {
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BACKGROUND_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        
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
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.add(pickupPanel, BorderLayout.NORTH);
        infoPanel.add(arrowLabel, BorderLayout.CENTER);
        infoPanel.add(destPanel, BorderLayout.SOUTH);
        
        // Action button
        actionButton = new JButton(actionText);
        styleButton(actionButton);
        
        add(infoPanel, BorderLayout.CENTER);
        add(actionButton, BorderLayout.SOUTH);
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

