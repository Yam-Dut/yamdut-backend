package org.yamdut.view.dashboard;

import org.jxmapviewer.viewer.GeoPosition;
import org.yamdut.service.GeocodingService;
import org.yamdut.service.LocationService;
import org.yamdut.service.RouteService;
import org.yamdut.view.map.MapPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Passenger dashboard with interactive map, control panel, and status bar.
 * Implements pickup/dropoff pins, routing, fare estimates, and stub dialogs
 * for rides/profile.
 */
public class PassengerDashboard extends BaseDashboard {

    // Map
    private MapPanel mapPanel;

    // Locations
    private GeoPosition pickupLocation;
    private GeoPosition dropoffLocation;

    // Services
    private final LocationService locationService = new LocationService();
    private final RouteService routeService = new RouteService();
    private final GeocodingService geocodingService = new GeocodingService();

    // UI components (control panel)
    private JPanel controlPanel;
    private JTextField dropoffAddressField;
    private JLabel pickupAddressLabel;
    private JLabel distanceLabel;
    private JLabel timeLabel;
    private JLabel fareLabel;
    private JLabel driversLabel;
    private JLabel statusLabel;
    private JButton requestRideBtn;
    private JButton clearDestBtn;
    private JButton refreshLocBtn;
    private JButton searchAddressBtn;
    private JButton viewRidesBtn;
    private JButton profileBtn;

    // Floating toggle to show control panel
    private JButton showPanelBtn;

    // Status bar
    private JLabel connectionStatusLabel;
    private JLabel actionStatusLabel;
    private JLabel notificationsLabel;

    public PassengerDashboard() {
        super();
        initContent();
        setWelcomeMessage("Welcome, Passenger!");
        initMapAndLocation();
    }

    @Override
    protected void initContent() {
        setLayout(new BorderLayout());
        add(buildLayeredMapWithToggle(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JComponent buildLayeredMapWithToggle() {
        // Map area
        mapPanel = new MapPanel();

        // Control panel (hidden by default)
        controlPanel = buildControlPanel();
        controlPanel.setVisible(false);

        // Floating toggle button
        showPanelBtn = new JButton("Request a Ride");
        showPanelBtn.addActionListener(e -> {
            controlPanel.setVisible(true);
            showPanelBtn.setVisible(false);
            revalidate();
            repaint();
        });
        JPanel overlay = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        overlay.setOpaque(false);
        overlay.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 0));
        overlay.add(showPanelBtn);
        mapPanel.add(overlay, BorderLayout.NORTH);

        // Wrap map + control in a simple border layout panel (avoids JLayeredPane sizing quirks)
        JPanel container = new JPanel(new BorderLayout());
        container.add(mapPanel, BorderLayout.CENTER);
        container.add(controlPanel, BorderLayout.EAST);
        return container;
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(360, 0));

        // Location section
        JLabel locTitle = new JLabel("Location");
        locTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        locTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(locTitle);

        panel.add(Box.createVerticalStrut(8));

        pickupAddressLabel = new JLabel("Detecting current location...");
        pickupAddressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pickupAddressLabel.setForeground(new Color(99, 110, 123));
        pickupAddressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(pickupAddressLabel);

        panel.add(Box.createVerticalStrut(6));

        dropoffAddressField = new JTextField();
        dropoffAddressField.setEditable(false);
        dropoffAddressField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dropoffAddressField);

        panel.add(Box.createVerticalStrut(6));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchAddressBtn = new JButton("🔍 Search");
        refreshLocBtn = new JButton("📍");
        refreshLocBtn.setToolTipText("Refresh current location");
        searchRow.add(searchAddressBtn);
        searchRow.add(refreshLocBtn);
        panel.add(searchRow);

        panel.add(Box.createVerticalStrut(12));

        // Trip details
        JLabel tripTitle = new JLabel("Trip details");
        tripTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tripTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tripTitle);

        panel.add(Box.createVerticalStrut(6));
        distanceLabel = new JLabel("📏 Distance: --");
        timeLabel = new JLabel("⏱️ Time: --");
        fareLabel = new JLabel("💰 Fare: --");
        driversLabel = new JLabel("🚗 Drivers nearby: --");
        for (JLabel lbl : new JLabel[]{distanceLabel, timeLabel, fareLabel, driversLabel}) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(new Color(80, 90, 100));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lbl);
        }

        panel.add(Box.createVerticalStrut(14));

        // Buttons
        requestRideBtn = new JButton("Request Ride");
        requestRideBtn.setEnabled(false);
        requestRideBtn.setBackground(new Color(46, 204, 113));
        requestRideBtn.setForeground(Color.WHITE);
        requestRideBtn.setFocusPainted(false);
        requestRideBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        clearDestBtn = new JButton("Clear Destination");
        clearDestBtn.setEnabled(false);
        clearDestBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        viewRidesBtn = new JButton("View My Rides");
        profileBtn = new JButton("Profile Settings");

        panel.add(requestRideBtn);
        panel.add(Box.createVerticalStrut(6));
        panel.add(clearDestBtn);
        panel.add(Box.createVerticalStrut(6));
        panel.add(viewRidesBtn);
        panel.add(Box.createVerticalStrut(6));
        panel.add(profileBtn);

        panel.add(Box.createVerticalGlue());

        // Status label
        statusLabel = new JLabel("Ready - click map to set destination");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(80, 90, 100));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(8));

        JButton hidePanelBtn = new JButton("Hide");
        hidePanelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        hidePanelBtn.addActionListener(e -> {
            controlPanel.setVisible(false);
            showPanelBtn.setVisible(true);
            revalidate();
            repaint();
        });
        panel.add(hidePanelBtn);

        // Wire handlers
        wireControlHandlers();
        return panel;
    }

    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3));
        bar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        bar.setBackground(new Color(245, 246, 247));

        connectionStatusLabel = new JLabel("Connected");
        actionStatusLabel = new JLabel("Ready");
        notificationsLabel = new JLabel("Notifications: 0");

        for (JLabel lbl : new JLabel[]{connectionStatusLabel, actionStatusLabel, notificationsLabel}) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(90, 99, 110));
        }

        bar.add(connectionStatusLabel);
        bar.add(actionStatusLabel);
        bar.add(notificationsLabel);
        return bar;
    }

    private void wireControlHandlers() {
        clearDestBtn.addActionListener(e -> clearDestination());

        requestRideBtn.addActionListener(e -> {
            if (validateRideRequest()) {
                JOptionPane.showMessageDialog(this,
                        "Ride requested!\nSearching for drivers...",
                        "Ride Requested",
                        JOptionPane.INFORMATION_MESSAGE);
                actionStatusLabel.setText("Searching for drivers...");
            }
        });

        refreshLocBtn.addActionListener(e -> refreshLocationAsync());

        searchAddressBtn.addActionListener(e -> {
            String address = JOptionPane.showInputDialog(this,
                    "Enter destination address:", "Search Address",
                    JOptionPane.QUESTION_MESSAGE);
            if (address != null && !address.isBlank()) {
                searchAndSetDestination(address.trim());
            }
        });

        viewRidesBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Ride history not yet implemented.", "Info",
                        JOptionPane.INFORMATION_MESSAGE));

        profileBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Profile settings not yet implemented.", "Info",
                        JOptionPane.INFORMATION_MESSAGE));
    }

    private void initMapAndLocation() {
        // Default to Kathmandu valley until location resolves
        pickupLocation = new GeoPosition(27.7172, 85.3240);
        mapPanel.setCenter(pickupLocation.getLatitude(), pickupLocation.getLongitude(), 10);
        updatePickupAddress();

        // Listen for map clicks from WebView (Leaflet)
        mapPanel.setMapClickListener((lat, lng) -> handleMapClick(lat, lng));

        // Load pickup location async
        refreshLocationAsync();
    }

    private void handleMapClick(double lat, double lng) {
        GeoPosition geoPos = new GeoPosition(lat, lng);
        dropoffLocation = geoPos;
        clearDestBtn.setEnabled(true);
        requestRideBtn.setEnabled(true);
        statusLabel.setText("Getting address...");

        // Reverse geocode in background
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return geocodingService.getAddress(geoPos);
            }

            @Override
            protected void done() {
                try {
                    String addr = get();
                    dropoffAddressField.setText(addr);
                } catch (Exception ex) {
                    dropoffAddressField.setText(String.format("%.4f, %.4f",
                            geoPos.getLatitude(), geoPos.getLongitude()));
                }
                statusLabel.setText("Destination set");
            }
        };
        worker.execute();

        updateRouteAsync();
    }

    private void refreshLocationAsync() {
        refreshLocBtn.setEnabled(false);
        actionStatusLabel.setText("Updating location...");
        SwingWorker<GeoPosition, Void> worker = new SwingWorker<>() {
            @Override
            protected GeoPosition doInBackground() {
                return locationService.getCurrentLocation();
            }

            @Override
            protected void done() {
                try {
                    pickupLocation = get();
                    mapPanel.setCenter(pickupLocation.getLatitude(), pickupLocation.getLongitude(), 12);
                    updatePickupAddress();
                    if (dropoffLocation != null) {
                        updateRouteAsync();
                    }
                    statusLabel.setText("Location updated");
                    actionStatusLabel.setText("Ready");
                } catch (Exception ex) {
                    statusLabel.setText("Failed to update location");
                    actionStatusLabel.setText("Offline");
                } finally {
                    refreshLocBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void updatePickupAddress() {
        if (pickupLocation == null) return;
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return geocodingService.getAddress(pickupLocation);
            }

            @Override
            protected void done() {
                try {
                    pickupAddressLabel.setText(get());
                } catch (Exception e) {
                    pickupAddressLabel.setText(String.format("%.4f, %.4f",
                            pickupLocation.getLatitude(), pickupLocation.getLongitude()));
                }
            }
        };
        worker.execute();
    }

    private void searchAndSetDestination(String address) {
        // Forward geocoding not implemented in GeocodingService; inform user
        JOptionPane.showMessageDialog(this,
                "Address search is not available. Please click on the map to set your destination.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Ready");
    }

    private void updateMapPins() {
        // No-op with WebView map; markers handled in JS via setRoute/clearRoute
    }

    private void updateRouteAsync() {
        if (pickupLocation == null || dropoffLocation == null) {
            return;
        }
        statusLabel.setText("Calculating route...");
        SwingWorker<RouteService.RouteResult, Void> worker = new SwingWorker<>() {
            @Override
            protected RouteService.RouteResult doInBackground() throws Exception {
                return routeService.getRoute(pickupLocation, dropoffLocation);
            }

            @Override
            protected void done() {
                try {
                    RouteService.RouteResult result = get();
                    double distanceKm = result.getDistanceKm();
                    int timeMin = (int) Math.ceil(result.getDurationMinutes());
                    double fare = calculateFare(distanceKm);
                    distanceLabel.setText(String.format("📏 Distance: %.2f km", distanceKm));
                    timeLabel.setText(String.format("⏱️ Time: %d min", timeMin));
                    fareLabel.setText(String.format("💰 Fare: $%.2f", fare));
                    mapPanel.setRoute(
                            pickupLocation.getLatitude(), pickupLocation.getLongitude(),
                            dropoffLocation.getLatitude(), dropoffLocation.getLongitude()
                    );
                    statusLabel.setText("Route calculated");
                } catch (Exception ex) {
                    statusLabel.setText("Failed to calculate route");
                }
            }
        };
        worker.execute();
    }

    private boolean validateRideRequest() {
        if (pickupLocation == null) {
            JOptionPane.showMessageDialog(this, "Pickup not set", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (dropoffLocation == null) {
            JOptionPane.showMessageDialog(this, "Select a destination", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        double distance = calculateDistance(pickupLocation, dropoffLocation);
        if (distance < 0.1) {
            JOptionPane.showMessageDialog(this, "Pickup and dropoff are too close", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearDestination() {
        dropoffLocation = null;
        dropoffAddressField.setText("");
        mapPanel.clearRoute();
        distanceLabel.setText("📏 Distance: --");
        timeLabel.setText("⏱️ Time: --");
        fareLabel.setText("💰 Fare: --");
        requestRideBtn.setEnabled(false);
        clearDestBtn.setEnabled(false);
        statusLabel.setText("Ready - click map to set destination");
    }

    private double calculateDistance(GeoPosition a, GeoPosition b) {
        double lat1 = a.getLatitude();
        double lon1 = a.getLongitude();
        double lat2 = b.getLatitude();
        double lon2 = b.getLongitude();
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a1 = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a1), Math.sqrt(1 - a1));
        return R * c;
    }

    private double calculateFare(double distanceKm) {
        double baseFare = 2.5;
        double perKm = 1.2;
        return baseFare + perKm * distanceKm;
    }

    // --------- Expose buttons for controller wiring ----------
    public JButton getShowMapButton() {
        // Map is always visible; reuse refresh to open dialog if needed by controller
        return refreshLocBtn;
    }

    public JButton getBookRideButton() { return requestRideBtn; }
    public JButton getRideHistoryButton() { return viewRidesBtn; }
    public JButton getPaymentMethodsButton() { return profileBtn; }
}

