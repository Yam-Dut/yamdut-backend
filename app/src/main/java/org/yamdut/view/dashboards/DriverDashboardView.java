package org.yamdut.view.dashboards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import org.yamdut.model.Trip;
import java.util.List;

public class DriverDashboardView extends JPanel {
    // Color scheme
    private static final Color BACKGROUND_SILVER = new Color(240, 242, 245);
    private static final Color PANEL_WHITE = new Color(255, 255, 255);
    private static final Color ACCENT_RED = new Color(220, 53, 69);
    private static final Color ACCENT_BLUE = new Color(70, 130, 180);
    private static final Color ACCENT_GREEN = new Color(40, 167, 69);
    private static final Color ACCENT_YELLOW = new Color(255, 193, 7);
    private static final Color BORDER_GRAY = new Color(200, 200, 200);
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);

    // Main panels
    private JPanel mainPanel;
    private JPanel requestsPanel;
    private JPanel mapPanel;
    private JPanel tripPanel;
    
    // Header components
    private JLabel driverNameLabel;
    private JLabel statusLabel;
    private JButton logoutButton;
    
    // Request card components (stored for access)
    private java.util.Map<Trip, JPanel> requestCards = new java.util.HashMap<>();
    private java.util.Map<Trip, JLabel> countdownLabels = new java.util.HashMap<>();
    private java.util.Map<Trip, JLabel> fareLabels = new java.util.HashMap<>();
    
    // Trip tracking components
    private JLabel tripPassengerLabel;
    private JLabel tripLocationLabel;
    private JLabel tripStatusLabel;
    private JLabel tripEtaLabel;
    private JLabel tripDurationLabel;
    private JLabel tripFareLabel;
    private JButton callButton;
    private JButton arrivedButton;
    private JButton completeButton;
    private JButton cancelButton;
    private JEditorPane mapView;
    
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public DriverDashboardView(String driverName) {
        super(new BorderLayout());
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        initializeComponents(driverName);
    }

    private void initializeComponents(String driverName) {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_SILVER);

        // Header
        JPanel headerPanel = createHeaderPanel(driverName);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area with CardLayout
        contentPanel.setBackground(BACKGROUND_SILVER);
        
        // Create different views (left side)
        JPanel requestsView = createRequestsView();
        JPanel tripView = createTripView();
        contentPanel.add(requestsView, "REQUESTS");
        contentPanel.add(tripView, "TRIP");
        
        // Map column on the right (always visible)
        JPanel mapColumn = createMapColumn();
        mapColumn.setPreferredSize(new Dimension(420, 0));
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(mapColumn, BorderLayout.EAST);
        
        add(mainPanel);
        
        // Show requests view by default
        showRequestsView();
    }

    private JPanel createHeaderPanel(String driverName) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TEXT_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setBackground(TEXT_DARK);
        
        JLabel titleLabel = new JLabel("🚖 YamDut - Driver Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        driverNameLabel = new JLabel("Driver: " + driverName);
        driverNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        driverNameLabel.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Status: Online");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(ACCENT_GREEN);
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createHorizontalStrut(30));
        leftPanel.add(driverNameLabel);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(statusLabel);

        logoutButton = createButton("Logout", ACCENT_RED);
        logoutButton.setPreferredSize(new Dimension(120, 40));

        header.add(leftPanel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        return header;
    }

    private JPanel createRequestsView() {
        JPanel view = new JPanel(new BorderLayout(0, 20));
        view.setBackground(BACKGROUND_SILVER);
        view.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("INCOMING RIDE REQUESTS (Auto-expires in 1 min)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_DARK);

        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        requestsPanel.setBackground(BACKGROUND_SILVER);

        JScrollPane scrollPane = new JScrollPane(requestsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        view.add(titleLabel, BorderLayout.NORTH);
        view.add(scrollPane, BorderLayout.CENTER);

        return view;
    }

    private JPanel createMapColumn() {
        JPanel view = new JPanel(new BorderLayout());
        view.setBackground(BACKGROUND_SILVER);
        view.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Live Map");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        view.add(title, BorderLayout.NORTH);

        // Map display
        mapView = new JEditorPane();
        mapView.setEditable(false);
        mapView.setContentType("text/html");
        mapView.setBackground(PANEL_WHITE);
        
        JScrollPane mapScroll = new JScrollPane(mapView);
        mapScroll.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));

        view.add(mapScroll, BorderLayout.CENTER);

        return view;
    }

    private JPanel createTripView() {
        JPanel view = new JPanel(new BorderLayout(0, 15));
        view.setBackground(BACKGROUND_SILVER);
        view.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Trip info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(PANEL_WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        tripStatusLabel = new JLabel("Going to Passenger");
        tripStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tripStatusLabel.setForeground(ACCENT_BLUE);

        tripPassengerLabel = new JLabel("👤 Passenger: ");
        tripPassengerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tripLocationLabel = new JLabel("📍 Pickup: ");
        tripLocationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tripEtaLabel = new JLabel("⏱️ ETA: ");
        tripEtaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tripDurationLabel = new JLabel("🕐 Duration: ");
        tripDurationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tripFareLabel = new JLabel("💰 Fare: ");
        tripFareLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tripFareLabel.setForeground(ACCENT_GREEN);

        infoPanel.add(tripStatusLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(tripPassengerLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(tripLocationLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(tripEtaLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(tripDurationLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(tripFareLabel);

        // Map display for trip
        mapPanel = new JPanel(new BorderLayout());
        mapPanel.setBackground(PANEL_WHITE);
        mapPanel.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));
        mapPanel.setPreferredSize(new Dimension(0, 300));
        
        JLabel mapPlaceholder = new JLabel("🗺️ Map View", SwingConstants.CENTER);
        mapPlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mapPlaceholder.setForeground(TEXT_SECONDARY);
        mapPanel.add(mapPlaceholder);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_SILVER);

        callButton = createButton("📞 Call Passenger", ACCENT_BLUE);
        arrivedButton = createButton("✅ I'm at Pickup Location", ACCENT_GREEN);
        completeButton = createButton("✅ Complete Trip", ACCENT_GREEN);
        cancelButton = createButton("❌ Cancel Trip", ACCENT_RED);

        callButton.setPreferredSize(new Dimension(180, 45));
        arrivedButton.setPreferredSize(new Dimension(220, 45));
        completeButton.setPreferredSize(new Dimension(180, 45));
        cancelButton.setPreferredSize(new Dimension(150, 45));

        buttonPanel.add(callButton);
        buttonPanel.add(arrivedButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(cancelButton);

        view.add(infoPanel, BorderLayout.NORTH);
        view.add(mapPanel, BorderLayout.CENTER);
        view.add(buttonPanel, BorderLayout.SOUTH);

        return view;
    }

    public JPanel createRequestCard(Trip trip) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 15));
        card.setBackground(PANEL_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(PANEL_WHITE);

        JLabel passengerLabel = new JLabel("👤 Passenger: " + trip.getRiderName());
        passengerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passengerLabel.setForeground(TEXT_DARK);

        JLabel pickupLabel = new JLabel("📍 Pickup: " + trip.getPickupLocation());
        pickupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pickupLabel.setForeground(TEXT_DARK);

        JLabel dropLabel = new JLabel("🎯 Drop: " + trip.getDropLocation());
        dropLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dropLabel.setForeground(TEXT_DARK);

        JLabel fareLabel = new JLabel("💰 Fare: Rs " + trip.getAdjustedFare());
        fareLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fareLabel.setForeground(ACCENT_GREEN);
        fareLabels.put(trip, fareLabel);

        infoPanel.add(passengerLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(pickupLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(dropLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(fareLabel);

        // Fare adjustment panel
        JPanel fareAdjustPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fareAdjustPanel.setBackground(PANEL_WHITE);

        JButton decreaseBtn = new JButton("− Decrease");
        decreaseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        decreaseBtn.setBackground(ACCENT_RED);
        decreaseBtn.setForeground(Color.WHITE);
        decreaseBtn.setFocusPainted(false);
        decreaseBtn.setBorderPainted(false);
        decreaseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton increaseBtn = new JButton("+ Increase");
        increaseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        increaseBtn.setBackground(ACCENT_GREEN);
        increaseBtn.setForeground(Color.WHITE);
        increaseBtn.setFocusPainted(false);
        increaseBtn.setBorderPainted(false);
        increaseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        fareAdjustPanel.add(decreaseBtn);
        fareAdjustPanel.add(increaseBtn);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(PANEL_WHITE);

        JButton acceptBtn = createButton("✅ Accept Trip", ACCENT_GREEN);
        JButton rejectBtn = createButton("❌ Reject", ACCENT_RED);

        acceptBtn.setPreferredSize(new Dimension(140, 40));
        rejectBtn.setPreferredSize(new Dimension(110, 40));

        actionPanel.add(acceptBtn);
        actionPanel.add(rejectBtn);

        // Countdown label
        JLabel countdownLabel = new JLabel("⏱️ Expires in: 60 seconds");
        countdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countdownLabel.setForeground(ACCENT_RED);
        countdownLabels.put(trip, countdownLabel);

        // Right panel with buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(PANEL_WHITE);

        rightPanel.add(fareAdjustPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(actionPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(countdownLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        requestCards.put(trip, card);

        // Store button references in card's client properties for controller access
        card.putClientProperty("acceptBtn", acceptBtn);
        card.putClientProperty("rejectBtn", rejectBtn);
        card.putClientProperty("increaseBtn", increaseBtn);
        card.putClientProperty("decreaseBtn", decreaseBtn);

        return card;
    }

    public void displayTripRequests(List<Trip> trips) {
        requestsPanel.removeAll();
        requestCards.clear();
        countdownLabels.clear();
        fareLabels.clear();

        for (Trip trip : trips) {
            JPanel card = createRequestCard(trip);
            requestsPanel.add(card);
            requestsPanel.add(Box.createVerticalStrut(15));
        }

        requestsPanel.revalidate();
        requestsPanel.repaint();
    }

    public void updateCountdown(Trip trip, int secondsLeft) {
        JLabel label = countdownLabels.get(trip);
        if (label != null) {
            label.setText("⏱️ Expires in: " + secondsLeft + " seconds");
            if (secondsLeft <= 10) {
                label.setForeground(ACCENT_RED);
            }
        }
    }

    public void updateFare(Trip trip, int newFare) {
        JLabel label = fareLabels.get(trip);
        if (label != null) {
            label.setText("💰 Fare: Rs " + newFare);
        }
    }

    public void removeRequestCard(Trip trip) {
        JPanel card = requestCards.get(trip);
        if (card != null) {
            requestsPanel.remove(card);
            requestCards.remove(trip);
            countdownLabels.remove(trip);
            fareLabels.remove(trip);
            requestsPanel.revalidate();
            requestsPanel.repaint();
        }
    }

    public void showRequestsView() {
        cardLayout.show(contentPanel, "REQUESTS");
    }

    public void showTripView() {
        cardLayout.show(contentPanel, "TRIP");
    }

    public void updateTripInfo(String status, String passenger, String location, 
                               String eta, String duration, int fare) {
        tripStatusLabel.setText(status);
        tripPassengerLabel.setText("👤 Passenger: " + passenger);
        tripLocationLabel.setText(location);
        tripEtaLabel.setText(eta);
        tripDurationLabel.setText(duration);
        tripFareLabel.setText("💰 Proposed Fare: Rs " + fare);
    }

    public void setTripPhase(Trip.TripStatus status) {
        switch (status) {
            case GOING_TO_PICKUP:
                tripStatusLabel.setText("Going to Passenger");
                tripStatusLabel.setForeground(ACCENT_BLUE);
                arrivedButton.setEnabled(false);
                completeButton.setEnabled(false);
                break;
            case ACCEPTED:
                tripStatusLabel.setText("Trip Accepted");
                tripStatusLabel.setForeground(ACCENT_BLUE);
                arrivedButton.setEnabled(false);
                completeButton.setEnabled(false);
                break;
            case AT_PICKUP:
                tripStatusLabel.setText("At Pickup Location");
                tripStatusLabel.setForeground(ACCENT_YELLOW);
                arrivedButton.setEnabled(true);
                completeButton.setEnabled(false);
                break;
            case IN_PROGRESS:
                tripStatusLabel.setText("Trip in Progress");
                tripStatusLabel.setForeground(ACCENT_GREEN);
                arrivedButton.setEnabled(false);
                completeButton.setEnabled(false);
                break;
            case COMPLETED:
                arrivedButton.setEnabled(false);
                completeButton.setEnabled(true);
                break;
            case CANCELLED:
            case EXPIRED:
                arrivedButton.setEnabled(false);
                completeButton.setEnabled(false);
                break;
        }
    }

    public void enableCompleteButton(boolean enable) {
        completeButton.setEnabled(enable);
    }

    public void showTripCompletionDialog(String passenger, String pickup, String drop,
                                        int duration, double distance, int fare) {
        JDialog dialog = new JDialog(
            (java.awt.Window) SwingUtilities.getWindowAncestor(this),
            "🎉 Trip Completed!",
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("🎉 Trip Completed!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ACCENT_GREEN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(25));

        addInfoLabel(panel, "Passenger: " + passenger);
        addInfoLabel(panel, "From: " + pickup);
        addInfoLabel(panel, "To: " + drop);
        panel.add(Box.createVerticalStrut(15));
        addInfoLabel(panel, "⏱️ Duration: " + (duration / 60) + " min " + (duration % 60) + " sec");
        addInfoLabel(panel, "🛣️ Distance: " + String.format("%.2f", distance) + " km");
        addInfoLabel(panel, "💰 Fare: Rs " + fare);
        panel.add(Box.createVerticalStrut(15));
        addInfoLabel(panel, "Rating: ⭐⭐⭐⭐⭐ (5.0)");

        panel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(PANEL_WHITE);

        JButton detailsBtn = createButton("View Details", ACCENT_BLUE);
        JButton homeBtn = createButton("Back to Home", ACCENT_GREEN);

        detailsBtn.setPreferredSize(new Dimension(140, 40));
        homeBtn.setPreferredSize(new Dimension(150, 40));

        homeBtn.addActionListener(e -> {
            dialog.dispose();
            showRequestsView();
        });

        buttonPanel.add(detailsBtn);
        buttonPanel.add(homeBtn);

        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addInfoLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

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

    // Listener methods
    public void addAcceptListener(Trip trip, ActionListener listener) {
        JPanel card = requestCards.get(trip);
        if (card != null) {
            JButton btn = (JButton) card.getClientProperty("acceptBtn");
            if (btn != null) btn.addActionListener(listener);
        }
    }

    public void addRejectListener(Trip trip, ActionListener listener) {
        JPanel card = requestCards.get(trip);
        if (card != null) {
            JButton btn = (JButton) card.getClientProperty("rejectBtn");
            if (btn != null) btn.addActionListener(listener);
        }
    }

    public void addIncreaseFareListener(Trip trip, ActionListener listener) {
        JPanel card = requestCards.get(trip);
        if (card != null) {
            JButton btn = (JButton) card.getClientProperty("increaseBtn");
            if (btn != null) btn.addActionListener(listener);
        }
    }

    public void addDecreaseFareListener(Trip trip, ActionListener listener) {
        JPanel card = requestCards.get(trip);
        if (card != null) {
            JButton btn = (JButton) card.getClientProperty("decreaseBtn");
            if (btn != null) btn.addActionListener(listener);
        }
    }

    public void addCallListener(ActionListener listener) {
        callButton.addActionListener(listener);
    }

    public void addArrivedListener(ActionListener listener) {
        arrivedButton.addActionListener(listener);
    }

    public void addCompleteListener(ActionListener listener) {
        completeButton.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void updateMapView(String html) {
        if (mapView != null) {
            mapView.setText(html);
            mapView.setCaretPosition(0);
        }
    }
}
