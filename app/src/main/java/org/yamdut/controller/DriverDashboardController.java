package org.yamdut.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.yamdut.model.RideRequest;
import org.yamdut.service.RideMatchingService;
import org.yamdut.service.RideSimulationService;
import org.yamdut.view.dashboard.DriverDashboard;

public class DriverDashboardController {

    private final DriverDashboard view;
    private final RideMatchingService matchingService;
    private final RideSimulationService simulationService;
    private RideRequest currentRide = null;
    private Timer refreshTimer;
    private boolean rideStarted = false;

    public DriverDashboardController(DriverDashboard view) {
        this.view = view;
        this.matchingService = RideMatchingService.getInstance();
        this.simulationService = RideSimulationService.getInstance();
        bindEvents();
        setupPeriodicRefresh();
    }
    
    private void bindEvents() {
        view.getOnlineToggle().addActionListener(e -> toggleOnline());
        view.getAcceptRideButton().addActionListener(e -> acceptRide());
        view.getStartRideButton().addActionListener(e -> startRide());
        view.getCompleteRideButton().addActionListener(e -> completeRide());
        
        // Request list selection
        view.getRequestList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = view.getRequestList().getSelectedIndex();
                view.getAcceptRideButton().setEnabled(index >= 0 && currentRide == null);
            }
        });
    }
    
    private void toggleOnline() {
        boolean online = view.getOnlineToggle().isSelected();

        if (online) {
            matchingService.registerDriver(this);
            view.setOnline(true);
            System.out.println("[DriverController] Driver went online");
            // Immediately refresh and then continue with periodic refresh
            refreshRequests();
            JOptionPane.showMessageDialog(
                view,
                "You are now online! You will see ride requests automatically.",
                "Online",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            matchingService.unregisterDriver(this);
            view.setOnline(false);
            view.getRequestListModel().clear();
            view.getAcceptRideButton().setEnabled(false);
            if (currentRide != null) {
                // Cancel current ride if going offline
                cancelRide();
            }
            System.out.println("[DriverController] Driver went offline");
        }
    }

    private void refreshRequests() {
        SwingUtilities.invokeLater(() -> {
            if (!view.getOnlineToggle().isSelected()) {
                return;
            }
            
            var requests = matchingService.getPendingRequests();
            System.out.println("[DriverController] Refreshing requests. Found: " + requests.size());
            System.out.println("[DriverController] All requests in service: " + matchingService.getAllRequestsCount());
            
            // Clear and repopulate
            view.getRequestListModel().clear();

            if (requests.isEmpty()) {
                view.getRequestListModel().addElement("No ride requests available");
            } else {
                for (RideRequest request : requests) {
                    // Show passenger name and locations
                    String displayText = request.toString();
                    view.getRequestListModel().addElement(displayText);
                    System.out.println("[DriverController] Added request: " + displayText);
                }
            }

            // Enable accept button if selection exists and no active ride
            int selectedIndex = view.getRequestList().getSelectedIndex();
            view.getAcceptRideButton().setEnabled(
                selectedIndex >= 0 && 
                selectedIndex < requests.size() &&
                currentRide == null
            );
        });
    }
    
    private void acceptRide() {
        int index = view.getRequestList().getSelectedIndex();

        if (index == -1) {
            JOptionPane.showMessageDialog(
                view,
                "Please select a ride request first",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        var requests = matchingService.getPendingRequests();
        if (index >= requests.size()) {
            return;
        }
        
        currentRide = requests.get(index);
        if (currentRide.isAccepted()) {
            JOptionPane.showMessageDialog(
                view,
                "This ride has already been accepted",
                "Ride Taken",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        matchingService.assignRide(currentRide);

        // Show ride on map and UI
        showRideOnMap(currentRide);
        view.showActiveRide(
            currentRide.getPickup(),
            currentRide.getDestination(),
            "Passenger"
        );
        
        // Notify passenger
        notifyPassengerAccepted();
        
        refreshRequests();
    }
    
    private void showRideOnMap(RideRequest request) {
        // Show route
        view.getMapPanel().setRoute(
            request.getPickupLat(), request.getPickupLon(),
            request.getDestLat(), request.getDestLon()
        );
        
        // Center on pickup
        view.getMapPanel().setCenter(request.getPickupLat(), request.getPickupLon(), 15);
        
        // Show passenger location
        String entitiesJson = String.format(
            "[{\"id\":\"passenger1\",\"lat\":%.6f,\"lon\":%.6f,\"type\":\"passenger\",\"name\":\"Passenger\"}]",
            request.getPickupLat(), request.getPickupLon()
        );
        view.getMapPanel().showEntities(entitiesJson);
    }
    
    private void notifyPassengerAccepted() {
        // In real app, notify passenger through service
        // For demo, this is handled by the matching service
    }
    
    private void startRide() {
        if (currentRide == null) return;
        
        int result = JOptionPane.showConfirmDialog(
            view,
            "Start the ride to " + currentRide.getDestination() + "?",
            "Start Ride",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            rideStarted = true;
            view.setRideStarted(true);
            
            // Start simulation
            double driverLat = currentRide.getPickupLat() + 0.005; // Driver starts slightly away
            double driverLon = currentRide.getPickupLon() + 0.005;
            
            simulationService.startRide(
                view.getMapPanel(),
                driverLat, driverLon,
                currentRide.getPickupLat(), currentRide.getPickupLon(),
                currentRide.getDestLat(), currentRide.getDestLon(),
                () -> {
                    // On complete
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            view,
                            "You have arrived at the destination!",
                            "Arrived",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    });
                }
            );
            
            JOptionPane.showMessageDialog(
                view,
                "Ride started! Driver is navigating to passenger.",
                "Ride Started",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void completeRide() {
        if (currentRide == null) return;
        
        int result = JOptionPane.showConfirmDialog(
            view,
            "Complete the ride?",
            "Complete Ride",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Stop simulation
            simulationService.stopRide();
            
            // Clear map
            view.getMapPanel().clearRoute();
            view.getMapPanel().clearMap();
            
            // Hide active ride
            view.hideActiveRide();
            
            // Notify passenger
            notifyPassengerCompleted();
            
            currentRide = null;
            rideStarted = false;
            
            JOptionPane.showMessageDialog(
                view,
                "Ride completed successfully!",
                "Ride Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void cancelRide() {
        if (currentRide != null) {
            simulationService.stopRide();
            view.getMapPanel().clearRoute();
            view.getMapPanel().clearMap();
            view.hideActiveRide();
            currentRide = null;
            rideStarted = false;
        }
    }
    
    private void notifyPassengerCompleted() {
        // In real app, notify passenger through service
        // For demo, find passenger controller and notify
    }
    
    private void setupPeriodicRefresh() {
        // Refresh requests every 3 seconds when online
        refreshTimer = new Timer(3000, e -> {
            if (view.getOnlineToggle().isSelected() && currentRide == null) {
                refreshRequests();
            }
        });
        refreshTimer.start();
    }
}
