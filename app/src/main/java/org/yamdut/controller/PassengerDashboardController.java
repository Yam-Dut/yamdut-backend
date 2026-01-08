package org.yamdut.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.yamdut.model.RideRequest;
import org.yamdut.service.GeocodingService;
import org.yamdut.service.RideMatchingService;
import org.yamdut.service.RideSimulationService;
import org.yamdut.utils.UserSession;
import org.yamdut.view.dashboard.PassengerDashboard;
import org.yamdut.service.MapService;
import org.yamdut.dao.PassengerDAO;
import org.yamdut.dao.PassengerDAOImpl;
import org.yamdut.model.Passenger;
import org.yamdut.model.User;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.jxmapviewer.viewer.GeoPosition;

public class PassengerDashboardController {

    private final PassengerDashboard view;
    private final RideMatchingService matchingService;
    private final GeocodingService geocodingService;
    private final MapService mapService;
    private final PassengerDAO passengerDAO;

    private User currentUser;
    private Passenger currentPassenger;

    private RideRequest currentRequest;
    private double pickupLat;
    private double pickupLon;
    private double destinationLat;
    private double destinationLon;
    private boolean rideAccepted;
    private boolean approachingNotified;
    private Timer pollTimer;
    private Timer driverTrackTimer;

    private double suggestedFare;
    private double userAdjustedFare;

    private List<Coordinate> approachRouteCache = new ArrayList<>();
    private List<Coordinate> tripRouteCache = new ArrayList<>();

    public PassengerDashboardController(PassengerDashboard view) {
        this.view = view;
        this.matchingService = RideMatchingService.getInstance();
        this.geocodingService = GeocodingService.getInstance();
        this.mapService = new MapService();
        this.passengerDAO = new PassengerDAOImpl();

        this.currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            Passenger dbPassenger = passengerDAO.getPassengerByUserId(currentUser.getId());
            if (dbPassenger == null) {
                // Self-healing: create passenger record if missing
                dbPassenger = new Passenger(currentUser.getId(), currentUser.getFullName(),
                        currentUser.getPhone() != null ? currentUser.getPhone() : "000-000-0000");
                passengerDAO.createPassenger(dbPassenger);
            }
            this.currentPassenger = dbPassenger;
        }

        bindEvents();
        setupLocking();
        initializeLocation();
        startDriverTracking();
    }

    private void setupLocking() {
        view.getPickupCard().showChangeButton(true);
        view.getPickupCard().setLocked(true);
        view.getPickupCard().setChangeAction(e -> {
            view.getPickupCard().setLocked(false);
            view.getPickupCard().showChangeButton(false);
            view.getPickupCard().setStatus("Map click or search enabled", org.yamdut.utils.Theme.INFO_COLOR);
        });
    }

    private void initializeLocation() {
        view.getPickupCard().setStatus("Detecting location...", org.yamdut.utils.Theme.INFO_COLOR);
        SwingWorker<GeocodingService.GeocodeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GeocodingService.GeocodeResult doInBackground() {
                // First get device coordinates
                GeocodingService.GeocodeResult loc = geocodingService.getDeviceLocation();
                // Then get readable address
                return geocodingService.reverseGeocode(loc.getLat(), loc.getLon());
            }

            @Override
            protected void done() {
                try {
                    GeocodingService.GeocodeResult result = get();
                    if (result.isSuccess()) {
                        pickupLat = result.getLat();
                        pickupLon = result.getLon();
                        view.getPickupCard().setAddress(result.getAddress());
                        view.setPickupLocation(result.getAddress(), pickupLat, pickupLon);
                        view.getMapPanel().updateEntityPosition("pickup", pickupLat, pickupLon, "PASSENGER");
                    }
                } catch (Exception e) {
                    System.err.println("[PassengerController] Initial location failed: " + e.getMessage());
                }
            }
        };
        worker.execute();
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

        // Adjust fare button
        view.getTripDetails().getAdjustButton().addActionListener(e -> adjustFare());

        // Map Click Listener
        view.getMapPanel().setMapClickListener(new org.yamdut.view.map.MapPanel.MapClickListener() {
            @Override
            public void onMapClick(double lat, double lon) {
                // Only allow pickup change if not locked
                if (!view.getPickupCard().getSearchButton().isEnabled()) {
                    return; // Locked
                }
                setPickupFromMap(lat, lon);
            }

            @Override
            public void onDestinationSelected(double lat, double lon) {
                setDestinationFromMap(lat, lon);
            }
        });
    }

    private void setPickupFromMap(double lat, double lon) {
        pickupLat = lat;
        pickupLon = lon;
        updateTripPreview(); // Fast preview

        view.getPickupCard().setStatus("Fetching address...", org.yamdut.utils.Theme.INFO_COLOR);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                GeocodingService.GeocodeResult result = geocodingService.reverseGeocode(lat, lon);
                return result.getAddress();
            }

            @Override
            protected void done() {
                try {
                    String address = get();
                    view.getPickupCard().setAddress(address);
                    view.setPickupLocation(address, lat, lon);

                    // Show marker/route
                    if (destinationLat != 0 && destinationLon != 0) {
                        updateRouteOnMap();
                    }
                    // Update Pickup Marker (Blue)
                    view.getMapPanel().updateEntityPosition("pickup", lat, lon, "PASSENGER");

                } catch (Exception e) {
                    view.getPickupCard().setAddress(String.format("%.5f, %.5f", lat, lon));
                }
            }
        };
        worker.execute();
    }

    private void setDestinationFromMap(double lat, double lon) {
        destinationLat = lat;
        destinationLon = lon;
        updateTripPreview(); // Fast preview

        view.getDestinationCard().setStatus("Fetching address...", org.yamdut.utils.Theme.INFO_COLOR);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                GeocodingService.GeocodeResult result = geocodingService.reverseGeocode(lat, lon);
                return result.getAddress();
            }

            @Override
            protected void done() {
                try {
                    String address = get();
                    view.getDestinationCard().setAddress(address);
                    view.setDestinationLocation(address, lat, lon);

                    // Show marker/route
                    if (pickupLat != 0 && pickupLon != 0) {
                        updateRouteOnMap();
                    }
                    // Update Destination Marker (Red)
                    view.getMapPanel().updateEntityPosition("destination", lat, lon, "PASSENGER");

                } catch (Exception e) {
                    view.getDestinationCard().setAddress(String.format("%.5f, %.5f", lat, lon));
                }
            }
        };
        worker.execute();
    }

    private void updateTripPreview() {
        if (pickupLat == 0 || pickupLon == 0 || destinationLat == 0 || destinationLon == 0) {
            view.getTripDetails().reset();
            return;
        }
        double distanceKm = calculateDistance(pickupLat, pickupLon, destinationLat, destinationLon);
        // Trip Preview Rate: Rs. 25 per km. (Approach surcharge added on assignment)
        suggestedFare = Math.round(distanceKm * 25.0);
        userAdjustedFare = suggestedFare;

        view.getTripDetails().setTripDetails(
                String.format("Rs. %.0f*", userAdjustedFare), // * denotes base trip fare
                String.format("%.1f km", distanceKm));
        view.getTripDetails().setAdjustable(true);
        view.getTripDetails().setDriverInfo("Searching...");
    }

    private void adjustFare() {
        String input = JOptionPane.showInputDialog(view,
                String.format("Suggested Trip Fare: Rs. %.0f\nEnter your offer (within +/- 20%%):", suggestedFare),
                userAdjustedFare);

        if (input != null && !input.isEmpty()) {
            try {
                double newFare = Double.parseDouble(input);
                double min = suggestedFare * 0.8;
                double max = suggestedFare * 1.2;

                if (newFare < min || newFare > max) {
                    JOptionPane.showMessageDialog(view,
                            String.format("Fare must be between Rs. %.0f and Rs. %.0f", min, max),
                            "Invalid Range", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                userAdjustedFare = Math.round(newFare);
                view.getTripDetails().setTripDetails(
                        String.format("Rs. %.0f*", userAdjustedFare),
                        String.format("%.1f km",
                                calculateDistance(pickupLat, pickupLon, destinationLat, destinationLon)));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private List<Coordinate> truncateRouteFromPoint(List<Coordinate> route, double lat, double lon) {
        if (route == null || route.isEmpty())
            return new ArrayList<>();

        int closestIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < route.size(); i++) {
            Coordinate c = route.get(i);
            double dist = calculateDistance(lat, lon, c.getLat(), c.getLon());
            if (dist < minDistance) {
                minDistance = dist;
                closestIndex = i;
            }
        }

        // Return sublist from closestIndex to end
        return new ArrayList<>(route.subList(closestIndex, route.size()));
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
                            updateTripPreview(); // Fast preview
                            view.setPickupLocation(result.getAddress(), pickupLat, pickupLon);

                            // Center map and show user marker (Blue)
                            view.getMapPanel().setCenter(pickupLat, pickupLon, 15);
                            view.getMapPanel().updateEntityPosition("pickup", pickupLat, pickupLon, "PASSENGER");

                            // If destination is set, show route
                            if (destinationLat != 0 && destinationLon != 0) {
                                updateRouteOnMap();
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
                            updateTripPreview(); // Fast preview
                            view.setDestinationLocation(result.getAddress(), destinationLat, destinationLon);

                            // Show destination marker (simulated as another passenger type or add
                            // destination type later)
                            // For now just update view, maybe add marker later if MapPanel supports it

                            // Show route if pickup is set
                            if (pickupLat != 0 && pickupLon != 0) {
                                updateRouteOnMap();
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
        String passengerId = currentPassenger != null ? String.valueOf(currentPassenger.getId())
                : (currentUser != null ? String.valueOf(currentUser.getId()) : "1");
        String passengerName = currentPassenger != null ? currentPassenger.getName()
                : (currentUser != null ? currentUser.getFullName() : "Passenger");

        currentRequest = new RideRequest(pickup, destination, pickupLat, pickupLon,
                destinationLat, destinationLon, passengerId, passengerName);
        currentRequest.setFare(userAdjustedFare); // Set the user's offered fare

        try {
            // Submit ride request first
            matchingService.submitRide(currentRequest);
            System.out.println("[PassengerController] Ride request submitted: " + currentRequest);

            // Update Trip Details Card to searching mode
            view.getTripDetails().setSearching(true);
            view.getTripDetails().setDriverInfo("Searching...");

            // Change route to GREEN to indicate active/booked state
            SwingUtilities.invokeLater(() -> {
                java.awt.Color green = new java.awt.Color(46, 204, 113); // Modern green
                view.getMapPanel().setRoute(pickupLat, pickupLon, destinationLat, destinationLon, green);
            });

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
                cancelBtn.addActionListener(e1 -> cancelRide());
            }

            JOptionPane.showMessageDialog(
                    view,
                    "Ride request submitted successfully!\nWaiting for a driver to accept...",
                    "Request Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

            startPolling();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    view,
                    "Failed to book ride: " + ex.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
            view.getTripDetails().reset();
        }
    }

    private void startPolling() {
        if (pollTimer != null && pollTimer.isRunning())
            return;

        pollTimer = new Timer(1000, e -> {
            if (currentRequest == null) {
                stopPolling();
                return;
            }

            RideRequest updated = matchingService.getRideStatus(currentRequest.getId());
            if (updated != null) {
                // 1. Sync driver location and route truncation
                if (("ACCEPTED".equals(updated.getStatus()) || "IN_PROGRESS".equals(updated.getStatus()))
                        && updated.getDriverId() > 0) {

                    view.getMapPanel().updateEntityPosition("driver_" + updated.getDriverId(),
                            updated.getDriverLat(), updated.getDriverLon(), "DRIVER");

                    if ("IN_PROGRESS".equals(updated.getStatus())) {
                        view.getMapPanel().updateEntityPosition("pickup", updated.getDriverLat(),
                                updated.getDriverLon(), "PASSENGER_ONBOARD");

                        // Truncate TRIP route
                        if (!tripRouteCache.isEmpty()) {
                            List<Coordinate> truncated = truncateRouteFromPoint(tripRouteCache,
                                    updated.getDriverLat(), updated.getDriverLon());
                            view.getMapPanel().setRoutePoints("TRIP", truncated, new Color(46, 204, 113));
                            // Clear APPROACH route
                            view.getMapPanel().setRoutePoints("APPROACH", new ArrayList<>(), new Color(155, 89, 182));
                        }
                    } else {
                        // Truncate APPROACH route
                        if (!approachRouteCache.isEmpty()) {
                            List<Coordinate> truncated = truncateRouteFromPoint(approachRouteCache,
                                    updated.getDriverLat(), updated.getDriverLon());
                            view.getMapPanel().setRoutePoints("APPROACH", truncated, new Color(155, 89, 182));
                        }
                    }
                }

                // 2. State transitions
                if ("ACCEPTED".equals(updated.getStatus()) && !rideAccepted) {
                    onRideAccepted(updated);
                } else if ("IN_PROGRESS".equals(updated.getStatus()) && !approachingNotified) {
                    // Logic for "driver is here / ride started"
                    approachingNotified = true;
                    view.getTripDetails().setDriverInfo("Ride in Progress...");
                } else if ("COMPLETED".equals(updated.getStatus())) {
                    onRideCompleted(updated);
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

    public void onRideAccepted(RideRequest request) {
        rideAccepted = true;
        SwingUtilities.invokeLater(() -> {
            view.getTripDetails().setSearching(false);
            view.getTripDetails()
                    .setDriverInfo(request.getDriverName() != null ? request.getDriverName() : "Driver Assigned");
            view.getTripDetails().setTripDetails(String.format("Rs. %.0f", request.getFare()), "Now");

            // Update Map with dual routes
            updateRoutesOnMap(request);

            // Show driver marker immediately
            if (request.getDriverLat() != 0) {
                view.getMapPanel().updateEntityPosition("driver_" + request.getDriverId(),
                        request.getDriverLat(), request.getDriverLon(), "DRIVER");
            }

            JOptionPane.showMessageDialog(
                    view,
                    (request.getDriverName() != null ? request.getDriverName() : "Driver")
                            + " accepted! Coming to pick you up.\nTotal Fare: Rs. "
                            + String.format("%.0f", request.getFare()),
                    "Ride Accepted",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void onRideCompleted(RideRequest updated) {
        stopPolling();
        SwingUtilities.invokeLater(() -> {
            // Clear map
            view.getMapPanel().clearRoutes();
            view.getMapPanel().updateEntityPosition("pickup", 0, 0, "NONE");
            view.getMapPanel().updateEntityPosition("destination", 0, 0, "NONE");
            view.getMapPanel().updateEntityPosition("driver_" + updated.getDriverId(), 0, 0, "NONE");

            // Review dialog
            String msg = String.format("YamDut: Ride Completed!\nYou paid Rs. %.0f\n\nHow was your experience?",
                    updated.getFare());
            Object[] options = { "1 Star", "2 Stars", "3 Stars", "4 Stars", "5 Stars" };
            int rating = JOptionPane.showOptionDialog(view, msg, "Ride Summary",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[4]);

            if (rating != JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(view, "Thank you for your feedback!");
            }

            // UI Reset
            view.getTripDetails().reset();
            view.hideActiveRide();
            clearAll();
            currentRequest = null;
        });
    }

    private void updateRoutesOnMap(RideRequest request) {
        if (request == null)
            return;

        // Route A: Driver to Passenger (APPROACH) - RED
        new javax.swing.SwingWorker<List<Coordinate>, Void>() {
            @Override
            protected List<Coordinate> doInBackground() throws Exception {
                List<org.jxmapviewer.viewer.GeoPosition> points = new ArrayList<>();
                points.add(new org.jxmapviewer.viewer.GeoPosition(request.getDriverLat(), request.getDriverLon()));
                points.add(new org.jxmapviewer.viewer.GeoPosition(request.getPickupLat(), request.getPickupLon()));
                List<org.jxmapviewer.viewer.GeoPosition> route = mapService.fetchRoute(points);
                List<double[]> routeNodes = new ArrayList<>();
                for (org.jxmapviewer.viewer.GeoPosition gp : route) {
                    routeNodes.add(new double[] { gp.getLatitude(), gp.getLongitude() });
                }
                List<double[]> interpolated = RideSimulationService.getInstance().interpolatePoints(routeNodes, 10);
                List<Coordinate> osmPoints = new ArrayList<>();
                for (double[] p : interpolated) {
                    osmPoints.add(new Coordinate(p[0], p[1]));
                }
                return osmPoints;
            }

            @Override
            protected void done() {
                try {
                    List<Coordinate> route = get();
                    approachRouteCache = new ArrayList<>(route); // Cache for stable truncation
                    java.awt.Color purple = new java.awt.Color(155, 89, 182);
                    view.getMapPanel().setRoutePoints("APPROACH", route, purple);
                } catch (Exception e) {
                    java.awt.Color purple = new java.awt.Color(155, 89, 182);
                    view.getMapPanel().setRoute("APPROACH", request.getDriverLat(), request.getDriverLon(),
                            request.getPickupLat(), request.getPickupLon(), purple);
                }
            }
        }.execute();

        // Route B: Pickup to Destination (TRIP) - PURPLE
        new javax.swing.SwingWorker<List<Coordinate>, Void>() {
            @Override
            protected List<Coordinate> doInBackground() throws Exception {
                List<org.jxmapviewer.viewer.GeoPosition> points = new ArrayList<>();
                points.add(new org.jxmapviewer.viewer.GeoPosition(request.getPickupLat(), request.getPickupLon()));
                points.add(new org.jxmapviewer.viewer.GeoPosition(request.getDestLat(), request.getDestLon()));
                List<org.jxmapviewer.viewer.GeoPosition> route = mapService.fetchRoute(points);
                List<double[]> routeNodes = new ArrayList<>();
                for (org.jxmapviewer.viewer.GeoPosition gp : route) {
                    routeNodes.add(new double[] { gp.getLatitude(), gp.getLongitude() });
                }
                List<double[]> interpolated = RideSimulationService.getInstance().interpolatePoints(routeNodes, 10);
                List<Coordinate> osmPoints = new ArrayList<>();
                for (double[] p : interpolated) {
                    osmPoints.add(new Coordinate(p[0], p[1]));
                }
                return osmPoints;
            }

            @Override
            protected void done() {
                try {
                    List<Coordinate> route = get();
                    tripRouteCache = new ArrayList<>(route); // Cache for stable truncation
                    java.awt.Color green = new java.awt.Color(46, 204, 113);
                    view.getMapPanel().setRoutePoints("TRIP", route, green);
                } catch (Exception e) {
                    java.awt.Color green = new java.awt.Color(46, 204, 113);
                    view.getMapPanel().setRoute("TRIP", request.getPickupLat(), request.getPickupLon(),
                            request.getDestLat(), request.getDestLon(), green);
                }
            }
        }.execute();
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
        approachingNotified = false;
        view.getTripDetails().reset();
        view.hideActiveRide();
        view.hideRatingPanel();
    }

    private void updateRouteOnMap() {
        if (pickupLat == 0 || pickupLon == 0 || destinationLat == 0 || destinationLon == 0)
            return;

        SwingWorker<List<Coordinate>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Coordinate> doInBackground() throws Exception {
                List<GeoPosition> points = new ArrayList<>();
                points.add(new GeoPosition(pickupLat, pickupLon));
                points.add(new GeoPosition(destinationLat, destinationLon));

                List<GeoPosition> route = mapService.fetchRoute(points);
                List<Coordinate> osmPoints = new ArrayList<>();
                for (GeoPosition gp : route) {
                    osmPoints.add(new Coordinate(gp.getLatitude(), gp.getLongitude()));
                }
                return osmPoints;
            }

            @Override
            protected void done() {
                try {
                    List<Coordinate> route = get();
                    Color green = new Color(46, 204, 113); // Modern green
                    view.getMapPanel().setRoutePoints(route, green);
                } catch (Exception e) {
                    // Fallback to direct line if OSRM fails
                    System.err.println("[Map] OSRM route failed, falling back to straight line: " + e.getMessage());
                    view.getMapPanel().setRoute(pickupLat, pickupLon, destinationLat, destinationLon,
                            new Color(46, 204, 113));
                }
            }
        };
        worker.execute();
    }

    private void startDriverTracking() {
        if (driverTrackTimer != null && driverTrackTimer.isRunning())
            return;

        driverTrackTimer = new Timer(1000, e -> {
            List<org.yamdut.model.Driver> drivers = matchingService.findAvailableDrivers(null);

            // For the purpose of "passengers should be able to see all drivers"
            // Note: MapPanel.updateEntityPosition handles DRIVER type with bike icon.
            for (org.yamdut.model.Driver d : drivers) {
                if (d.getStatus().equals("ONLINE") && d.getLat() != 0 && d.getLon() != 0) {
                    // Update driver position on map
                    // Key: "driver_" + id to avoid collisions with "ME" or "DEST"
                    view.getMapPanel().updateEntityPosition("driver_" + d.getId(), d.getLat(), d.getLon(), "DRIVER");
                }
            }
        });
        driverTrackTimer.start();
    }

    public RideRequest getCurrentRequest() {
        return currentRequest;
    }
}
