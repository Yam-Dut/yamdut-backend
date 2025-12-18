package org.yamdut.view.dashboards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class DriverDashboardView extends JFrame {
    private JEditorPane mapPanel;
    private JButton arrivedButton;
    private JButton startTripButton;
    private JButton endTripButton;
    private JButton logoutButton;
    private JLabel riderInfoLabel;
    private JLabel tripStatusLabel;
    private JLabel timestampLabel;
    private String currentTripStatus;

    public DriverDashboardView() {
        super("YamDut - Driver Dashboard");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);
        initializeComponents();
    }

    public void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 247, 250));

        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mapPanel = new JEditorPane();
        mapPanel.setEditable(false);
        mapPanel.setContentType("text/html");
        mapPanel.setBackground(Color.WHITE);
        loadDriverMapHTML();

        JScrollPane mapScroll = new JScrollPane(mapPanel);
        mapScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        centerPanel.add(mapScroll, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(45, 50, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel titleLabel = new JLabel("🚕 YamDut - Driver Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(110, 40));
        logoutButton.setOpaque(true);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel infoPanel = createInfoPanel();
        bottomPanel.add(infoPanel);

        bottomPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        arrivedButton = createStyledButton("Arrived", new Color(70, 130, 180));
        startTripButton = createStyledButton("Start Trip", new Color(255, 193, 7));
        endTripButton = createStyledButton("End Trip", new Color(40, 167, 69));

        arrivedButton.setEnabled(true);
        startTripButton.setEnabled(false);
        endTripButton.setEnabled(false);

        buttonPanel.add(arrivedButton);
        buttonPanel.add(startTripButton);
        buttonPanel.add(endTripButton);

        bottomPanel.add(buttonPanel);

        return bottomPanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        riderInfoLabel = new JLabel("Rider: Not assigned");
        riderInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        riderInfoLabel.setForeground(new Color(33, 37, 41));

        tripStatusLabel = new JLabel("Trip Status: PENDING");
        tripStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tripStatusLabel.setForeground(new Color(70, 130, 180));

        timestampLabel = new JLabel("Timestamps: -");
        timestampLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timestampLabel.setForeground(new Color(100, 100, 100));

        infoPanel.add(riderInfoLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(tripStatusLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(timestampLabel);

        return infoPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 45));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });

        return button;
    }

    private void loadDriverMapHTML() {
        try {
            URL mapResource = getClass().getResource("/web/driver-map.html");
            if (mapResource != null) {
                mapPanel.setPage(mapResource);
            } else {
                String fallbackHTML = "<html><body style='display:flex;align-items:center;justify-content:center;height:100%;'>" +
                    "<h2 style='color:#666;'>Driver map file not found</h2></body></html>";
                mapPanel.setText(fallbackHTML);
            }
        } catch (IOException e) {
            String errorHTML = "<html><body style='display:flex;align-items:center;justify-content:center;height:100%;'>" +
                "<h2 style='color:#d32f2f;'>Error loading map: " + e.getMessage() + "</h2></body></html>";
            mapPanel.setText(errorHTML);
            e.printStackTrace();
        }
    }

    public void addArrivedListener(ActionListener listener) {
        arrivedButton.addActionListener(listener);
    }

    public void addStartTripListener(ActionListener listener) {
        startTripButton.addActionListener(listener);
    }

    public void addEndTripListener(ActionListener listener) {
        endTripButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void setRiderInfo(String name, String phone) {
        riderInfoLabel.setText(String.format("Rider: %s | Phone: %s", name, phone));
    }

    public void setTripStatus(String status) {
        this.currentTripStatus = status;
        tripStatusLabel.setText("Trip Status: " + status);

        switch (status) {
            case "PENDING":
                arrivedButton.setEnabled(true);
                startTripButton.setEnabled(false);
                endTripButton.setEnabled(false);
                tripStatusLabel.setForeground(new Color(70, 130, 180));
                break;
            case "ARRIVED":
                arrivedButton.setEnabled(false);
                startTripButton.setEnabled(true);
                endTripButton.setEnabled(false);
                tripStatusLabel.setForeground(new Color(255, 193, 7));
                break;
            case "IN_PROGRESS":
                arrivedButton.setEnabled(false);
                startTripButton.setEnabled(false);
                endTripButton.setEnabled(true);
                tripStatusLabel.setForeground(new Color(40, 167, 69));
                break;
            case "COMPLETED":
                arrivedButton.setEnabled(false);
                startTripButton.setEnabled(false);
                endTripButton.setEnabled(false);
                tripStatusLabel.setForeground(new Color(40, 167, 69));
                break;
        }
    }

    public void updateTimestamps(String arrived, String started, String ended) {
        StringBuilder timestamps = new StringBuilder("Timestamps: ");
        if (arrived != null) timestamps.append("Arrived: ").append(arrived).append(" | ");
        if (started != null) timestamps.append("Started: ").append(started).append(" | ");
        if (ended != null) timestamps.append("Ended: ").append(ended);
        timestampLabel.setText(timestamps.toString());
    }

    public String getCurrentTripStatus() {
        return currentTripStatus;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showTripSummary(String summary) {
        JOptionPane.showMessageDialog(this, summary, "Trip Summary", JOptionPane.INFORMATION_MESSAGE);
    }
}
