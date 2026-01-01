package org.yamdut.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.yamdut.model.Driver;
import org.yamdut.model.RideRequest;
import org.yamdut.model.User;
import org.yamdut.service.RideMatchingService;
import org.yamdut.service.RideSimulationService;
import org.yamdut.utils.UserSession;
import org.yamdut.view.dashboard.DriverDashboard;

public class DriverDashboardController {

    private final DriverDashboard view;
    private final RideMatchingService matchingService;
    private final RideSimulationService simulationService;
    private final Driver currentDriver;
    private RideRequest currentRide = null;
    private Timer refreshTimer;
    private boolean driverAtPickup = false;
    private boolean tripStarted = false;

    // Driver's simulated current location
    private double driverLat = 27.7172; // Default Kathmandu
    private double driverLon = 85.3240;

    public DriverDashboardController(DriverDashboard view) {
        this.view = view;
        this.matchingService = RideMatchingService.getInstance();
        this.simulationService = RideSimulationService.getInstance();

        // Use real logged-in user
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            this.currentDriver = new Driver("Unknown Driver", "000", 0, 0, "OFFLINE");
            this.currentDriver.setId(0);
        } else {
            this.currentDriver = new Driver(
                    currentUser.getFullName(),
                    currentUser.getPhone(),
                    5.0,
                    0,
                    "OFFLINE");
            this.currentDriver.setId((int) currentUser.getId());
        }

        // Set driver ID for simulation
        simulationService.setDriverId("driver_" + currentDriver.getId());

        bindEvents();
        setupPeriodicRefresh();
    }

    private void bindEvents() {
        view.getOnlineToggle().addActionListener(e -> toggleOnline());
        view.getAcceptRideButton().addActionListener(e -> acceptRide());
        view.getArrivedAtPickupButton().addActionListener(e -> arrivedAtPickup());
        view.getStartTripButton().addActionListener(e -> startTrip());
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
            currentDriver.setStatus("ONLINE");
            matchingService.registerDriver(currentDriver);
            view.setOnline(true);
            System.out.println("[DriverController] Driver went online");
            refreshRequests();

            // Show driver on map at current location
            view.getMapPanel().setCenter(driverLat, driverLon, 14);
            view.getMapPanel().showEntities(String.format(
                    "[{\"id\":\"driver_%d\",\"lat\":%.6f,\"lon\":%.6f,\"type\":\"driver\",\"name\":\"%s\"}]",
                    currentDriver.getId(), driverLat, driverLon, currentDriver.getName()));

            JOptionPane.showMessageDialog(
                    view,
                    "You are now online! You will see ride requests automatically.",
                    "Online",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            currentDriver.setStatus("OFFLINE");
            matchingService.unregisterDriver(currentDriver);
            view.setOnline(false);
            view.getRequestListModel().clear();
            view.getAcceptRideButton().setEnabled(false);
            if (currentRide != null) {
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

            view.getRequestListModel().clear();

            if (requests.isEmpty()) {
                view.getRequestListModel().addElement("No ride requests available");
            } else {
                for (RideRequest request : requests) {
                    String displayText = request.toString();
                    view.getRequestListModel().addElement(displayText);
                }
            }

            int selectedIndex = view.getRequestList().getSelectedIndex();
            view.getAcceptRideButton().setEnabled(
                    selectedIndex >= 0 &&
                            selectedIndex < requests.size() &&
                            currentRide == null);
        });
    }

    private void acceptRide() {
        int index = view.getRequestList().getSelectedIndex();

        if (index == -1) {
            JOptionPane.showMessageDialog(
                    view,
                    "Please select a ride request first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
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
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        matchingService.assignRide(currentRide.getId(), currentDriver.getId(), currentDriver.getName());

        // Show UI for active ride
        view.showActiveRide(
                currentRide.getPickup(),
                currentRide.getDestination(),
                currentRide.getPassengerName());

        // Start Phase 1: Driver moving to Pickup
        startPhase1();

        refreshRequests();

        JOptionPane.showMessageDialog(
                view,
                "Ride accepted! Navigating to pickup location.",
                "Ride Accepted",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void startPhase1() {
        // Phase 1: Driver -> Pickup
        simulationService.startPhase1(
                view.getMapPanel(),
                driverLat, driverLon,
                currentRide.getPickupLat(), currentRide.getPickupLon(),
                () -> {
                    // On arrive at pickup
                    SwingUtilities.invokeLater(() -> {
                        driverAtPickup = true;
                        driverLat = currentRide.getPickupLat();
                        driverLon = currentRide.getPickupLon();
                        JOptionPane.showMessageDialog(
                                view,
                                "You have arrived at the pickup location!\nClick 'Arrived at Pickup' to confirm.",
                                "Arrived at Pickup",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                });
    }

    private void arrivedAtPickup() {
        if (currentRide == null)
            return;

        // Stop Phase 1 simulation if still running
        simulationService.stopRide();

        driverAtPickup = true;
        view.setDriverArrivedAtPickup();

        // Update service status to ARRIVED
        matchingService.arriveAtPickup(currentRide.getId());

        JOptionPane.showMessageDialog(
                view,
                "Passenger notified. Click 'Start Trip' when passenger is in the car.",
                "Ready",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void startTrip() {
        if (currentRide == null || !driverAtPickup)
            return;

        int result = JOptionPane.showConfirmDialog(
                view,
                "Start the trip to " + currentRide.getDestination() + "?",
                "Start Trip",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            tripStarted = true;
            view.setRideStarted(true);

            // Phase 2: Pickup -> Destination
            simulationService.startPhase2(
                    view.getMapPanel(),
                    currentRide.getPickupLat(), currentRide.getPickupLon(),
                    currentRide.getDestLat(), currentRide.getDestLon(),
                    () -> {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    view,
                                    "You have arrived at the destination!",
                                    "Destination Reached",
                                    JOptionPane.INFORMATION_MESSAGE);
                        });
                    });

            JOptionPane.showMessageDialog(
                    view,
                    "Trip started! Navigating to destination.",
                    "Trip Started",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void completeRide() {
        if (currentRide == null)
            return;

        int result = JOptionPane.showConfirmDialog(
                view,
                "Complete the ride?",
                "Complete Ride",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            simulationService.stopRide();

            view.getMapPanel().clearRoute();
            view.getMapPanel().clearMap();
            view.hideActiveRide();

            // Update driver location to destination
            driverLat = currentRide.getDestLat();
            driverLon = currentRide.getDestLon();

            currentRide = null;
            driverAtPickup = false;
            tripStarted = false;

            JOptionPane.showMessageDialog(
                    view,
                    "Ride completed successfully!",
                    "Ride Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            // Show driver at new location
            view.getMapPanel().setCenter(driverLat, driverLon, 14);
            view.getMapPanel().showEntities(String.format(
                    "[{\"id\":\"driver_%d\",\"lat\":%.6f,\"lon\":%.6f,\"type\":\"driver\",\"name\":\"%s\"}]",
                    currentDriver.getId(), driverLat, driverLon, currentDriver.getName()));
        }
    }

    private void cancelRide() {
        if (currentRide != null) {
            simulationService.stopRide();
            view.getMapPanel().clearRoute();
            view.getMapPanel().clearMap();
            view.hideActiveRide();
            currentRide = null;
            driverAtPickup = false;
            tripStarted = false;
        }
    }

    private void setupPeriodicRefresh() {
        refreshTimer = new Timer(3000, e -> {
            if (view.getOnlineToggle().isSelected() && currentRide == null) {
                refreshRequests();
            }
        });
        refreshTimer.start();
    }
}
