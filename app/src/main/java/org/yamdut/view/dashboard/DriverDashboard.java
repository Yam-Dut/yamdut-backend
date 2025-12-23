package org.yamdut.view.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.jxmapviewer.viewer.GeoPosition;
import org.yamdut.utils.Theme;
import org.yamdut.view.components.PrimaryButton;
import org.yamdut.view.map.MapPanel;

public class DriverDashboard extends BaseDashboard {
    // UI Components
    private MapPanel mapPanel;
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private JPanel rideControlPanel;
    private JPanel rideInfoPanel;
    private JPanel earningsPanel;
    private JPanel rideRequestsPanel;
    
    // Status controls
    private JButton onlineToggleBtn;
    private JLabel statusLabel;
    private JLabel earningsTodayLabel;
    private JLabel totalTripsLabel;
    private JLabel ratingLabel;
    private JLabel onlineTimeLabel;
    
    // Ride info
    private JLabel passengerNameLabel;
    private JLabel passengerPhoneLabel;
    private JLabel pickupLocationLabel;
    private JLabel dropoffLocationLabel;
    private JLabel rideFareLabel;
    private JLabel rideDistanceLabel;
    private JLabel rideEtaLabel;
    
    // Ride control buttons
    private JButton acceptRideBtn;
    private JButton startRideBtn;
    private JButton completeRideBtn;
    private JButton cancelRideBtn;
    private JButton navigateBtn;
    
    // Earnings table
    private JTable earningsTable;
    private DefaultTableModel earningsModel;
    
    // Ride requests table
    private JTable requestsTable;
    private DefaultTableModel requestsModel;
    
    // State
    private boolean isOnline = false;
    private boolean hasActiveRide = false;
    private LocalDateTime onlineSince;
    private Timer onlineTimer;
    
    public DriverDashboard() {
        super();
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_PRIMARY);
        
        initUI();
        initMap();
        initTimer();
        updateUIState();
    }
    
    private void initUI() {
        // Create main container with sidebar
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        
        // Sidebar Panel
        sidebarPanel = createSidebarPanel();
        splitPane.setLeftComponent(sidebarPanel);
        
        // Main Content Panel (Map + Controls)
        mainContentPanel = createMainContentPanel();
        splitPane.setRightComponent(mainContentPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(Theme.BACKGROUND_SECONDARY);
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header with status
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.BACKGROUND_SECONDARY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel headerLabel = new JLabel("Driver Dashboard");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        
        statusLabel = new JLabel("Offline");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(Theme.ERROR_COLOR);
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        sidebar.add(headerPanel, BorderLayout.NORTH);
        
        // Stats Panel
        JPanel statsPanel = createStatsPanel();
        sidebar.add(statsPanel, BorderLayout.CENTER);
        
        // Ride Control Panel
        rideControlPanel = createRideControlPanel();
        sidebar.add(rideControlPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND_SECONDARY);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Online Toggle Button
        PrimaryButton toggleBtn = new PrimaryButton("Go Online");
        onlineToggleBtn = toggleBtn.getButton();
        onlineToggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        onlineToggleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        onlineToggleBtn.addActionListener(e -> toggleOnlineStatus());
        
        // Stats Grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        statsGrid.setBackground(Theme.BACKGROUND_SECONDARY);
        statsGrid.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        earningsTodayLabel = createStatCard("Today's Earnings", "₹0.00", Theme.COLOR_PRIMARY);
        totalTripsLabel = createStatCard("Total Trips", "0", new Color(52, 152, 219));
        ratingLabel = createStatCard("Rating", "4.5", new Color(241, 196, 15));
        onlineTimeLabel = createStatCard("Online Time", "0:00", new Color(155, 89, 182));
        
        statsGrid.add(earningsTodayLabel);
        statsGrid.add(totalTripsLabel);
        statsGrid.add(ratingLabel);
        statsGrid.add(onlineTimeLabel);
        
        // Ride Requests Panel
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setBackground(Theme.BACKGROUND_SECONDARY);
        requestsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel requestsTitle = new JLabel("Ride Requests");
        requestsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        requestsTitle.setForeground(Theme.TEXT_PRIMARY);
        
        String[] columns = {"Time", "From", "Fare", "Distance"};
        requestsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        requestsTable = new JTable(requestsModel);
        requestsTable.setRowHeight(30);
        requestsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        requestsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(new LineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(350, 150));
        
        requestsPanel.add(requestsTitle, BorderLayout.NORTH);
        requestsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components
        panel.add(onlineToggleBtn);
        panel.add(Box.createVerticalStrut(20));
        panel.add(statsGrid);
        panel.add(Box.createVerticalStrut(20));
        panel.add(requestsPanel);
        
        return panel;
    }
    
    private JLabel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Theme.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        
        // Wrap in JLabel to maintain consistent sizing
        JLabel wrapper = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(150, 80);
            }
        };
        wrapper.setLayout(new BorderLayout());
        wrapper.add(card, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel createRideControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND_SECONDARY);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Ride Info Panel
        rideInfoPanel = new JPanel();
        rideInfoPanel.setLayout(new BoxLayout(rideInfoPanel, BoxLayout.Y_AXIS));
        rideInfoPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        rideInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.COLOR_PRIMARY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        rideInfoPanel.setVisible(false);
        
        JLabel rideTitle = new JLabel("Active Ride");
        rideTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rideTitle.setForeground(Theme.COLOR_PRIMARY);
        rideTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        passengerNameLabel = new JLabel("Passenger: --");
        passengerNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passengerNameLabel.setForeground(Theme.TEXT_PRIMARY);
        
        passengerPhoneLabel = new JLabel("Phone: --");
        passengerPhoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passengerPhoneLabel.setForeground(Theme.TEXT_SECONDARY);
        
        pickupLocationLabel = new JLabel("Pickup: --");
        pickupLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pickupLocationLabel.setForeground(Theme.TEXT_PRIMARY);
        
        dropoffLocationLabel = new JLabel("Destination: --");
        dropoffLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dropoffLocationLabel.setForeground(Theme.TEXT_PRIMARY);
        
        rideFareLabel = new JLabel("Fare: ₹--");
        rideFareLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rideFareLabel.setForeground(Theme.COLOR_PRIMARY);
        
        rideDistanceLabel = new JLabel("Distance: -- km");
        rideDistanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rideDistanceLabel.setForeground(Theme.TEXT_SECONDARY);
        
        rideEtaLabel = new JLabel("ETA: -- min");
        rideEtaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rideEtaLabel.setForeground(Theme.TEXT_SECONDARY);
        
        rideInfoPanel.add(rideTitle);
        rideInfoPanel.add(Box.createVerticalStrut(10));
        rideInfoPanel.add(passengerNameLabel);
        rideInfoPanel.add(passengerPhoneLabel);
        rideInfoPanel.add(Box.createVerticalStrut(10));
        rideInfoPanel.add(pickupLocationLabel);
        rideInfoPanel.add(dropoffLocationLabel);
        rideInfoPanel.add(Box.createVerticalStrut(10));
        rideInfoPanel.add(rideFareLabel);
        rideInfoPanel.add(rideDistanceLabel);
        rideInfoPanel.add(rideEtaLabel);
        
        // Control Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonsPanel.setBackground(Theme.BACKGROUND_SECONDARY);
        buttonsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        acceptRideBtn = createActionButton("Accept Ride", Theme.COLOR_PRIMARY);
        startRideBtn = createActionButton("Start Ride", new Color(46, 204, 113));
        completeRideBtn = createActionButton("Complete Ride", new Color(52, 152, 219));
        cancelRideBtn = createActionButton("Cancel Ride", Theme.ERROR_COLOR);
        navigateBtn = createActionButton("Navigate", new Color(241, 196, 15));
        
        acceptRideBtn.addActionListener(e -> acceptRide());
        startRideBtn.addActionListener(e -> startRide());
        completeRideBtn.addActionListener(e -> completeRide());
        cancelRideBtn.addActionListener(e -> cancelRide());
        navigateBtn.addActionListener(e -> navigateToPickup());
        
        buttonsPanel.add(acceptRideBtn);
        buttonsPanel.add(startRideBtn);
        buttonsPanel.add(completeRideBtn);
        buttonsPanel.add(cancelRideBtn);
        buttonsPanel.add(navigateBtn);
        
        // Add components
        panel.add(rideInfoPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(null);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setVisible(false);
        return button;
    }
    
    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        // Map Panel
        mapPanel = new MapPanel();
        panel.add(mapPanel, BorderLayout.CENTER);
        
        // Earnings Panel (floating)
        earningsPanel = createEarningsPanel();
        
        // Position panels
        JPanel mapOverlayPanel = new JPanel(new BorderLayout());
        mapOverlayPanel.setOpaque(false);
        
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.setOpaque(false);
        bottomRightPanel.add(earningsPanel);
        
        mapOverlayPanel.add(bottomRightPanel, BorderLayout.SOUTH);
        panel.add(mapOverlayPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEarningsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(300, 250));
        
        JLabel title = new JLabel("Earnings History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Theme.TEXT_PRIMARY);
        
        // Create table model
        String[] columns = {"Date", "Ride", "Fare", "Status"};
        earningsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        earningsTable = new JTable(earningsModel);
        earningsTable.setRowHeight(25);
        earningsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        earningsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(earningsTable);
        scrollPane.setBorder(null);
        
        // Add sample data
        addSampleEarnings();
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void initMap() {
        // Set default location (Kathmandu)
        GeoPosition defaultLocation = new GeoPosition(27.7172, 85.3240);
        mapPanel.setCenter(defaultLocation.getLatitude(), defaultLocation.getLongitude(), 13);
        
        // Add driver marker
        mapPanel.addDriverMarker(defaultLocation.getLatitude(), defaultLocation.getLongitude(), "driver1", "You");
    }
    
    private void initTimer() {
        onlineTimer = new Timer(1000, e -> updateOnlineTime());
    }
    
    private void updateOnlineTime() {
        if (isOnline && onlineSince != null) {
            long seconds = java.time.Duration.between(onlineSince, LocalDateTime.now()).getSeconds();
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            onlineTimeLabel.setText(String.format("%d:%02d", hours, minutes));
        }
    }
    
    // Button action methods
    private void toggleOnlineStatus() {
        isOnline = !isOnline;
        
        if (isOnline) {
            onlineSince = LocalDateTime.now();
            onlineTimer.start();
            statusLabel.setText("Online");
            statusLabel.setForeground(new Color(46, 204, 113));
            onlineToggleBtn.setText("Go Offline");
            onlineToggleBtn.setBackground(Theme.ERROR_COLOR);
            showSuccess("You're now online and ready to receive ride requests!");
            
            // Simulate ride requests when online
            simulateRideRequests();
        } else {
            onlineTimer.stop();
            statusLabel.setText("Offline");
            statusLabel.setForeground(Theme.ERROR_COLOR);
            onlineToggleBtn.setText("Go Online");
            onlineToggleBtn.setBackground(Theme.COLOR_PRIMARY);
            showInfo("You're now offline.");
        }
        
        updateUIState();
    }
    
    private void simulateRideRequests() {
        if (!isOnline) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(3000); // Wait 3 seconds
                
                if (isOnline && !hasActiveRide) {
                    addRideRequest("Thamel", 250.0, 2.5);
                    showInfo("New ride request received!");
                }
                
                Thread.sleep(5000); // Wait 5 more seconds
                
                if (isOnline && !hasActiveRide) {
                    addRideRequest("Boudha", 320.0, 4.2);
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    
    private void acceptRide() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a ride request first");
            return;
        }
        
        // Get ride details from table
        String from = (String) requestsModel.getValueAt(selectedRow, 1);
        String fareStr = (String) requestsModel.getValueAt(selectedRow, 2);
        String distanceStr = (String) requestsModel.getValueAt(selectedRow, 3);
        
        double fare = Double.parseDouble(fareStr.replace("₹", "").trim());
        double distance = Double.parseDouble(distanceStr.replace(" km", "").trim());
        
        // Remove from requests
        clearRideRequest(selectedRow);
        
        // Simulate ride acceptance
        GeoPosition pickup = new GeoPosition(27.7172, 85.3240); // Thamel
        GeoPosition dropoff = new GeoPosition(27.6966, 85.3591); // Boudha
        
        setActiveRide(
            "John Smith", 
            "+977 9841XXXXXX",
            pickup,
            dropoff,
            fare,
            distance,
            15
        );
        
        showSuccess("Ride accepted! Passenger details loaded.");
    }
    
    private void startRide() {
        showSuccess("Ride started! Heading to pickup location.");
        startRideBtn.setVisible(false);
        completeRideBtn.setVisible(true);
    }
    
    private void completeRide() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Mark this ride as completed?",
            "Complete Ride",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Add to earnings
            addEarning("#R" + System.currentTimeMillis(), 450.0, "Completed");
            showRideCompleted();
            showSuccess("Ride completed! Payment received.");
            
            // Update stats
            updateStats(1250.0, 45, 4.7);
        }
    }
    
    private void cancelRide() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this ride?",
            "Cancel Ride",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            showRideCompleted();
            showInfo("Ride cancelled.");
        }
    }
    
    private void navigateToPickup() {
        showInfo("Navigation will open in external app");
    }
    
    // Public methods for controller interaction
    
    public void setActiveRide(String passengerName, String phone, 
                              GeoPosition pickup, GeoPosition dropoff,
                              double fare, double distance, int eta) {
        hasActiveRide = true;
        
        passengerNameLabel.setText("Passenger: " + passengerName);
        passengerPhoneLabel.setText("Phone: " + phone);
        pickupLocationLabel.setText("Pickup: " + formatLocation(pickup));
        dropoffLocationLabel.setText("Destination: " + formatLocation(dropoff));
        rideFareLabel.setText(String.format("Fare: ₹%.2f", fare));
        rideDistanceLabel.setText(String.format("Distance: %.1f km", distance));
        rideEtaLabel.setText("ETA: " + eta + " min");
        
        rideInfoPanel.setVisible(true);
        acceptRideBtn.setVisible(false);
        startRideBtn.setVisible(true);
        cancelRideBtn.setVisible(true);
        navigateBtn.setVisible(true);
        
        // Show route on map
        mapPanel.showRoute(pickup.getLatitude(), pickup.getLongitude(),
                          dropoff.getLatitude(), dropoff.getLongitude());
        
        updateUIState();
    }
    
    public void addRideRequest(String from, double fare, double distance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = LocalDateTime.now().format(formatter);
        requestsModel.addRow(new Object[]{time, from, String.format("₹%.2f", fare), 
                                         String.format("%.1f km", distance)});
    }
    
    public void addEarning(String rideId, double fare, String status) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        String date = LocalDateTime.now().format(formatter);
        earningsModel.addRow(new Object[]{date, rideId, String.format("₹%.2f", fare), status});
        
        // Update today's earnings
        updateEarnings(fare);
    }
    
    public void updateStats(double earnings, int trips, double rating) {
        SwingUtilities.invokeLater(() -> {
            earningsTodayLabel.setText(String.format("₹%.2f", earnings));
            totalTripsLabel.setText(String.valueOf(trips));
            ratingLabel.setText(String.format("%.1f", rating));
        });
    }
    
    public void clearRideRequest(int row) {
        if (row >= 0 && row < requestsModel.getRowCount()) {
            requestsModel.removeRow(row);
        }
    }
    
    public void showRideCompleted() {
        hasActiveRide = false;
        rideInfoPanel.setVisible(false);
        startRideBtn.setVisible(false);
        completeRideBtn.setVisible(false);
        cancelRideBtn.setVisible(false);
        navigateBtn.setVisible(false);
        
        mapPanel.clearRoute();
        showSuccess("Ride completed successfully!");
        updateUIState();
    }
    
    // Private helper methods
    private void updateUIState() {
        acceptRideBtn.setVisible(isOnline && !hasActiveRide && requestsModel.getRowCount() > 0);
        completeRideBtn.setVisible(hasActiveRide);
    }
    
    private String formatLocation(GeoPosition pos) {
        return String.format("%.4f, %.4f", pos.getLatitude(), pos.getLongitude());
    }
    
    private void updateEarnings(double amount) {
        // Parse current earnings and add new amount
        String current = earningsTodayLabel.getText().replace("₹", "").trim();
        try {
            double currentEarnings = Double.parseDouble(current);
            currentEarnings += amount;
            earningsTodayLabel.setText(String.format("₹%.2f", currentEarnings));
        } catch (NumberFormatException e) {
            earningsTodayLabel.setText(String.format("₹%.2f", amount));
        }
    }
    
    private void addSampleEarnings() {
        earningsModel.addRow(new Object[]{"12/10", "#R001", "₹450.00", "Completed"});
        earningsModel.addRow(new Object[]{"12/09", "#R002", "₹320.00", "Completed"});
        earningsModel.addRow(new Object[]{"12/09", "#R003", "₹280.00", "Completed"});
        earningsModel.addRow(new Object[]{"12/08", "#R004", "₹550.00", "Completed"});
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Getters for controller
    public JButton getOnlineToggleButton() {
        return onlineToggleBtn;
    }
    
    public JButton getAcceptRideButton() {
        return acceptRideBtn;
    }
    
    public JButton getStartRideButton() {
        return startRideBtn;
    }
    
    public JButton getCompleteRideButton() {
        return completeRideBtn;
    }
    
    public JButton getCancelRideButton() {
        return cancelRideBtn;
    }
    
    public JButton getNavigateButton() {
        return navigateBtn;
    }
    
    public JTable getRideRequestsTable() {
        return requestsTable;
    }
    
    public DefaultTableModel getRideRequestsModel() {
        return requestsModel;
    }
    
    public MapPanel getMapPanel() {
        return mapPanel;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    
    public boolean hasActiveRide() {
        return hasActiveRide;
    }
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    protected void initContent() {
        // Already implemented in initUI()
    }
}