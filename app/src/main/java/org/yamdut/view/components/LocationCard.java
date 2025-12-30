package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.yamdut.utils.Theme;

public class LocationCard extends JPanel {
    private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel addressLabel;
    
    public LocationCard(String title, String icon, Color accentColor) {
        setLayout(new BorderLayout(12, 8));
        setBackground(Theme.BACKGROUND_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Icon and title panel
        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);
        
        // Icon
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(accentColor);
        iconLabel.setPreferredSize(new Dimension(40, 40));
        
        // Title
        titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.getHeadingFont());
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Address label
        addressLabel = new JLabel("Click on map to set location");
        addressLabel.setFont(Theme.getBodyFont());
        addressLabel.setForeground(Theme.TEXT_SECONDARY);
        addressLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        
        add(topPanel, BorderLayout.NORTH);
        add(addressLabel, BorderLayout.CENTER);
    }
    
    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            addressLabel.setText("Click on map to set location");
            addressLabel.setForeground(Theme.TEXT_SECONDARY);
        } else {
            addressLabel.setText(address);
            addressLabel.setForeground(Theme.TEXT_PRIMARY);
        }
    }
    
    public String getAddress() {
        String text = addressLabel.getText();
        return text.equals("Click on map to set location") ? "" : text;
    }
    
    public void setIcon(String icon) {
        iconLabel.setText(icon);
    }
}

