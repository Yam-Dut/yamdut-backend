package org.yamdut.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

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
    private double pickupLat = 0;
    private double pickupLon = 0;
    private double destinationLat = 0;
    private double destinationLon = 0;
    private boolean rideAccepted = false;

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
        
        // Cancel ride will be set up when ride card is shown
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
        
        SwingWorker<GeocodingService.GeocodeResult, Void> worker = 
            new SwingWorker<GeocodingService.GeocodeResult, Void>() {
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
                                JOptionPane.WARNING_MESSAGE
                            );
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
        
        SwingWorker<GeocodingService.GeocodeResult, Void> worker = 
            new SwingWorker<GeocodingService.GeocodeResult, Void>() {
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
                                JOptionPane.WARNING_MESSAGE
                            );
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
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        if (pickupLat == 0 || pickupLon == 0 || destinationLat == 0 || destinationLon == 0) {
            JOptionPane.showMessageDialog(
                view,
                "Please search for locations first",
                "Invalid Locations",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        org.yamdut.model.User currentUser = UserSession.getInstance().getCurrentUser();
        String passengerId = currentUser != null ? 
            String.valueOf(currentUser.getId()) : "passenger1";
        String passengerName = currentUser != null ? 
            currentUser.getFullName() : "Passenger";
        
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
            drivers.forEach(driver ->
                view.getDriverListModel().addElement("  • " + driver.toString())
            );
            
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
            view.getActiveRideCard().setButtonText("❌ Cancel Ride");
            cancelBtn.addActionListener(e -> cancelRide());
        }
        
        JOptionPane.showMessageDialog(
            view,
            "Ride request submitted successfully!\n" +
            (drivers.isEmpty() ? 
                "No drivers are online right now. Your request will be shown to drivers when they go online." :
                drivers.size() + " driver(s) will see your request."),
            "Request Submitted",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showDriversOnMap(java.util.List<?> drivers) {
        // Create entities array for map
        StringBuilder entitiesJson = new StringBuilder("[");
        for (int i = 0; i < drivers.size(); i++) {
            if (i > 0) entitiesJson.append(",");
            // Use approximate driver locations (in real app, get from driver service)
            double driverLat = pickupLat + (Math.random() - 0.5) * 0.01;
            double driverLon = pickupLon + (Math.random() - 0.5) * 0.01;
            entitiesJson.append(String.format(
                "{\"id\":\"driver%d\",\"lat\":%.6f,\"lon\":%.6f,\"type\":\"driver\",\"name\":\"%s\"}",
                i, driverLat, driverLon, drivers.get(i).toString()
            ));
        }
        entitiesJson.append("]");
        
        view.getMapPanel().showEntities(entitiesJson.toString());
    }
    
    public void onRideAccepted() {
        rideAccepted = true;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                view,
                "Driver accepted your ride! They are on the way.",
                "Ride Accepted",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
    
    public void onRideCompleted() {
        SwingUtilities.invokeLater(() -> {
            view.showRatingPanel();
            JOptionPane.showMessageDialog(
                view,
                "Ride completed! Please rate your driver.",
                "Ride Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
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
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Remove request from matching service
            matchingService.cancelRide(currentRequest);
            
            // Clear UI
            clearAll();
            
            JOptionPane.showMessageDialog(
                view,
                "Ride request cancelled successfully.",
                "Request Cancelled",
                JOptionPane.INFORMATION_MESSAGE
            );
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
