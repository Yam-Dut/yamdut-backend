package org.yamdut.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.jxmapviewer.viewer.GeoPosition;
import org.yamdut.service.RideService;
import org.yamdut.view.dashboard.DriverDashboard;

public class DriverDashboardController {
    private final DriverDashboard view;
    private final RideService rideService;
    
    public DriverDashboardController(DriverDashboard view) {
        this.view = view;
        this.rideService = new RideService();
        
        initializeListeners();
        simulateRideRequests();
    }
    
    private void initializeListeners() {
        // Online toggle
        view.getOnlineToggleButton().addActionListener(e -> {
            view.toggleOnlineStatus();
            if (view.isOnline()) {
                simulateRideRequests();
            }
        });
        
        // Accept ride
        view.getAcceptRideButton().addActionListener(e -> {
            acceptRide();
        });
        
        // Start ride
        view.getStartRideButton().addActionListener(e -> {
            startRide();
        });
        
        // Complete ride
        view.getCompleteRideButton().addActionListener(e -> {
            completeRide();
        });
        
        // Cancel ride
        view.getCancelRideButton().addActionListener(e -> {
            cancelRide();
        });
        
        // Navigate
        view.getNavigateButton().addActionListener(e -> {
            navigateToPickup();
        });
        
        // Ride requests table double-click
        view.getRideRequestsTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    acceptRide();
                }
            }
        });
    }
    
    private void simulateRideRequests() {
        if (!view.isOnline()) return;
        
        Timer requestTimer = new Timer(10000, new ActionListener() {
            private int requestCount = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.isOnline() && !view.hasActiveRide() && requestCount < 3) {
                    String[] locations = {"Thamel", "Boudha", "Patan", "New Road"};
                    double[] fares = {250.0, 320.0, 180.0, 450.0};
                    double[] distances = {2.5, 4.2, 3.1, 5.8};
                    
                    int index = requestCount % locations.length;
                    view.addRideRequest(locations[index], fares[index], distances[index]);
                    requestCount++;
                    
                    if (requestCount >= 3) {
                        ((Timer)e.getSource()).stop();
                    }
                }
            }
        });
        requestTimer.start();
    }
    
    private void acceptRide() {
        int selectedRow = view.getRideRequestsTable().getSelectedRow();
        if (selectedRow < 0) {
            view.showError("Please select a ride request first");
            return;
        }
        
        // Get ride details from table
        String from = (String) view.getRideRequestsModel().getValueAt(selectedRow, 1);
        String fareStr = (String) view.getRideRequestsModel().getValueAt(selectedRow, 2);
        String distanceStr = (String) view.getRideRequestsModel().getValueAt(selectedRow, 3);
        
        double fare = Double.parseDouble(fareStr.replace("₹", "").trim());
        double distance = Double.parseDouble(distanceStr.replace(" km", "").trim());
        
        // Remove from requests
        view.clearRideRequest(selectedRow);
        
        // Simulate ride acceptance
        GeoPosition pickup = new GeoPosition(27.7172, 85.3240); // Thamel
        GeoPosition dropoff = new GeoPosition(27.6966, 85.3591); // Boudha
        
        view.setActiveRide(
            "John Smith", 
            "+977 9841XXXXXX",
            pickup,
            dropoff,
            fare,
            distance,
            15
        );
        
        view.showSuccess("Ride accepted! Passenger details loaded.");
    }
    
    private void startRide() {
        view.showSuccess("Ride started! Heading to pickup location.");
        view.getStartRideButton().setVisible(false);
        view.getCompleteRideButton().setVisible(true);
    }
    
    private void completeRide() {
        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Mark this ride as completed?",
            "Complete Ride",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Add to earnings
            view.addEarning("#R" + System.currentTimeMillis(), 450.0, "Completed");
            view.showRideCompleted();
            view.showSuccess("Ride completed! Payment received.");
            
            // Update stats
            view.updateStats(1250.0, 45, 4.7);
        }
    }
    
    private void cancelRide() {
        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Are you sure you want to cancel this ride?",
            "Cancel Ride",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            view.showRideCompleted();
            view.showInfo("Ride cancelled.");
        }
    }
    
    private void navigateToPickup() {
        view.showInfo("Navigation will open in external app");
    }
}