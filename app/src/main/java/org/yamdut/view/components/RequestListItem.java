package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.yamdut.utils.Theme;

public class RequestListItem extends JPanel {
    private JLabel passengerNameLabel;
    private JLabel routeLabel;
    
    public RequestListItem(String passengerName, String pickup, String destination) {
        setLayout(new BorderLayout(0, 4));
        setBackground(Theme.BACKGROUND_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        
        // Passenger name
        passengerNameLabel = new JLabel("👤 " + passengerName);
        passengerNameLabel.setFont(Theme.getHeadingFont());
        passengerNameLabel.setForeground(Theme.COLOR_PRIMARY);
        
        // Route
        JPanel routePanel = new JPanel(new BorderLayout(4, 0));
        routePanel.setOpaque(false);
        
        JLabel pickupLabel = new JLabel("📍 " + shortenAddress(pickup));
        pickupLabel.setFont(Theme.getBodyFont());
        pickupLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel arrowLabel = new JLabel("↓");
        arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        arrowLabel.setForeground(Theme.TEXT_SECONDARY);
        arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel destLabel = new JLabel("🎯 " + shortenAddress(destination));
        destLabel.setFont(Theme.getBodyFont());
        destLabel.setForeground(Theme.TEXT_PRIMARY);
        
        routePanel.add(pickupLabel, BorderLayout.NORTH);
        routePanel.add(arrowLabel, BorderLayout.CENTER);
        routePanel.add(destLabel, BorderLayout.SOUTH);
        
        add(passengerNameLabel, BorderLayout.NORTH);
        add(routePanel, BorderLayout.CENTER);
    }
    
    private String shortenAddress(String address) {
        if (address.length() > 40) {
            return address.substring(0, 37) + "...";
        }
        return address;
    }
}

