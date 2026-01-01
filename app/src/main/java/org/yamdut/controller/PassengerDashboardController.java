package org.yamdut.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.yamdut.model.RideRequest;
import org.yamdut.service.GeocodingService;
import org.yamdut.service.RideMatchingService;
import org.yamdut.utils.UserSession;
import org.yamdut.view.dashboard.PassengerDashboard;

public class PassengerDashboardController {

    private final PassengerDashboard view;
    private final RideMatchingService matchingService;
    private final GeocodingService geocodingService;

    private RideRequest currentRequest;
    private double pickupLat;
    private double pickupLon;
    private double destinationLat;
    private double destinationLon;
    private boolean rideAccepted;
    private Timer pollTimer;

    public PassengerDashboardController(PassengerDashboard view) {
        this.view = view;
        this.matchingService = RideMatchingService.getInstance();
        this.geocodingService = GeocodingService.getInstance();
        bindEvents();
    }

    private void bindEvents() {
        // Book ride button
        view.getBookRideButton().addActionListener(e -> bookRide());

        // Clear button
        view.getClearButton().addActionListener(e -> clearAll());

        // Pickup location search
        view.getPickupCard().setSearchAction(e -> geocodePickup());

        // Destination location search
        view.getDestinationCard().setSearchAction(e -> geocodeDestination());

        // Map Click Listener
        view.getMapPanel().setMapClickListener(new org.yamdut.view.map.MapPanel.MapClickListener() {
            @Override
            public void onMapClick(double lat, double lon) {
                // Optional: Handle single click if needed
            }

            @Override
            public void onDestinationSelected(double lat, double lon) {
                setDestinationFromMap(lat, lon);
            }
        });
    }

    private void setDestinationFromMap(double lat, double lon) {
        destinationLat = lat;
        destinationLon = lon;

        // Update View Text
        SwingUtilities.invokeLater(() -> {
            view.getDestinationCard().setAddress(String.format("%.5f, %.5f", lat, lon));
            view.setDestinationLocation("Map Location", lat, lon);

            // Show marker/route
            if (pickupLat != 0 && pickupLon != 0) {
                view.showRoute(pickupLat, pickupLon, destinationLat, destinationLon);
                view.getMapPanel().setRoute(pickupLat, pickupLon, destinationLat, destinationLon);
            }
            // Update Destination Marker (Red)
            view.getMapPanel().updateEntityPosition("DEST", lat, lon, "DRIVER"); // Reusing DRIVER color (Red) for Dest
                                                                                 // for now
        });
    }

    private void showDriversOnMap(java.util.List<?> drivers) {
        for (int i = 0; i < drivers.size(); i++) {
            // Use approximate driver locations (in real app, get from driver service)
            double driverLat = pickupLat + (Math.random() - 0.5) * 0.01;
            double driverLon = pickupLon + (Math.random() - 0.5) * 0.01;

            String driverId = "driver_" + i;
            view.getMapPanel().updateEntityPosition(driverId, driverLat, driverLon, "DRIVER");
        }
    }

    private void geocodePickup() {
        String locationName = view.getPickupCard().getAddress();
        if (locationName.isEmpty()) {
            view.getPickupCard().setStatus("Please enter a location",
                    org.yamdut.utils.Theme.ERROR_COLOR);
            return;
        }

        view.getPickupCard().setStatus("Searching...",
                org.yamdut.utils.Theme.INFO_COLOR);
        view.getPickupCard().getSearchButton().setEnabled(false);

        SwingWorker<GeocodingService.GeocodeResult, Void> worker = new SwingWorker<GeocodingService.GeocodeResult, Void>() {
            @Override
            protected GeocodingService.GeocodeResult doInBackground() {
                return geocodingService.geocode(locationName);
            }

            @Override
            protected void done() {
                try {
                    GeocodingService.GeocodeResult result = get();
                    SwingUtilities.invokeLater(() -> {
                        view.getPickupCard().getSearchButton().setEnabled(true);

                        if (result.isSuccess()) {
                            pickupLat = result.getLat();
                            pickupLon = result.getLon();
                            view.setPickupLocation(result.getAddress(), pickupLat, pickupLon);

                            // Center map and show user marker (Blue)
                            view.getMapPanel().setCenter(pickupLat, pickupLon, 15);
                            view.getMapPanel().updateEntityPosition("ME", pickupLat, pickupLon, "PASSENGER");

                            // If destination is set, show route
                            if (destinationLat != 0 && destinationLon != 0) {
                                view.showRoute(pickupLat, pickupLon, destinationLat, destinationLon);
                            }
                        } else {
                            view.getPickupCard().setStatus(result.getAddress(),
                                    org.yamdut.utils.Theme.ERROR_COLOR);
                            JOptionPane.showMessageDialog(
                                    view,
                                    "Could not find location: " + result.getAddress(),
                                    "Location Not Found",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        view.getPickupCard().getSearchButton().setEnabled(true);
                        view.getPickupCard().setStatus("Error: " + e.getMessage(),
                                org.yamdut.utils.Theme.ERROR_COLOR);
                    });
                }
            }
        };
        worker.execute();
    }

    private void geocodeDestination() {
        String locationName = view.getDestinationCard().getAddress();
        if (locationName.isEmpty()) {
            view.getDestinationCard().setStatus("Please enter a location",
                    org.yamdut.utils.Theme.ERROR_COLOR);
            return;
        }

        view.getDestinationCard().setStatus("Searching...",
                org.yamdut.utils.Theme.INFO_COLOR);
        view.getDestinationCard().getSearchButton().setEnabled(false);

        SwingWorker<GeocodingService.GeocodeResult, Void> worker = new SwingWorker<GeocodingService.GeocodeResult, Void>() {
            @Override
            protected GeocodingService.GeocodeResult doInBackground() {
                return geocodingService.geocode(locationName);
            }

            @Override
            protected void done() {
                try {
                    GeocodingService.GeocodeResult result = get();
                    SwingUtilities.invokeLater(() -> {
                        view.getDestinationCard().getSearchButton().setEnabled(true);

                        if (result.isSuccess()) {
                            destinationLat = result.getLat();
                            destinationLon = result.getLon();
                            view.setDestinationLocation(result.getAddress(), destinationLat, destinationLon);

                            // Show destination marker (simulated as another passenger type or add
                            // destination type later)
                            // For now just update view, maybe add marker later if MapPanel supports it

                            // Show route if pickup is set
                            if (pickupLat != 0 && pickupLon != 0) {
                                view.showRoute(pickupLat, pickupLon, destinationLat, destinationLon);
                            }
                        } else {
                            view.getDestinationCard().setStatus(result.getAddress(),
                                    org.yamdut.utils.Theme.ERROR_COLOR);
                            JOptionPane.showMessageDialog(
                                    view,
                                    "Could not find location: " + result.getAddress(),
                                    "Location Not Found",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        view.getDestinationCard().getSearchButton().setEnabled(true);
                        view.getDestinationCard().setStatus("Error: " + e.getMessage(),
                                org.yamdut.utils.Theme.ERROR_COLOR);
                    });
                }
            }
        };
        worker.execute();
    }

    private void bookRide() {
        String pickup = view.getPickupCard().getAddress();
        String destination = view.getDestinationCard().getAddress();

        if (pickup.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Please set both pickup and destination locations",
                    "Missing Locations",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pickupLat == 0 || pickupLon == 0 || destinationLat == 0 || destinationLon == 0) {
            JOptionPane.showMessageDialog(
                    view,
                    "Please search for locations first",
                    "Invalid Locations",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        org.yamdut.model.User currentUser = UserSession.getInstance().getCurrentUser();
        String passengerId = currentUser != null ? String.valueOf(currentUser.getId()) : "passenger1";
        String passengerName = currentUser != null ? currentUser.getFullName() : "Passenger";

        currentRequest = new RideRequest(pickup, destination, pickupLat, pickupLon,
                destinationLat, destinationLon, passengerId, passengerName);

        // Submit ride request first
        matchingService.submitRide(currentRequest);
        System.out.println("[PassengerController] Ride request submitted: " + currentRequest);

        // Check for available drivers (but don't require them)
        var drivers = matchingService.findAvailableDrivers(currentRequest);

        view.getDriverListModel().clear();
        if (drivers.isEmpty()) {
            view.getDriverListModel().addElement("No drivers online. Your request is queued.");
            view.getDriverListModel().addElement("Drivers will see your request when they go online.");
        } else {
            view.getDriverListModel().addElement("Available drivers:");
            drivers.forEach(driver -> view.getDriverListModel().addElement("  • " + driver.toString()));

            // Show drivers on map
            showDriversOnMap(drivers);
        }

        // Show active ride card with cancel option
        view.showActiveRide(pickup, destination, "Waiting for driver...");

        // Setup cancel button listener (remove old listeners first)
        if (view.getActiveRideCard() != null) {
            javax.swing.AbstractButton cancelBtn = view.getActiveRideCard().getActionButton();
            // Remove all existing listeners
            for (java.awt.event.ActionListener al : cancelBtn.getActionListeners()) {
                cancelBtn.removeActionListener(al);
            }
            view.getActiveRideCard().setButtonText("Cancel Ride");
            cancelBtn.addActionListener(e -> cancelRide());
        }

        JOptionPane.showMessageDialog(
                view,
                "Ride request submitted successfully!\n" +
                        (drivers.isEmpty()
                                ? "No drivers are online right now. Your request will be shown to drivers when they go online."
                                : drivers.size() + " driver(s) will see your request."),
                "Request Submitted",
                JOptionPane.INFORMATION_MESSAGE);

        startPolling();
    }

    private void startPolling() {
        if (pollTimer != null && pollTimer.isRunning())
            return;

        pollTimer = new Timer(3000, e -> {
            if (currentRequest == null) {
                stopPolling();
                return;
            }

            RideRequest updated = matchingService.getRideStatus(currentRequest.getId());
            if (updated != null) {
                if ("ACCEPTED".equals(updated.getStatus()) && !rideAccepted) {
                    // Driver accepted
                    onRideAccepted(updated.getDriverName(), updated.getFare());
                } else if ("IN_PROGRESS".equals(updated.getStatus())) {
                    // Driver is approaching (started ride)
                    stopPolling();
                    onDriverApproaching();
                }
            }
        });
        pollTimer.start();
    }

    private void stopPolling() {
        if (pollTimer != null) {
            pollTimer.stop();
            pollTimer = null;
        }
    }

    public void onRideAccepted(String driverName, double fare) {
        rideAccepted = true;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    view,
                    (driverName != null ? driverName : "Driver") + " accepted coming to pick u up\nFare: $"
                            + String.format("%.2f", fare),
                    "Ride Accepted",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void onDriverApproaching() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    view,
                    "Driver is approaching to you",
                    "Driver Arriving",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void onRideCompleted() {
        SwingUtilities.invokeLater(() -> {
            view.showRatingPanel();
            JOptionPane.showMessageDialog(
                    view,
                    "Ride completed! Please rate your driver.",
                    "Ride Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void cancelRide() {
        if (currentRequest == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to cancel this ride request?",
                "Cancel Ride",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Remove request from matching service
            matchingService.cancelRide(currentRequest.getId());

            // Clear UI
            clearAll();

            JOptionPane.showMessageDialog(
                    view,
                    "Ride request cancelled successfully.",
                    "Request Cancelled",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearAll() {
        view.clearLocations();
        pickupLat = 0;
        pickupLon = 0;
        destinationLat = 0;
        destinationLon = 0;
        currentRequest = null;
        rideAccepted = false;
        view.getDriverListModel().clear();
        view.hideActiveRide();
        view.hideRatingPanel();
    }

    public RideRequest getCurrentRequest() {
        return currentRequest;
    }
}
