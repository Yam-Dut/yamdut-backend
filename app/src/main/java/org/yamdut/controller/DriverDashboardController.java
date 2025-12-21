package org.yamdut.controller;

import org.yamdut.view.dashboard.DriverDashboard;
import org.yamdut.model.User;
import org.yamdut.model.Trip;
import org.yamdut.model.Trip.TripStatus;

import javax.swing.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DriverDashboardController {
    private DriverDashboard view;
    private User currentDriver;
    private List<Trip> pendingRequests;
    private Trip currentTrip;
    private Map<Trip, Timer> countdownTimers;
    private Timer movementTimer;
    private Timer durationTimer;
    private int elapsedSeconds;

    // Driver position tracking
    private double currentDriverLat;
    private double currentDriverLng;
    private double targetLat;
    private double targetLng;

    public DriverDashboardController(DriverDashboard view, User currentDriver) {
        this.view = view;
        this.currentDriver = currentDriver;
        this.pendingRequests = new ArrayList<>();
        this.countdownTimers = new HashMap<>();
        
        loadPendingRequests();
        setupListeners();
        displayRequests();
    }

    private void loadPendingRequests() {
        // Hardcoded trip requests with Kathmandu locations
        
        // Trip 1: Basantapur to Baneshwor
        Trip trip1 = new Trip(
            currentDriver.getId(),
            1,
            "Suresh Shrestha",
            "9841234567",
            "Basantapur, Kathmandu",
            27.7045,
            85.3077,
            "Baneshwor, Kathmandu",
            27.6915,
            85.3435,
            450
        );
        
        // Trip 2: Thamel to Airport
        Trip trip2 = new Trip(
            currentDriver.getId(),
            2,
            "Anjali Thapa",
            "9851234568",
            "Thamel, Kathmandu",
            27.7172,
            85.3120,
            "Airport, Kathmandu",
            27.6966,
            85.3591,
            800
        );
        
        // Trip 3: Patan to Bhaktapur
        Trip trip3 = new Trip(
            currentDriver.getId(),
            3,
            "Bikash Rai",
            "9861234569",
            "Patan Durbar Square",
            27.6731,
            85.3261,
            "Bhaktapur Durbar Square",
            27.6720,
            85.4298,
            650
        );
        
        // Trip 4: Boudha to Swayambhu
        Trip trip4 = new Trip(
            currentDriver.getId(),
            4,
            "Sita Gurung",
            "9871234570",
            "Boudhanath Stupa",
            27.7215,
            85.3620,
            "Swayambhunath Temple",
            27.7149,
            85.2906,
            550
        );
        
        // Trip 5: Koteshwor to Balaju
        Trip trip5 = new Trip(
            currentDriver.getId(),
            5,
            "Ram Tamang",
            "9881234571",
            "Koteshwor, Kathmandu",
            27.6769,
            85.3480,
            "Balaju, Kathmandu",
            27.7350,
            85.3000,
            700
        );
        
        pendingRequests.add(trip1);
        pendingRequests.add(trip2);
        pendingRequests.add(trip3);
        pendingRequests.add(trip4);
        pendingRequests.add(trip5);
    }

    private void setupListeners() {
        view.addLogoutListener(e -> handleLogout());
        // Open map in browser helper: prompt for token if needed, then open
        try {
            JButton openBtn = view.getOpenMapBrowserButton();
            if (openBtn != null) {
                openBtn.addActionListener(e -> {
                    String token = JOptionPane.showInputDialog(null,
                            "Enter Mapbox token (leave blank to use MAPBOX_TOKEN env):",
                            "Mapbox Token", JOptionPane.QUESTION_MESSAGE);
                    if (token != null && !token.isBlank()) {
                        view.setMapboxToken(token.trim());
                    }
                    // refresh map and open
                    updateMapView(new double[]{currentDriverLat, currentDriverLng}, "Idle");
                    view.openMapInBrowser();
                });
            }
        } catch (Exception ignored) {}
    }

    private void displayRequests() {
        view.displayTripRequests(pendingRequests);
        
        // Setup listeners and timers for each request
        for (Trip trip : pendingRequests) {
            setupRequestListeners(trip);
            startRequestCountdown(trip);
        }

        // Initialize map with driver's current idle location
        currentDriverLat = 27.7050;
        currentDriverLng = 85.3150;
        updateMapView(null, "Idle");
    }

    private void setupRequestListeners(Trip trip) {
        view.addAcceptListener(trip, e -> acceptTrip(trip));
        view.addRejectListener(trip, e -> rejectTrip(trip));
        view.addIncreaseFareListener(trip, e -> increaseFare(trip));
        view.addDecreaseFareListener(trip, e -> decreaseFare(trip));
    }

    private void startRequestCountdown(Trip trip) {
        final int[] secondsLeft = {60};
        
        Timer timer = new Timer(1000, e -> {
            secondsLeft[0]--;
            view.updateCountdown(trip, secondsLeft[0]);
            
            if (secondsLeft[0] <= 0) {
                ((Timer) e.getSource()).stop();
                expireRequest(trip);
            }
        });
        
        countdownTimers.put(trip, timer);
        timer.start();
    }

    private void increaseFare(Trip trip) {
        int newFare = trip.getAdjustedFare() + 50;
        trip.setAdjustedFare(newFare);
        view.updateFare(trip, newFare);
    }

    private void decreaseFare(Trip trip) {
        int newFare = Math.max(trip.getBaseFare(), trip.getAdjustedFare() - 50);
        trip.setAdjustedFare(newFare);
        view.updateFare(trip, newFare);
    }

    private void acceptTrip(Trip trip) {
        // Stop countdown timer
        Timer timer = countdownTimers.get(trip);
        if (timer != null) {
            timer.stop();
        }
        
        // Remove all other requests
        for (Trip t : new ArrayList<>(pendingRequests)) {
            if (!t.equals(trip)) {
                Timer otherTimer = countdownTimers.get(t);
                if (otherTimer != null) {
                    otherTimer.stop();
                }
                view.removeRequestCard(t);
            }
        }
        
        pendingRequests.clear();
        view.removeRequestCard(trip);
        
        // Set as current trip
        currentTrip = trip;
        currentTrip.setStatus(TripStatus.GOING_TO_PICKUP);
        
        // Initialize driver position (slightly away from passenger)
        currentDriverLat = trip.getPickupLat() - 0.01;
        currentDriverLng = trip.getPickupLng() - 0.01;
        currentTrip.setDriverLat(currentDriverLat);
        currentTrip.setDriverLng(currentDriverLng);
        
        targetLat = trip.getPickupLat();
        targetLng = trip.getPickupLng();
        
        // Show trip view
        startPhase1_GoingToPassenger();
    }

    private void rejectTrip(Trip trip) {
        Timer timer = countdownTimers.get(trip);
        if (timer != null) {
            timer.stop();
        }
        
        pendingRequests.remove(trip);
        countdownTimers.remove(trip);
        view.removeRequestCard(trip);
        
        view.showMessage("Trip rejected", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void expireRequest(Trip trip) {
        trip.setStatus(TripStatus.EXPIRED);
        pendingRequests.remove(trip);
        countdownTimers.remove(trip);
        view.removeRequestCard(trip);
    }

    private void startPhase1_GoingToPassenger() {
        view.showTripView();
        view.setTripPhase(TripStatus.GOING_TO_PICKUP);
        
        double distance = calculateDistance(currentDriverLat, currentDriverLng, 
                                           targetLat, targetLng);
        int eta = (int) (distance * 3); // Rough estimate: 3 min per km
        
        view.updateTripInfo(
            "Going to Passenger",
            currentTrip.getRiderName(),
            "📍 Pickup: " + currentTrip.getPickupLocation(),
            "⏱️ ETA: " + eta + " min | 🛣️ " + String.format("%.2f", distance) + " km",
            "🕐 Duration: --",
            currentTrip.getAdjustedFare()
        );
        
        // Setup trip view listeners
        view.addCallListener(e -> handleCall());
        view.addArrivedListener(e -> handleArrivedAtPickup());
        view.addCompleteListener(e -> handleCompleteTrip());
        view.addCancelListener(e -> handleCancelTrip());
        
        // Start simulating driver movement
        simulateDriverMovement();
        
        // Update map view
        updateMapView(new double[]{currentDriverLat, currentDriverLng},
                      "Going to pickup");
    }

    private void simulateDriverMovement() {
        movementTimer = new Timer(2000, e -> {
            // Calculate direction vector
            double latDiff = targetLat - currentDriverLat;
            double lngDiff = targetLng - currentDriverLng;
            
            // Move 10% of the distance each step
            double step = 0.10;
            currentDriverLat += latDiff * step;
            currentDriverLng += lngDiff * step;
            
            currentTrip.setDriverLat(currentDriverLat);
            currentTrip.setDriverLng(currentDriverLng);
            
            // Calculate remaining distance
            double distance = calculateDistance(currentDriverLat, currentDriverLng, 
                                               targetLat, targetLng);
            
            // Update ETA
            int eta = (int) Math.max(1, distance * 3);
            
            String locationLabel = "";
            if (currentTrip.getStatus() == TripStatus.GOING_TO_PICKUP) {
                locationLabel = "📍 Pickup: " + currentTrip.getPickupLocation();
            } else if (currentTrip.getStatus() == TripStatus.IN_PROGRESS) {
                locationLabel = "🎯 Destination: " + currentTrip.getDropLocation();
            }
            
            view.updateTripInfo(
                currentTrip.getStatus() == TripStatus.GOING_TO_PICKUP ? 
                    "Going to Passenger" : "Trip in Progress",
                currentTrip.getRiderName(),
                locationLabel,
                "⏱️ ETA: " + eta + " min | 🛣️ " + String.format("%.2f", distance) + " km",
                currentTrip.getStatus() == TripStatus.IN_PROGRESS ? 
                    "🕐 Duration: " + formatDuration(elapsedSeconds) : "🕐 Duration: --",
                currentTrip.getAdjustedFare()
            );
            
            // Check if arrived
            if (distance < 0.05) { // Within 50 meters
                if (currentTrip.getStatus() == TripStatus.GOING_TO_PICKUP) {
                    arrivedAtPickup();
                } else if (currentTrip.getStatus() == TripStatus.IN_PROGRESS) {
                    arrivedAtDestination();
                }
            }
            
            updateMapView(new double[]{currentDriverLat, currentDriverLng},
                          currentTrip.getStatus() == TripStatus.GOING_TO_PICKUP ? "Going to pickup" : "Trip in progress");
        });
        
        movementTimer.start();
    }

    private void arrivedAtPickup() {
        currentTrip.setStatus(TripStatus.AT_PICKUP);
        view.setTripPhase(TripStatus.AT_PICKUP);
        view.showMessage("You've arrived at pickup location!", "Arrived", 
                        JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleArrivedAtPickup() {
        if (movementTimer != null) {
            movementTimer.stop();
        }
        
        currentTrip.setArrivedAt(Timestamp.valueOf(LocalDateTime.now()));
        startPhase2_TripInProgress();
    }

    private void startPhase2_TripInProgress() {
        currentTrip.setStatus(TripStatus.IN_PROGRESS);
        currentTrip.setStartedAt(Timestamp.valueOf(LocalDateTime.now()));
        elapsedSeconds = 0;
        
        view.setTripPhase(TripStatus.IN_PROGRESS);
        
        // Update target to destination
        targetLat = currentTrip.getDropoffLat();
        targetLng = currentTrip.getDropoffLng();
        
        double distance = calculateDistance(currentDriverLat, currentDriverLng, 
                                           targetLat, targetLng);
        
        view.updateTripInfo(
            "Trip in Progress",
            currentTrip.getRiderName(),
            "🎯 Destination: " + currentTrip.getDropLocation(),
            "⏱️ ETA: " + (int)(distance * 3) + " min | 🛣️ " + String.format("%.2f", distance) + " km",
            "🕐 Duration: 00:00:00",
            currentTrip.getAdjustedFare()
        );
        
        // Start duration timer
        startDurationTimer();
        
        // Resume movement simulation
        simulateDriverMovement();
        
        updateMapView(new double[]{currentDriverLat, currentDriverLng}, "Trip in progress");
    }

    private void startDurationTimer() {
        durationTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            
            double distance = calculateDistance(currentDriverLat, currentDriverLng, 
                                               targetLat, targetLng);
            int eta = (int) Math.max(1, distance * 3);
            
            view.updateTripInfo(
                "Trip in Progress",
                currentTrip.getRiderName(),
                "🎯 Destination: " + currentTrip.getDropLocation(),
                "⏱️ ETA: " + eta + " min | 🛣️ " + String.format("%.2f", distance) + " km",
                "🕐 Duration: " + formatDuration(elapsedSeconds),
                currentTrip.getAdjustedFare()
            );
        });
        
        durationTimer.start();
    }

    private void arrivedAtDestination() {
        if (movementTimer != null) {
            movementTimer.stop();
        }
        
        view.enableCompleteButton(true);
        view.showMessage("Arrived at destination!", "Arrived", 
                        JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCompleteTrip() {
        if (durationTimer != null) {
            durationTimer.stop();
        }
        if (movementTimer != null) {
            movementTimer.stop();
        }
        
        currentTrip.setEndedAt(Timestamp.valueOf(LocalDateTime.now()));
        currentTrip.setStatus(TripStatus.COMPLETED);
        
        // Calculate total distance
        double totalDistance = calculateDistance(
            currentTrip.getPickupLat(), currentTrip.getPickupLng(),
            currentTrip.getDropoffLat(), currentTrip.getDropoffLng()
        );
        
        currentTrip.setDistance(totalDistance);
        
        // Show completion dialog
        view.showTripCompletionDialog(
            currentTrip.getRiderName(),
            currentTrip.getPickupLocation(),
            currentTrip.getDropLocation(),
            elapsedSeconds,
            totalDistance,
            currentTrip.getAdjustedFare()
        );
        
        // Reset for next trip
        resetForNextTrip();
    }

    private void handleCall() {
        view.showMessage("Calling " + currentTrip.getRiderName() + " at " + 
                        currentTrip.getRiderPhone(), "Call Passenger", 
                        JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCancelTrip() {
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to cancel this trip?",
            "Cancel Trip",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (movementTimer != null) {
                movementTimer.stop();
            }
            if (durationTimer != null) {
                durationTimer.stop();
            }
            
            currentTrip.setStatus(TripStatus.CANCELLED);
            resetForNextTrip();
            view.showMessage("Trip cancelled", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetForNextTrip() {
        currentTrip = null;
        elapsedSeconds = 0;
        
        // Reload new requests
        loadPendingRequests();
        displayRequests();
        view.showRequestsView();
    }

    private void handleLogout() {
        // Stop all timers
        for (Timer timer : countdownTimers.values()) {
            if (timer != null) {
                timer.stop();
            }
        }
        if (movementTimer != null) {
            movementTimer.stop();
        }
        if (durationTimer != null) {
            durationTimer.stop();
        }
        
        view.showMessage("Logged out", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    private void updateMapView(double[] driverCoord, String status) {
        double dLat = driverCoord != null ? driverCoord[0] : currentDriverLat;
        double dLng = driverCoord != null ? driverCoord[1] : currentDriverLng;

        String MAPBOX_TOKEN = System.getenv("MAPBOX_TOKEN");
        if (MAPBOX_TOKEN == null) MAPBOX_TOKEN = "pk.eyJ1IjoiYWJoaXNoZWs2OSIsImEiOiJjbWo2MXBweGsxdGwzM2ZzYmlwMTBmeHV5In0.zuYgQ4F5JiCH6R6znK5T-w";

        // build markers
        String pickupMarker = "";
        String destMarker = "";
        if (currentTrip != null) {
            pickupMarker = String.format(
                "const passengerMarker = new mapboxgl.Marker({color:'red'}).setLngLat([%f,%f]).addTo(map);",
                currentTrip.getPickupLng(), currentTrip.getPickupLat()
            );
            if (currentTrip.getStatus() == TripStatus.IN_PROGRESS || currentTrip.getStatus() == TripStatus.COMPLETED) {
                destMarker = String.format(
                    "const destMarker = new mapboxgl.Marker({color:'green'}).setLngLat([%f,%f]).addTo(map);",
                    currentTrip.getDropoffLng(), currentTrip.getDropoffLat()
                );
            }
        }

        String html;
        if (MAPBOX_TOKEN == null || MAPBOX_TOKEN.isBlank() || MAPBOX_TOKEN.contains("YOUR_MAPBOX_TOKEN")) {
            // Fallback simple view when token is not set
            html = """
            <html><body style='font-family: Segoe UI; padding: 16px;'>
              <h3 style='margin:0 0 8px 0;'>Driver Location</h3>
              <div style='padding:12px; background:#f7fafc; border:1px solid #e2e8f0; border-radius:8px;'>
                <p style='margin:4px 0;'>Status: %s</p>
                <p style='margin:4px 0;'>Driver: %.4f, %.4f</p>
                %s
              </div>
              <p style='color:#718096; font-size:12px; margin-top:10px;'>Set MAPBOX token to see live map.</p>
            </body></html>
            """.formatted(
                status != null ? status : "Idle",
                dLat, dLng,
                currentTrip != null
                    ? String.format("<p style='margin:4px 0;'>Target: %.4f, %.4f</p>", targetLat, targetLng)
                    : ""
            );
        } else {
            html = """
            <html>
            <head>
              <meta charset='utf-8' />
              <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />
              <script src='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.js'></script>
              <link href='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.css' rel='stylesheet' />
            </head>
            <body style='margin:0;padding:0;'>
              <div id='map' style='width:100%%;height:520px;'></div>
              <script>
                mapboxgl.accessToken = '%s';
                const driverPos = [%f,%f];
                const map = new mapboxgl.Map({
                  container: 'map',
                  style: 'mapbox://styles/mapbox/streets-v11',
                  center: driverPos,
                  zoom: 13
                });
                map.addControl(new mapboxgl.NavigationControl());
                const driverMarker = new mapboxgl.Marker({color:'blue'}).setLngLat(driverPos).addTo(map);
                %s
                %s
              </script>
            </body>
            </html>
            """.formatted(MAPBOX_TOKEN, dLng, dLat, pickupMarker, destMarker);
        }

        view.updateMapView(html);
    }
}
