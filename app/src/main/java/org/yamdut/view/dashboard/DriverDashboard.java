package org.yamdut.view.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import org.yamdut.model.Role;
import org.yamdut.utils.Theme;
import org.yamdut.view.components.ModernButton;
import org.yamdut.view.components.RideCard;
import org.yamdut.view.components.StatusBadge;
import org.yamdut.view.map.MapPanel;

public class DriverDashboard extends BaseDashboard {

    // Controls
    private JToggleButton onlineToggle;
    private ModernButton acceptRideButton;
    private ModernButton startRideButton;
    private ModernButton completeRideButton;
    
    // Status
    private StatusBadge statusBadge;
    private JPanel titleStatusPanel; // For status badge updates
    
    // Requests list
    private DefaultListModel<String> requestListModel;
    private JList<String> requestList;
    
    // Active ride display
    private JPanel activeRidePanel;
    private RideCard activeRideCard;
    
    // Map
    private MapPanel mapPanel;
    

    public DriverDashboard() {
        super();
        setWelcomeMessage("Welcome, Driver");
        initContent();
    }

    @Override
    protected void initContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left sidebar panel
        JPanel leftPanel = createLeftPanel();
        leftPanel.setPreferredSize(new Dimension(380, 0));
        
        // Map panel
        mapPanel = new MapPanel(Role.DRIVER);
        mapPanel.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        
        // Layout
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(mapPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        // Title and status
        titleStatusPanel = new JPanel(new BorderLayout(0, 8));
        titleStatusPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        JLabel titleLabel = new JLabel("Driver Dashboard");
        titleLabel.setFont(Theme.getTitleFont());
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        statusBadge = StatusBadge.offline();
        
        titleStatusPanel.add(titleLabel, BorderLayout.NORTH);
        titleStatusPanel.add(statusBadge, BorderLayout.CENTER);
        
        // Control buttons
        JPanel controlPanel = createControlPanel();
        
        // Requests section
        JPanel requestsSection = createRequestsSection();
        
        // Active ride panel (initially hidden)
        activeRidePanel = new JPanel(new BorderLayout());
        activeRidePanel.setBackground(Theme.BACKGROUND_PRIMARY);
        activeRidePanel.setVisible(false);
        
        // Assemble panel
        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setBackground(Theme.BACKGROUND_PRIMARY);
        topSection.add(titleStatusPanel, BorderLayout.NORTH);
        topSection.add(controlPanel, BorderLayout.CENTER);
        
        panel.add(topSection, BorderLayout.NORTH);
        panel.add(requestsSection, BorderLayout.CENTER);
        panel.add(activeRidePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.weightx = 1.0;
        
        // Online toggle - improved UI
        onlineToggle = new JToggleButton("🟢 Go Online");
        styleToggleButton(onlineToggle);
        
        // Accept ride button
        acceptRideButton = new ModernButton("Accept Ride", Theme.COLOR_SECONDARY);
        acceptRideButton.setEnabled(false);
        
        // Start ride button (hidden initially)
        startRideButton = new ModernButton("Start Ride", Theme.COLOR_SECONDARY);
        startRideButton.setVisible(false);
        
        // Complete ride button (hidden initially)
        completeRideButton = new ModernButton("Complete Ride", Theme.SUCCESS_COLOR);
        completeRideButton.setVisible(false);
        
        panel.add(onlineToggle, gbc);
        panel.add(acceptRideButton, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(startRideButton, gbc);
        panel.add(completeRideButton, gbc);
        
        return panel;
    }
    
    private void styleToggleButton(JToggleButton button) {
        button.setFont(Theme.getButtonFont());
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.COLOR_PRIMARY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 50));
        
        // Add hover and state change effects
        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setText("🔴 Go Offline");
                button.setBackground(Theme.SUCCESS_COLOR);
            } else {
                button.setText("🟢 Go Online");
                button.setBackground(Theme.COLOR_PRIMARY);
            }
        });
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isSelected()) {
                    button.setBackground(Theme.SUCCESS_COLOR.darker());
                } else {
                    button.setBackground(Theme.COLOR_PRIMARY.darker());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.isSelected()) {
                    button.setBackground(Theme.SUCCESS_COLOR);
                } else {
                    button.setBackground(Theme.COLOR_PRIMARY);
                }
            }
        });
    }
    
    private JPanel createRequestsSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        
        JLabel sectionTitle = new JLabel("Ride Requests");
        sectionTitle.setFont(Theme.getHeadingFont());
        sectionTitle.setForeground(Theme.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        requestListModel = new DefaultListModel<>();
        requestList = new JList<>(requestListModel);
        requestList.setFont(Theme.getBodyFont());
        requestList.setBackground(Theme.BACKGROUND_CARD);
        requestList.setSelectionBackground(Theme.COLOR_PRIMARY);
        requestList.setSelectionForeground(Color.WHITE);
        requestList.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        requestList.setCellRenderer(new javax.swing.ListCellRenderer<String>() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<? extends String> list, String value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel(value);
                label.setFont(Theme.getBodyFont());
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                if (isSelected) {
                    label.setBackground(Theme.COLOR_PRIMARY);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Theme.BACKGROUND_CARD);
                    label.setForeground(Theme.TEXT_PRIMARY);
                }
                
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(requestList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.setBackground(Theme.BACKGROUND_CARD);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void setOnline(boolean online) {
        onlineToggle.setSelected(online);
        // Text and color are handled by the change listener in styleToggleButton
        
        // Update status badge - find it in the titleStatusPanel
        statusBadge = online ? StatusBadge.online() : StatusBadge.offline();
        
        // Update status badge in titleStatusPanel
        if (titleStatusPanel != null) {
            // Remove old status badge if exists
            Component[] components = titleStatusPanel.getComponents();
            for (int i = components.length - 1; i >= 0; i--) {
                if (components[i] instanceof StatusBadge) {
                    titleStatusPanel.remove(i);
                }
            }
            // Add new status badge
            titleStatusPanel.add(statusBadge, BorderLayout.CENTER);
            titleStatusPanel.revalidate();
            titleStatusPanel.repaint();
        }
    }
    
    public void showActiveRide(String pickup, String destination, String passengerName) {
        activeRideCard = new RideCard(pickup, destination, "View Details");
        activeRidePanel.removeAll();
        activeRidePanel.add(activeRideCard, BorderLayout.CENTER);
        activeRidePanel.setVisible(true);
        activeRidePanel.revalidate();
        activeRidePanel.repaint();
        
        // Hide accept button, show start/complete buttons
        acceptRideButton.setVisible(false);
        startRideButton.setVisible(true);
        completeRideButton.setVisible(false);
    }
    
    public void hideActiveRide() {
        activeRidePanel.setVisible(false);
        activeRidePanel.removeAll();
        activeRideCard = null;
        
        // Show accept button, hide start/complete buttons
        acceptRideButton.setVisible(true);
        startRideButton.setVisible(false);
        completeRideButton.setVisible(false);
    }
    
    public void setRideStarted(boolean started) {
        if (started) {
            startRideButton.setVisible(false);
            completeRideButton.setVisible(true);
        } else {
            startRideButton.setVisible(true);
            completeRideButton.setVisible(false);
        }
    }

    // Getters
    public JToggleButton getOnlineToggle() {
        return onlineToggle;
    }

    public ModernButton getAcceptRideButton() {
        return acceptRideButton;
    }
    
    public ModernButton getStartRideButton() {
        return startRideButton;
    }
    
    public ModernButton getCompleteRideButton() {
        return completeRideButton;
    }

    public DefaultListModel<String> getRequestListModel() {
        return requestListModel;
    }

    public JList<String> getRequestList() {
        return requestList;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }
    
    public RideCard getActiveRideCard() {
        return activeRideCard;
    }
}
