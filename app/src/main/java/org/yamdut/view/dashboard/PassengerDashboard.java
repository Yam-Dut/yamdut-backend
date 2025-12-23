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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.jxmapviewer.viewer.GeoPosition;
import org.yamdut.utils.Theme;
import org.yamdut.view.components.PrimaryButton;
import org.yamdut.view.map.MapPanel;

public class PassengerDashboard extends BaseDashboard {
    // UI Components
    private MapPanel mapPanel;
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private JPanel rideControlPanel;
    private JPanel locationPanel;
    private JPanel driverInfoPanel;
    private JPanel rideHistoryPanel;
    
    // Location fields
    private JLabel pickupLocationLabel;
    private JLabel dropoffLocationLabel;
    private JTextField pickupField;
    private JTextField dropoffField;
    private JButton pickupSelectBtn;
    private JButton dropoffSelectBtn;
    private JButton swapLocationsBtn;
    private JButton useCurrentLocationBtn;
    
    // Ride control
    private JButton requestRideBtn;
    private JButton cancelRideBtn;
    private JButton trackRideBtn;
    private JLabel rideStatusLabel;
    private JLabel etaLabel;
    private JLabel fareEstimateLabel;
    private JProgressBar rideProgressBar;
    
    // Driver info
    private JLabel driverNameLabel;
    private JLabel driverRatingLabel;
    private JLabel vehicleInfoLabel;
    private JLabel driverEtaLabel;
    private JPanel driverAvatarPanel;
    private JButton contactDriverBtn;
    private JButton callDriverBtn;
    
    // Ride history
    private JTable rideHistoryTable;
    private DefaultTableModel historyModel;
    
    // State
    private GeoPosition pickupLocation;
    private GeoPosition dropoffLocation;
    private boolean isRideActive = false;
    private boolean isSelectingPickup = false;
    private boolean isSelectingDropoff = false;
    
    public PassengerDashboard() {
        super();
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_PRIMARY);
        
        initUI();
        initMap();
        updateUIState();
    }
    
    private void initUI() {
        // Create main container with sidebar
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
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
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.BACKGROUND_SECONDARY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel headerLabel = new JLabel("Book a Ride");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Add wallet/balance button
        JButton walletBtn = new JButton("₹ 250.00");
        walletBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        walletBtn.setForeground(Theme.COLOR_PRIMARY);
        walletBtn.setBackground(Theme.BACKGROUND_SECONDARY);
        walletBtn.setBorder(new LineBorder(Theme.COLOR_PRIMARY, 1));
        walletBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        walletBtn.setFocusPainted(false);
        walletBtn.addActionListener(e -> showWalletDialog());
        headerPanel.add(walletBtn, BorderLayout.EAST);
        
        sidebar.add(headerPanel, BorderLayout.NORTH);
        
        // Location Selection Panel
        locationPanel = createLocationPanel();
        sidebar.add(locationPanel, BorderLayout.CENTER);
        
        // Ride Control Panel
        rideControlPanel = createRideControlPanel();
        sidebar.add(rideControlPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    private JPanel createLocationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND_SECONDARY);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Pickup Section
        JPanel pickupSection = new JPanel(new BorderLayout());
        pickupSection.setBackground(Theme.BACKGROUND_SECONDARY);
        pickupSection.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel pickupTitle = new JLabel("Pickup Location");
        pickupTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pickupTitle.setForeground(Theme.TEXT_SECONDARY);
        pickupSection.add(pickupTitle, BorderLayout.NORTH);
        
        JPanel pickupInputPanel = new JPanel(new BorderLayout());
        pickupInputPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        pickupInputPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 8)
        ));
        
        pickupField = new JTextField();
        pickupField.setEditable(false);
        pickupField.setBorder(null);
        pickupField.setBackground(Theme.BACKGROUND_PRIMARY);
        pickupField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pickupField.setText("Select pickup point");
        
        pickupSelectBtn = new JButton("📍");
        pickupSelectBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pickupSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
        pickupSelectBtn.setBorder(null);
        pickupSelectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pickupSelectBtn.setFocusPainted(false);
        pickupSelectBtn.addActionListener(e -> togglePickupSelection());
        
        pickupInputPanel.add(pickupField, BorderLayout.CENTER);
        pickupInputPanel.add(pickupSelectBtn, BorderLayout.EAST);
        pickupSection.add(pickupInputPanel, BorderLayout.CENTER);
        
        pickupLocationLabel = new JLabel("Not set");
        pickupLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pickupLocationLabel.setForeground(Theme.TEXT_SECONDARY);
        pickupSection.add(pickupLocationLabel, BorderLayout.SOUTH);
        
        // Use Current Location Button
        useCurrentLocationBtn = new JButton("📍 Use Current Location");
        useCurrentLocationBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        useCurrentLocationBtn.setForeground(Theme.COLOR_PRIMARY);
        useCurrentLocationBtn.setBackground(Theme.BACKGROUND_SECONDARY);
        useCurrentLocationBtn.setBorder(null);
        useCurrentLocationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        useCurrentLocationBtn.setFocusPainted(false);
        useCurrentLocationBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        useCurrentLocationBtn.addActionListener(e -> useCurrentLocation());
        
        // Dropoff Section
        JPanel dropoffSection = new JPanel(new BorderLayout());
        dropoffSection.setBackground(Theme.BACKGROUND_SECONDARY);
        dropoffSection.setBorder(new EmptyBorder(20, 0, 15, 0));
        
        JLabel dropoffTitle = new JLabel("Destination");
        dropoffTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dropoffTitle.setForeground(Theme.TEXT_SECONDARY);
        dropoffSection.add(dropoffTitle, BorderLayout.NORTH);
        
        JPanel dropoffInputPanel = new JPanel(new BorderLayout());
        dropoffInputPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        dropoffInputPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 8)
        ));
        
        dropoffField = new JTextField();
        dropoffField.setEditable(false);
        dropoffField.setBorder(null);
        dropoffField.setBackground(Theme.BACKGROUND_PRIMARY);
        dropoffField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dropoffField.setText("Select destination");
        
        dropoffSelectBtn = new JButton("📍");
        dropoffSelectBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dropoffSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
        dropoffSelectBtn.setBorder(null);
        dropoffSelectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dropoffSelectBtn.setFocusPainted(false);
        dropoffSelectBtn.addActionListener(e -> toggleDropoffSelection());
        
        dropoffInputPanel.add(dropoffField, BorderLayout.CENTER);
        dropoffInputPanel.add(dropoffSelectBtn, BorderLayout.EAST);
        dropoffSection.add(dropoffInputPanel, BorderLayout.CENTER);
        
        dropoffLocationLabel = new JLabel("Not set");
        dropoffLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dropoffLocationLabel.setForeground(Theme.TEXT_SECONDARY);
        dropoffSection.add(dropoffLocationLabel, BorderLayout.SOUTH);
        
        // Swap Locations Button
        swapLocationsBtn = new JButton("⇄ Swap Locations");
        swapLocationsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        swapLocationsBtn.setForeground(Theme.COLOR_PRIMARY);
        swapLocationsBtn.setBackground(Theme.BACKGROUND_SECONDARY);
        swapLocationsBtn.setBorder(null);
        swapLocationsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        swapLocationsBtn.setFocusPainted(false);
        swapLocationsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        swapLocationsBtn.addActionListener(e -> swapLocations());
        
        // Add components to main panel
        panel.add(pickupSection);
        panel.add(Box.createVerticalStrut(5));
        panel.add(useCurrentLocationBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(dropoffSection);
        panel.add(Box.createVerticalStrut(10));
        panel.add(swapLocationsBtn);
        
        return panel;
    }
    
    private JPanel createRideControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND_SECONDARY);
        
        // Ride Status
        rideStatusLabel = new JLabel("Ready to ride");
        rideStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rideStatusLabel.setForeground(Theme.TEXT_SECONDARY);
        rideStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // ETA and Fare
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setBackground(Theme.BACKGROUND_SECONDARY);
        
        JLabel etaTitle = new JLabel("ETA:");
        etaTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        etaTitle.setForeground(Theme.TEXT_SECONDARY);
        
        etaLabel = new JLabel("-- min");
        etaLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etaLabel.setForeground(Theme.TEXT_PRIMARY);
        
        JLabel fareTitle = new JLabel("Estimated Fare:");
        fareTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fareTitle.setForeground(Theme.TEXT_SECONDARY);
        
        fareEstimateLabel = new JLabel("₹ --");
        fareEstimateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        fareEstimateLabel.setForeground(Theme.TEXT_PRIMARY);
        
        infoPanel.add(etaTitle);
        infoPanel.add(etaLabel);
        infoPanel.add(fareTitle);
        infoPanel.add(fareEstimateLabel);
        
        // Ride Progress Bar
        rideProgressBar = new JProgressBar();
        rideProgressBar.setMaximum(100);
        rideProgressBar.setValue(0);
        rideProgressBar.setForeground(Theme.COLOR_PRIMARY);
        rideProgressBar.setBackground(Theme.BACKGROUND_PRIMARY);
        rideProgressBar.setBorder(new EmptyBorder(5, 0, 5, 0));
        rideProgressBar.setVisible(false);
        
        // Control Buttons
        PrimaryButton requestBtn = new PrimaryButton("Request Ride");
        requestRideBtn = requestBtn.getButton();
        requestRideBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        requestRideBtn.setEnabled(false);
        requestRideBtn.addActionListener(e -> requestRide());
        
        cancelRideBtn = new JButton("Cancel Ride");
        cancelRideBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelRideBtn.setForeground(Theme.ERROR_COLOR);
        cancelRideBtn.setBackground(Theme.BACKGROUND_SECONDARY);
        cancelRideBtn.setBorder(new LineBorder(Theme.ERROR_COLOR, 1));
        cancelRideBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelRideBtn.setFocusPainted(false);
        cancelRideBtn.setVisible(false);
        cancelRideBtn.addActionListener(e -> cancelRide());
        
        trackRideBtn = new JButton("Track Ride");
        trackRideBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        trackRideBtn.setForeground(Theme.COLOR_PRIMARY);
        trackRideBtn.setBackground(Theme.BACKGROUND_SECONDARY);
        trackRideBtn.setBorder(new LineBorder(Theme.COLOR_PRIMARY, 1));
        trackRideBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        trackRideBtn.setFocusPainted(false);
        trackRideBtn.setVisible(false);
        trackRideBtn.addActionListener(e -> trackRide());
        
        // Add components
        panel.add(rideStatusLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(rideProgressBar);
        panel.add(Box.createVerticalStrut(15));
        panel.add(requestRideBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(cancelRideBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(trackRideBtn);
        
        return panel;
    }
    
    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        // Map Panel
        mapPanel = new MapPanel();
        panel.add(mapPanel, BorderLayout.CENTER);
        
        // Floating Driver Info Panel
        driverInfoPanel = createDriverInfoPanel();
        driverInfoPanel.setVisible(false);
        
        // Floating Ride History Panel
        rideHistoryPanel = createRideHistoryPanel();
        
        // Position panels
        JPanel mapOverlayPanel = new JPanel(new BorderLayout());
        mapOverlayPanel.setOpaque(false);
        
        // Top-right: Driver info
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.add(driverInfoPanel);
        
        // Bottom-right: Ride history
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.setOpaque(false);
        bottomRightPanel.add(rideHistoryPanel);
        
        mapOverlayPanel.add(topRightPanel, BorderLayout.NORTH);
        mapOverlayPanel.add(bottomRightPanel, BorderLayout.SOUTH);
        panel.add(mapOverlayPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDriverInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(280, 350));
        
        // Header
        JLabel header = new JLabel("Driver Assigned");
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(Theme.TEXT_PRIMARY);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Driver Avatar (placeholder)
        driverAvatarPanel = new JPanel();
        driverAvatarPanel.setPreferredSize(new Dimension(80, 80));
        driverAvatarPanel.setBackground(Theme.COLOR_PRIMARY);
        driverAvatarPanel.setBorder(new LineBorder(Theme.BORDER_COLOR, 1));
        driverAvatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Driver Info
        JPanel driverInfo = new JPanel();
        driverInfo.setLayout(new BoxLayout(driverInfo, BoxLayout.Y_AXIS));
        driverInfo.setBackground(Theme.BACKGROUND_SECONDARY);
        
        driverNameLabel = new JLabel("--");
        driverNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        driverNameLabel.setForeground(Theme.TEXT_PRIMARY);
        driverNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        driverRatingLabel = new JLabel("★ --");
        driverRatingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        driverRatingLabel.setForeground(Theme.TEXT_SECONDARY);
        driverRatingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        vehicleInfoLabel = new JLabel("-- • --");
        vehicleInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehicleInfoLabel.setForeground(Theme.TEXT_SECONDARY);
        vehicleInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        driverEtaLabel = new JLabel("--");
        driverEtaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        driverEtaLabel.setForeground(Theme.COLOR_PRIMARY);
        driverEtaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        driverInfo.add(driverNameLabel);
        driverInfo.add(Box.createVerticalStrut(5));
        driverInfo.add(driverRatingLabel);
        driverInfo.add(Box.createVerticalStrut(5));
        driverInfo.add(vehicleInfoLabel);
        driverInfo.add(Box.createVerticalStrut(10));
        driverInfo.add(driverEtaLabel);
        
        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(Theme.BACKGROUND_SECONDARY);
        
        contactDriverBtn = new JButton("Message");
        contactDriverBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contactDriverBtn.setBackground(Theme.BACKGROUND_SECONDARY);
        contactDriverBtn.setBorder(new LineBorder(Theme.COLOR_PRIMARY, 1));
        contactDriverBtn.setForeground(Theme.COLOR_PRIMARY);
        contactDriverBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contactDriverBtn.setFocusPainted(false);
        contactDriverBtn.addActionListener(e -> contactDriver());
        
        callDriverBtn = new JButton("Call");
        callDriverBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        callDriverBtn.setBackground(Theme.COLOR_PRIMARY);
        callDriverBtn.setBorder(null);
        callDriverBtn.setForeground(Color.WHITE);
        callDriverBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        callDriverBtn.setFocusPainted(false);
        callDriverBtn.addActionListener(e -> callDriver());
        
        actionPanel.add(contactDriverBtn);
        actionPanel.add(callDriverBtn);
        
        // Add components
        panel.add(header);
        panel.add(Box.createVerticalStrut(15));
        panel.add(driverAvatarPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(driverInfo);
        panel.add(Box.createVerticalStrut(15));
        panel.add(actionPanel);
        
        return panel;
    }
    
    private JPanel createRideHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(300, 200));
        
        JLabel title = new JLabel("Recent Rides");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Theme.TEXT_PRIMARY);
        
        // Create table model
        String[] columns = {"Date", "From", "To", "Fare"};
        historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        rideHistoryTable = new JTable(historyModel);
        rideHistoryTable.setRowHeight(30);
        rideHistoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rideHistoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(rideHistoryTable);
        scrollPane.setBorder(null);
        
        // Add sample data
        addSampleRideHistory();
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void initMap() {
        // Set default location (Kathmandu)
        pickupLocation = new GeoPosition(27.7172, 85.3240);
        mapPanel.setCenter(pickupLocation.getLatitude(), pickupLocation.getLongitude(), 13);
        updatePickupLocationLabel();
        
        // Set map click listener
        mapPanel.setMapClickListener((lat, lng) -> {
            GeoPosition clickedPos = new GeoPosition(lat, lng);
            
            if (isSelectingPickup) {
                setPickupLocation(clickedPos);
                isSelectingPickup = false;
                pickupSelectBtn.setText("📍");
                pickupSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
                showInfo("Pickup location selected");
            } else if (isSelectingDropoff) {
                setDropoffLocation(clickedPos);
                isSelectingDropoff = false;
                dropoffSelectBtn.setText("📍");
                dropoffSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
                showInfo("Destination selected");
            } else {
                // Default: select dropoff
                setDropoffLocation(clickedPos);
                showInfo("Destination selected");
            }
        });
    }
    
    // Button action methods
    private void togglePickupSelection() {
        isSelectingPickup = !isSelectingPickup;
        isSelectingDropoff = false;
        
        if (isSelectingPickup) {
            pickupSelectBtn.setText("✓");
            pickupSelectBtn.setBackground(new Color(46, 204, 113));
            dropoffSelectBtn.setText("📍");
            dropoffSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
            showInfo("Click on map to select pickup location");
        } else {
            pickupSelectBtn.setText("📍");
            pickupSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
        }
    }
    
    private void toggleDropoffSelection() {
        isSelectingDropoff = !isSelectingDropoff;
        isSelectingPickup = false;
        
        if (isSelectingDropoff) {
            dropoffSelectBtn.setText("✓");
            dropoffSelectBtn.setBackground(new Color(46, 204, 113));
            pickupSelectBtn.setText("📍");
            pickupSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
            showInfo("Click on map to select destination");
        } else {
            dropoffSelectBtn.setText("📍");
            dropoffSelectBtn.setBackground(Theme.BACKGROUND_PRIMARY);
        }
    }
    
    private void useCurrentLocation() {
        // For now, use Kathmandu as current location
        GeoPosition currentLocation = new GeoPosition(27.7172, 85.3240);
        setPickupLocation(currentLocation);
        showInfo("Current location set as pickup point");
    }
    
    private void swapLocations() {
        if (pickupLocation != null && dropoffLocation != null) {
            GeoPosition temp = pickupLocation;
            pickupLocation = dropoffLocation;
            dropoffLocation = temp;
            
            // Update fields
            setPickupLocation(pickupLocation);
            setDropoffLocation(dropoffLocation);
            showInfo("Locations swapped");
        } else {
            showError("Both pickup and destination must be set to swap");
        }
    }
    
    private void requestRide() {
        if (pickupLocation == null || dropoffLocation == null) {
            showError("Please select both pickup and destination locations");
            return;
        }
        
        // Show loading
        requestRideBtn.setText("Requesting...");
        requestRideBtn.setEnabled(false);
        
        // Simulate ride request
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(2000); // Simulate API call
                
                // Simulate driver assignment
                setRideActive(true);
                showDriverInfo("Rajesh Kumar", 4.8, "Toyota Prius • AB 1234", 5);
                mapPanel.addDriverMarker(27.7175, 85.3245, "driver1", "Rajesh Kumar");
                showSuccess("Ride requested! Driver assigned and arriving in 5 minutes.");
                
                // Add to ride history
                addRideToHistory("Pickup Location", "Destination", 250.0);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                requestRideBtn.setText("Request Ride");
                requestRideBtn.setEnabled(true);
            }
        });
    }
    
    private void cancelRide() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this ride?",
            "Cancel Ride",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            setRideActive(false);
            mapPanel.clearMap();
            showInfo("Ride cancelled successfully");
        }
    }
    
    private void trackRide() {
        showInfo("Real-time tracking will be implemented soon");
    }
    
    private void contactDriver() {
        showInfo("Messaging feature will be implemented soon");
    }
    
    private void callDriver() {
        showInfo("Calling feature will be implemented soon");
    }
    
    // Public methods for controller interaction
    
    public void setPickupLocation(GeoPosition location) {
        this.pickupLocation = location;
        pickupField.setText(String.format("%.5f, %.5f", location.getLatitude(), location.getLongitude()));
        updatePickupLocationLabel();
        updateUIState();
        mapPanel.setCenter(location.getLatitude(), location.getLongitude(), 13);
        mapPanel.addPickupMarker(location.getLatitude(), location.getLongitude(), "pickup");
    }
    
    public void setDropoffLocation(GeoPosition location) {
        this.dropoffLocation = location;
        dropoffField.setText(String.format("%.5f, %.5f", location.getLatitude(), location.getLongitude()));
        updateDropoffLocationLabel();
        updateUIState();
        mapPanel.addDestinationMarker(location.getLatitude(), location.getLongitude(), "destination");
        
        // Show route if both locations are set
        if (pickupLocation != null && dropoffLocation != null) {
            showRoute();
        }
    }
    
    public void showRoute() {
        if (pickupLocation != null && dropoffLocation != null) {
            mapPanel.showRoute(
                pickupLocation.getLatitude(), pickupLocation.getLongitude(),
                dropoffLocation.getLatitude(), dropoffLocation.getLongitude()
            );
            estimateFareAndETA();
        }
    }
    
    public void showDriverOnMap(double driverLat, double driverLon, String driverId, String driverName) {
        mapPanel.addDriverMarker(driverLat, driverLon, driverId, driverName);
    }
    
    public void updateRideProgress(int progress) {
        rideProgressBar.setValue(progress);
        rideProgressBar.setVisible(true);
    }
    
    public void showDriverInfo(String driverName, double rating, String vehicleInfo, int etaMinutes) {
        driverNameLabel.setText(driverName);
        driverRatingLabel.setText(String.format("★ %.1f", rating));
        vehicleInfoLabel.setText(vehicleInfo);
        driverEtaLabel.setText(String.format("Arriving in %d min", etaMinutes));
        driverInfoPanel.setVisible(true);
    }
    
    public void setRideActive(boolean active) {
        this.isRideActive = active;
        updateUIState();
        
        if (active) {
            rideStatusLabel.setText("Ride in progress");
            rideStatusLabel.setForeground(Theme.COLOR_PRIMARY);
            requestRideBtn.setVisible(false);
            cancelRideBtn.setVisible(true);
            trackRideBtn.setVisible(true);
        } else {
            rideStatusLabel.setText("Ready to ride");
            rideStatusLabel.setForeground(Theme.TEXT_SECONDARY);
            requestRideBtn.setVisible(true);
            cancelRideBtn.setVisible(false);
            trackRideBtn.setVisible(false);
            driverInfoPanel.setVisible(false);
        }
    }
    
    public void addRideToHistory(String from, String to, double fare) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        String date = LocalDateTime.now().format(formatter);
        historyModel.addRow(new Object[]{date, from, to, String.format("₹%.2f", fare)});
    }
    
    // Private helper methods
    private void updatePickupLocationLabel() {
        if (pickupLocation != null) {
            pickupLocationLabel.setText(String.format("%.5f, %.5f", 
                pickupLocation.getLatitude(), pickupLocation.getLongitude()));
        }
    }
    
    private void updateDropoffLocationLabel() {
        if (dropoffLocation != null) {
            dropoffLocationLabel.setText(String.format("%.5f, %.5f", 
                dropoffLocation.getLatitude(), dropoffLocation.getLongitude()));
        }
    }
    
    private void updateUIState() {
        boolean canRequest = (pickupLocation != null && dropoffLocation != null);
        requestRideBtn.setEnabled(canRequest);
        
        if (canRequest) {
            rideStatusLabel.setText("Ready to request ride");
        } else {
            rideStatusLabel.setText("Select pickup and destination");
        }
    }
    
    private void estimateFareAndETA() {
        if (pickupLocation != null && dropoffLocation != null) {
            // Simple estimation based on distance
            double distance = calculateDistance(
                pickupLocation.getLatitude(), pickupLocation.getLongitude(),
                dropoffLocation.getLatitude(), dropoffLocation.getLongitude()
            );
            
            int etaMinutes = (int) (distance * 2); // Rough estimate
            double fare = distance * 10; // Rough fare calculation
            
            etaLabel.setText(String.format("%d min", etaMinutes));
            fareEstimateLabel.setText(String.format("₹%.2f", fare));
        }
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for distance calculation
        double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                  Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                  Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
    
    private void addSampleRideHistory() {
        historyModel.addRow(new Object[]{"12/10 14:30", "Thamel", "Tribhuvan Airport", "₹450.00"});
        historyModel.addRow(new Object[]{"12/09 09:15", "Boudha", "Patan", "₹320.00"});
        historyModel.addRow(new Object[]{"12/08 18:45", "New Road", "Swayambhu", "₹280.00"});
        historyModel.addRow(new Object[]{"12/07 11:20", "Kathmandu Durbar", "Bhaktapur", "₹550.00"});
    }
    
    private void showWalletDialog() {
        JOptionPane.showMessageDialog(this, 
            "Wallet Balance: ₹250.00\nAdd Money | View Transactions",
            "My Wallet",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Getters for controller
    public JButton getRequestRideButton() {
        return requestRideBtn;
    }
    
    public JButton getCancelRideButton() {
        return cancelRideBtn;
    }
    
    public JButton getTrackRideButton() {
        return trackRideBtn;
    }
    
    public JButton getPickupSelectButton() {
        return pickupSelectBtn;
    }
    
    public JButton getDropoffSelectButton() {
        return dropoffSelectBtn;
    }
    
    public JButton getSwapLocationsButton() {
        return swapLocationsBtn;
    }
    
    public JButton getUseCurrentLocationButton() {
        return useCurrentLocationBtn;
    }
    
    public JButton getContactDriverButton() {
        return contactDriverBtn;
    }
    
    public JButton getCallDriverButton() {
        return callDriverBtn;
    }
    
    public GeoPosition getPickupLocation() {
        return pickupLocation;
    }
    
    public GeoPosition getDropoffLocation() {
        return dropoffLocation;
    }
    
    public MapPanel getMapPanel() {
        return mapPanel;
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