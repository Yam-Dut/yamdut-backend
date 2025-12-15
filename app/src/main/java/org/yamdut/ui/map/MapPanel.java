package org.yamdut.ui.map;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class MapPanel extends JPanel {
    private JButton openMapButton;

    public MapPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel placeholderPanel = new JPanel(new GridBagLayout());
        placeholderPanel.setBackground(new Color(240, 242, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel mapIcon = new JLabel("🗺️");
        mapIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        gbc.insets = new Insets(0, 0, 20, 0);
        placeholderPanel.add(mapIcon, gbc);

        JLabel titleLabel = new JLabel("Interactive Map");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.insets = new Insets(0, 0, 10, 0);
        placeholderPanel.add(titleLabel, gbc);

        JLabel descLabel = new JLabel("Live tracking and 3D map view (coming soon)");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(127, 140, 141));
        placeholderPanel.add(descLabel, gbc);

        openMapButton = new JButton("Open Map in Browser");
        openMapButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        openMapButton.setBackground(new Color(52, 152, 219));
        openMapButton.setForeground(Color.WHITE);
        openMapButton.setFocusPainted(false);
        openMapButton.setBorderPainted(false);
        openMapButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openMapButton.setPreferredSize(new Dimension(220, 40));
        gbc.insets = new Insets(30, 0, 0, 0);
        placeholderPanel.add(openMapButton, gbc);

        add(placeholderPanel, BorderLayout.CENTER);

        openMapButton.addActionListener(e -> openWebPage("https://www.google.com/maps"));
    }

    private void openWebPage(String url) {
        try {
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please visit: " + url,
                        "Open in Browser",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not open browser. Please visit: " + url,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public JButton getOpenMapButton() {
        return openMapButton;
    }
}


