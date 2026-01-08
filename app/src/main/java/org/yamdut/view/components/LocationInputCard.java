package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import org.yamdut.utils.Theme;

public class LocationInputCard extends JPanel {
    private JLabel iconLabel;
    private JLabel titleLabel;
    private JTextField addressField;
    private JButton searchButton;
    private JButton changeButton; // Added for pickup locking
    private JLabel statusLabel;

    public LocationInputCard(String title, String icon, Color accentColor) {
        setLayout(new BorderLayout(12, 8));
        setBackground(Theme.BACKGROUND_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                new EmptyBorder(16, 16, 16, 16)));

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

        // Input field and button
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        addressField = new JTextField();
        addressField.setFont(Theme.getBodyFont());
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
                new EmptyBorder(10, 12, 10, 12)));
        addressField.setBackground(Color.WHITE);
        addressField.setPreferredSize(new Dimension(0, 40));

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchButton.setBackground(accentColor); // Use parameter directly
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        inputPanel.add(addressField, BorderLayout.CENTER);

        // Action panel for buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actionPanel.setOpaque(false);

        changeButton = new JButton("Change");
        changeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changeButton.setBackground(new Color(52, 152, 219));
        changeButton.setForeground(Color.WHITE);
        changeButton.setVisible(false); // Only visible for pickup if needed
        changeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        actionPanel.add(changeButton);
        actionPanel.add(searchButton);

        inputPanel.add(actionPanel, BorderLayout.EAST);

        // Status label
        statusLabel = new JLabel("Enter location name");
        statusLabel.setFont(Theme.getCaptionFont());
        statusLabel.setForeground(Theme.TEXT_SECONDARY);
        statusLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        add(topPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    public void setAddress(String address) {
        if (address != null && !address.isEmpty()) {
            addressField.setText(address);
            statusLabel.setText("Location set");
            statusLabel.setForeground(Theme.SUCCESS_COLOR);
        } else {
            addressField.setText("");
            statusLabel.setText("Enter location name");
            statusLabel.setForeground(Theme.TEXT_SECONDARY);
        }
    }

    public String getAddress() {
        return addressField.getText().trim();
    }

    public void setStatus(String status, Color color) {
        statusLabel.setText(status);
        statusLabel.setForeground(color);
    }

    public JTextField getAddressField() {
        return addressField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public void setLocked(boolean locked) {
        addressField.setEditable(!locked);
        searchButton.setEnabled(!locked);
        if (locked) {
            addressField.setBackground(new Color(245, 245, 245));
        } else {
            addressField.setBackground(Color.WHITE);
        }
    }

    public void showChangeButton(boolean show) {
        changeButton.setVisible(show);
        revalidate();
        repaint();
    }

    public JButton getChangeButton() {
        return changeButton;
    }

    public void setChangeAction(ActionListener listener) {
        for (ActionListener al : changeButton.getActionListeners()) {
            changeButton.removeActionListener(al);
        }
        changeButton.addActionListener(listener);
    }

    public void setSearchAction(ActionListener listener) {
        for (ActionListener al : searchButton.getActionListeners()) {
            searchButton.removeActionListener(al);
        }
        searchButton.addActionListener(listener);

        // Also trigger on Enter key
        addressField.addActionListener(listener);
    }
}
