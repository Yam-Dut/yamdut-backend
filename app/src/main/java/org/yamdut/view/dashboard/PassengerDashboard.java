package org.yamdut.view.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.yamdut.utils.Theme;
import org.yamdut.model.Role;
import org.yamdut.view.components.LocationInputCard;
import org.yamdut.view.components.ModernButton;
import org.yamdut.view.components.RatingPanel;
import org.yamdut.view.components.RideCard;
import org.yamdut.view.components.TripDetailsCard;
import org.yamdut.view.map.MapPanel;

public class PassengerDashboard extends BaseDashboard {
    private LocationInputCard pickupCard;
    private LocationInputCard destinationCard;
    private ModernButton bookRideButton;
    private ModernButton clearButton;

    private TripDetailsCard tripDetailsCard;

    private MapPanel mapPanel;
    private JPanel rideInfoPanel;
    private RideCard activeRideCard;
    private RatingPanel ratingPanel;

    public PassengerDashboard() {
        super();
        setWelcomeMessage("Welcome, Passenger");
        initContent();
    }

    @Override
    protected void initContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left sidebar panel (wider for better UI)
        JPanel leftPanel = createLeftPanel();
        leftPanel.setPreferredSize(new Dimension(420, 0));

        // Map panel (smaller, on the right)
        mapPanel = new MapPanel(Role.PASSENGER);
        mapPanel.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        mapPanel.setPreferredSize(new Dimension(600, 0)); // Smaller map

        // Layout
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(mapPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Theme.BACKGROUND_PRIMARY);

        // Title
        JLabel titleLabel = new JLabel("Book a Ride");
        titleLabel.setFont(Theme.getTitleFont());
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Location cards
        JPanel locationPanel = new JPanel(new GridBagLayout());
        locationPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.weightx = 1.0;

        pickupCard = new LocationInputCard("Pickup Location", "Pickup", Theme.COLOR_PRIMARY);
        destinationCard = new LocationInputCard("Destination", "Dest", Theme.COLOR_ACCENT);

        locationPanel.add(pickupCard, gbc);
        locationPanel.add(destinationCard, gbc);

        // Action buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.gridwidth = GridBagConstraints.REMAINDER;
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.insets = new Insets(0, 0, 8, 0);
        btnGbc.weightx = 1.0;

        bookRideButton = new ModernButton("Book Ride", Theme.COLOR_PRIMARY);
        bookRideButton.setEnabled(false);

        clearButton = new ModernButton("Clear", Theme.TEXT_SECONDARY);
        clearButton.setFont(Theme.getBodyFont());
        clearButton.setPreferredSize(new Dimension(0, 36));

        buttonPanel.add(bookRideButton, btnGbc);
        btnGbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(clearButton, btnGbc);

        // Trip details section
        tripDetailsCard = new TripDetailsCard();
        tripDetailsCard.reset();

        // Active ride panel (initially hidden)
        rideInfoPanel = new JPanel(new BorderLayout(0, 12));
        rideInfoPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        rideInfoPanel.setVisible(false);

        // Rating panel (initially hidden)
        ratingPanel = new RatingPanel();
        ratingPanel.setVisible(false);

        // Active ride panel (initially hidden)
        rideInfoPanel = new JPanel(new BorderLayout(0, 12));
        rideInfoPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        rideInfoPanel.setVisible(false);

        // Rating panel (initially hidden)
        ratingPanel = new RatingPanel();
        ratingPanel.setVisible(false);

        // Assemble left panel
        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setBackground(Theme.BACKGROUND_PRIMARY);
        topSection.add(titleLabel, BorderLayout.NORTH);
        topSection.add(locationPanel, BorderLayout.CENTER);
        topSection.add(buttonPanel, BorderLayout.SOUTH);

        JPanel bottomSection = new JPanel(new BorderLayout(0, 12));
        bottomSection.setBackground(Theme.BACKGROUND_PRIMARY);
        bottomSection.add(rideInfoPanel, BorderLayout.NORTH);
        bottomSection.add(ratingPanel, BorderLayout.CENTER);

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(tripDetailsCard, BorderLayout.CENTER);
        panel.add(bottomSection, BorderLayout.SOUTH);

        return panel;
    }

    public TripDetailsCard getTripDetailsCard() {
        return tripDetailsCard;
    }

    public void setPickupLocation(String address, double lat, double lon) {
        pickupCard.setAddress(address);
        pickupCard.setStatus("Location found", Theme.SUCCESS_COLOR);
        updateBookButtonState();

        // Center map on pickup and add marker
        mapPanel.setCenter(lat, lon, 15);
        mapPanel.showEntities(String.format(
                "[{\"id\":\"pickup\",\"lat\":%.6f,\"lon\":%.6f,\"type\":\"passenger\",\"name\":\"Pickup\"}]",
                lat, lon));
    }

    public void setDestinationLocation(String address, double lat, double lon) {
        destinationCard.setAddress(address);
        destinationCard.setStatus("Location found", Theme.SUCCESS_COLOR);
        updateBookButtonState();

        // Show destination marker
        mapPanel.showEntities(String.format(
                "[{\"id\":\"destination\",\"lat\":%.6f,\"lon\":%.6f,\"type\":\"passenger\",\"name\":\"Destination\"}]",
                lat, lon));

        // Show route if pickup is set
        // Route will be shown by controller
    }

    public void showRoute(double pickupLat, double pickupLon, double destLat, double destLon) {
        mapPanel.setRoute(pickupLat, pickupLon, destLat, destLon);
    }

    public void showRoute(double pickupLat, double pickupLon, double destLat, double destLon, Color color) {
        mapPanel.setRoute(pickupLat, pickupLon, destLat, destLon, color);
    }

    private void updateBookButtonState() {
        boolean canBook = !pickupCard.getAddress().isEmpty() &&
                !destinationCard.getAddress().isEmpty();
        bookRideButton.setEnabled(canBook);
    }

    public void clearLocations() {
        pickupCard.setAddress("");
        destinationCard.setAddress("");
        pickupCard.setStatus("Enter location name", Theme.TEXT_SECONDARY);
        destinationCard.setStatus("Enter location name", Theme.TEXT_SECONDARY);
        bookRideButton.setEnabled(false);
        mapPanel.clearAllRoutes();
        mapPanel.clearMap();
        tripDetailsCard.reset();
    }

    public void showActiveRide(String pickup, String destination, String driverName) {
        activeRideCard = new RideCard(pickup, destination, "Cancel Ride");
        rideInfoPanel.removeAll();
        rideInfoPanel.add(activeRideCard, BorderLayout.CENTER);
        rideInfoPanel.setVisible(true);
        rideInfoPanel.revalidate();
        rideInfoPanel.repaint();
    }

    public void hideActiveRide() {
        rideInfoPanel.setVisible(false);
        rideInfoPanel.removeAll();
        activeRideCard = null;
    }

    public void showRatingPanel() {
        ratingPanel.setVisible(true);
        ratingPanel.reset();
    }

    public void hideRatingPanel() {
        ratingPanel.setVisible(false);
    }

    // Getters
    public LocationInputCard getPickupCard() {
        return pickupCard;
    }

    public LocationInputCard getDestinationCard() {
        return destinationCard;
    }

    public ModernButton getBookRideButton() {
        return bookRideButton;
    }

    public ModernButton getClearButton() {
        return clearButton;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public TripDetailsCard getTripDetails() {
        return tripDetailsCard;
    }

    public RideCard getActiveRideCard() {
        return activeRideCard;
    }

    public RatingPanel getRatingPanel() {
        return ratingPanel;
    }
}
