package org.yamdut.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.jxmapviewer.viewer.GeoPosition;
import org.yamdut.service.LocationService;
import org.yamdut.service.RideService;
import org.yamdut.view.dashboard.PassengerDashboard;

public class PassengerDashboardController {
    private final PassengerDashboard view;
    private final RideService rideService;
    private final LocationService locationService;
    
    public PassengerDashboardController(PassengerDashboard view) {
        this.view = view;
        this.rideService = new RideService();
        this.locationService = new LocationService();
        
        initializeListeners();
    }
    
    private void initializeListeners() {
        // Pickup selection - removed duplicate listener since view already has it
        // Dropoff selection - removed duplicate listener since view already has it
        
        // Use current location
        view.getUseCurrentLocationButton().addActionListener(e -> {
            useCurrentLocation();
        });
        
        // Swap locations
        view.getSwapLocationsButton().addActionListener(e -> {
            swapLocations();
        });
        
        // Request ride
        view.getRequestRideButton().addActionListener(e -> {
            requestRide();
        });
        
        // Cancel ride - removed duplicate since view already has it
        // Track ride - removed duplicate since view already has it
        // Contact driver - removed duplicate since view already has it
        // Call driver - removed duplicate since view already has it
    }
    
    private void useCurrentLocation() {
        try {
            GeoPosition currentLocation = locationService.getCurrentLocation();
            // Update the pickup location field directly
            if (currentLocation != null) {
                view.setPickupLocation(currentLocation);
                JOptionPane.showMessageDialog(view, 
                    "Current location set as pickup point", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, 
                "Unable to get current location: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void swapLocations() {
        GeoPosition pickup = view.getPickupLocation();
        GeoPosition dropoff = view.getDropoffLocation();
        
        if (pickup != null && dropoff != null) {
            view.setPickupLocation(dropoff);
            view.setDropoffLocation(pickup);
            JOptionPane.showMessageDialog(view, 
                "Locations swapped", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(view, 
                "Both pickup and destination must be set to swap", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void requestRide() {
        GeoPosition pickup = view.getPickupLocation();
        GeoPosition dropoff = view.getDropoffLocation();
        
        if (pickup == null || dropoff == null) {
            JOptionPane.showMessageDialog(view, 
                "Please select both pickup and destination locations", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading
        view.getRequestRideButton().setText("Requesting...");
        view.getRequestRideButton().setEnabled(false);
        
        // Simulate ride request
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simulate driver assignment
                view.setRideActive(true);
                view.showDriverInfo("Rajesh Kumar", 4.8, "Toyota Prius • AB 1234", 5);
                view.showDriverOnMap(27.7175, 85.3245, "driver1", "Rajesh Kumar");
                JOptionPane.showMessageDialog(view, 
                    "Ride requested! Driver assigned and arriving in 5 minutes.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                view.getRequestRideButton().setText("Request Ride");
                view.getRequestRideButton().setEnabled(true);
                
                // Add to ride history
                view.addRideToHistory("Pickup Location", "Destination", 250.0);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void cancelRide() {
        // This is already handled in the view
    }
    
    private void trackRide() {
        // This is already handled in the view
    }
    
    private void contactDriver() {
        // This is already handled in the view
    }
    
    private void callDriver() {
        // This is already handled in the view
    }
}