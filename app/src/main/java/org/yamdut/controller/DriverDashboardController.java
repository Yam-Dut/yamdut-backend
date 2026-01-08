package org.yamdut.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.yamdut.model.Driver;
import org.yamdut.model.RideRequest;
import org.yamdut.model.Driver;
import org.yamdut.model.User;
import org.yamdut.service.RideMatchingService;
import org.yamdut.service.RideSimulationService;
import org.yamdut.utils.UserSession;
import org.yamdut.view.dashboard.DriverDashboard;
import org.yamdut.service.MapService;
import org.yamdut.dao.DriverDAO;
import org.yamdut.dao.DriverDAOImpl;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import org.openstreetmap.gui.jmapviewer.Coordinate;

public class DriverDashboardController {

    private final DriverDashboard view;
    private final RideMatchingService matchingService;
    private final RideSimulationService simulationService;
    private final MapService mapService;
    private final DriverDAO driverDAO;
    private Driver currentDriver;
    private RideRequest currentRide = null;
    private Timer refreshTimer;
    private double driverLat = 27.7172; // Default KTM
    private double driverLon = 85.3240;

    public DriverDashboardController(DriverDashboard view) {
        this.view = view;
        this.matchingService = RideMatchingService.getInstance();
        this.simulationService = RideSimulationService.getInstance();
        this.mapService = new MapService();
        this.driverDAO = new DriverDAOImpl();

        // Use real logged-in user
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Fallback for testing without login
            this.currentDriver = new Driver("Unknown Driver", "000", 0, 0, "OFFLINE");
            this.currentDriver.setId(0);
        } else {
            // Fetch driver from DB by user ID
            Driver dbDriver = driverDAO.getDriverByUserId(currentUser.getId());
            if (dbDriver == null) {
                // Self-healing: Create driver record if it doesn't exist (handle legacy users)
                dbDriver = new Driver(
                        currentUser.getId(),
                        currentUser.getFullName(),
                        currentUser.getPhone() != null ? currentUser.getPhone() : "000-000-0000",
                        "Bike", // Default
                        "NOT-SET", // Default
                        "OFFLINE");
                driverDAO.createDriver(dbDriver);
                System.out.println(
                        "[DriverController] Created missing Driver record for User ID: " + currentUser.getId());
            }
            this.currentDriver = dbDriver;
        }

        initializeLocation();
        bindEvents();
        setupPeriodicRefresh();
    }

    private void initializeLocation() {
        // Simple simulation of driver location detection
        new javax.swing.SwingWorker<org.yamdut.service.GeocodingService.GeocodeResult, Void>() {
            @Override
            protected org.yamdut.service.GeocodingService.GeocodeResult doInBackground() {
                var gs = org.yamdut.service.GeocodingService.getInstance();
                return gs.getDeviceLocation();
            }

            @Override
            protected void done() {
                try {
                    var res = get();
                    if (res.isSuccess()) {
                        driverLat = res.getLat();
                        driverLon = res.getLon();
                        view.getMapPanel().updateEntityPosition("ME", driverLat, driverLon, "DRIVER");
                        view.getMapPanel().setCenter(driverLat, driverLon, 14);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }.execute();
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
            currentDriver.setStatus("ONLINE");
            currentDriver.setLat(driverLat);
            currentDriver.setLon(driverLon);
            matchingService.registerDriver(currentDriver);
            view.setOnline(true);
            System.out.println("[DriverController] Driver went online");
            // Immediately refresh and then continue with periodic refresh
            refreshRequests();
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

        if (currentDriver.getId() <= 0) {
            JOptionPane.showMessageDialog(view,
                    "Your driver profile is not correctly initialized. Please restart the app.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Apply a realistic departure offset for simulation first (~1.5km away from
        // pickup)
        double offset = 0.015;
        driverLat = currentRide.getPickupLat() + offset;
        driverLon = currentRide.getPickupLon() + offset;

        // Ensure currentDriver model is updated
        currentDriver.setLat(driverLat);
        currentDriver.setLon(driverLon);
        matchingService.registerDriver(currentDriver);

        try {
            matchingService.assignRide(currentRide.getId(), currentDriver.getId(), currentDriver.getName(), driverLat,
                    driverLon);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Failed to accept ride: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // Clear previous "ME" marker and show unified ride markers
        view.getMapPanel().removeEntityMarker("ME");
        showRideOnMap(currentRide);
        view.showActiveRide(
                currentRide.getPickup(),
                currentRide.getDestination(),
                currentRide.getPassengerName());

        // Start Leg 1 Simulation: Move to Pickup (Follow Roads)
        new javax.swing.SwingWorker<List<double[]>, Void>() {
            @Override
            protected List<double[]> doInBackground() throws Exception {
                List<org.jxmapviewer.viewer.GeoPosition> points = new ArrayList<>();
                points.add(new org.jxmapviewer.viewer.GeoPosition(driverLat, driverLon));
                points.add(
                        new org.jxmapviewer.viewer.GeoPosition(currentRide.getPickupLat(), currentRide.getPickupLon()));
                List<org.jxmapviewer.viewer.GeoPosition> route = mapService.fetchRoute(points);
                List<double[]> routePoints = new ArrayList<>();
                for (org.jxmapviewer.viewer.GeoPosition gp : route) {
                    routePoints.add(new double[] { gp.getLatitude(), gp.getLongitude() });
                }
                // Pre-interpolate for index-based sync
                return simulationService.interpolatePoints(routePoints, 10);
            }

            @Override
            protected void done() {
                try {
                    final List<double[]> simulationRoute = get();
                    simulationService.startRide(
                            view.getMapPanel(),
                            "driver_" + currentDriver.getId(),
                            simulationRoute,
                            (point, index) -> {
                                driverLat = point[0];
                                driverLon = point[1];
                                currentDriver.setLat(driverLat);
                                currentDriver.setLon(driverLon);
                                matchingService.registerDriver(currentDriver);

                                // Sync to DB for passenger visibility
                                matchingService.updateRideLocation(currentRide.getId(), driverLat, driverLon);

                                // Dynamic Route Cleanup: delete the path behind the driver
                                List<org.openstreetmap.gui.jmapviewer.Coordinate> remaining = new java.util.ArrayList<>();
                                for (int i = index; i < simulationRoute.size(); i++) {
                                    remaining.add(
                                            new org.openstreetmap.gui.jmapviewer.Coordinate(simulationRoute.get(i)[0],
                                                    simulationRoute.get(i)[1]));
                                }
                                view.getMapPanel().setRoutePoints("APPROACH", remaining,
                                        new java.awt.Color(155, 89, 182));
                            },
                            () -> {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(view, "You have arrived at the pickup location!");
                                });
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        // Notify passenger
        notifyPassengerAccepted();

        refreshRequests();
    }

    private void showRideOnMap(RideRequest request) {
        // Show route
        updateRouteOnMap(request);

        // Center on pickup
        view.getMapPanel().setCenter(request.getPickupLat(), request.getPickupLon(), 15);

        // Explicitly show pickup and destination markers
        view.getMapPanel().updateEntityPosition("pickup", request.getPickupLat(), request.getPickupLon(), "PASSENGER");
        view.getMapPanel().updateEntityPosition("destination", request.getDestLat(), request.getDestLon(), "PASSENGER");

        // Show driver marker initially
        view.getMapPanel().updateEntityPosition("driver_" + currentDriver.getId(), driverLat, driverLon, "DRIVER");
    }

    private void notifyPassengerAccepted() {
        // In real app, notify passenger through service
        // For demo, this is handled by the matching service
    }

    private void startRide() {
        if (currentRide == null)
            return;

        int result = JOptionPane.showConfirmDialog(
                view,
                "Start the ride to " + currentRide.getDestination() + "?",
                "Start Ride",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // Notify service
            matchingService.startRide(currentRide.getId());

            // Start Leg 2 Simulation: Move to Destination (Follow Roads)
            new javax.swing.SwingWorker<List<double[]>, Void>() {
                @Override
                protected List<double[]> doInBackground() throws Exception {
                    List<org.jxmapviewer.viewer.GeoPosition> points = new ArrayList<>();
                    points.add(new org.jxmapviewer.viewer.GeoPosition(driverLat, driverLon));
                    points.add(
                            new org.jxmapviewer.viewer.GeoPosition(currentRide.getDestLat(), currentRide.getDestLon()));
                    List<org.jxmapviewer.viewer.GeoPosition> route = mapService.fetchRoute(points);
                    List<double[]> routePoints = new ArrayList<>();
                    for (org.jxmapviewer.viewer.GeoPosition gp : route) {
                        routePoints.add(new double[] { gp.getLatitude(), gp.getLongitude() });
                    }
                    // Pre-interpolate for index-based sync
                    return simulationService.interpolatePoints(routePoints, 10);
                }

                @Override
                protected void done() {
                    try {
                        final List<double[]> simulationRoute = get();
                        simulationService.startRide(
                                view.getMapPanel(),
                                "driver_" + currentDriver.getId(),
                                simulationRoute,
                                (point, index) -> {
                                    driverLat = point[0];
                                    driverLon = point[1];
                                    currentDriver.setLat(driverLat);
                                    currentDriver.setLon(driverLon);
                                    matchingService.registerDriver(currentDriver);

                                    // Sync to DB
                                    matchingService.updateRideLocation(currentRide.getId(), driverLat, driverLon);

                                    // Move passenger with driver (Onboarded)
                                    view.getMapPanel().updateEntityPosition("pickup", driverLat, driverLon,
                                            "PASSENGER_ONBOARD");

                                    // Dynamic Route Cleanup: delete the path behind the driver
                                    List<org.openstreetmap.gui.jmapviewer.Coordinate> remaining = new java.util.ArrayList<>();
                                    for (int i = index - 1; i < simulationRoute.size(); i++) {
                                        remaining.add(new org.openstreetmap.gui.jmapviewer.Coordinate(
                                                simulationRoute.get(i)[0],
                                                simulationRoute.get(i)[1]));
                                    }
                                    view.getMapPanel().setRoutePoints("TRIP", remaining,
                                            new java.awt.Color(46, 204, 113));
                                },
                                () -> {
                                    // On complete
                                    matchingService.completeRide(currentRide.getId());

                                    SwingUtilities.invokeLater(() -> {
                                        // Clear map on driver side
                                        view.getMapPanel().clearRoutes();
                                        view.getMapPanel().updateEntityPosition("pickup", 0, 0, "NONE");
                                        view.getMapPanel().updateEntityPosition("destination", 0, 0, "NONE");

                                        JOptionPane.showMessageDialog(
                                                view,
                                                "You have arrived at the destination!\nRide completed successfully.",
                                                "Arrived",
                                                JOptionPane.INFORMATION_MESSAGE);

                                        currentRide = null;
                                        refreshRequests();
                                    });
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();

            JOptionPane.showMessageDialog(
                    view,
                    "Ride started! Driver is navigating to passenger.",
                    "Ride Started",
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
            // Stop simulation
            simulationService.stopRide();

            // Clear map
            view.getMapPanel().clearAllRoutes();
            view.getMapPanel().clearMap();

            // Hide active ride
            view.hideActiveRide();

            // Notify service
            matchingService.completeRide(currentRide.getId());

            currentRide = null;

            JOptionPane.showMessageDialog(
                    view,
                    "Ride completed successfully!",
                    "Ride Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateRouteOnMap(RideRequest request) {
        if (request == null)
            return;

        // Route A: Driver to Passenger (APPROACH) - RED
        new javax.swing.SwingWorker<List<Coordinate>, Void>() {
            @Override
            protected List<Coordinate> doInBackground() throws Exception {
                List<org.jxmapviewer.viewer.GeoPosition> points = new ArrayList<>();
                points.add(new org.jxmapviewer.viewer.GeoPosition(driverLat, driverLon));
                points.add(new org.jxmapviewer.viewer.GeoPosition(request.getPickupLat(), request.getPickupLon()));
                List<org.jxmapviewer.viewer.GeoPosition> route = mapService.fetchRoute(points);
                List<Coordinate> osmPoints = new ArrayList<>();
                for (org.jxmapviewer.viewer.GeoPosition gp : route) {
                    osmPoints.add(new Coordinate(gp.getLatitude(), gp.getLongitude()));
                }
                return osmPoints;
            }

            @Override
            protected void done() {
                try {
                    List<Coordinate> route = get();
                    view.getMapPanel().setRoutePoints("APPROACH", route, Color.RED);
                } catch (Exception e) {
                    view.getMapPanel().setRoute("APPROACH", driverLat, driverLon,
                            request.getPickupLat(), request.getPickupLon(), Color.RED);
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
                List<Coordinate> osmPoints = new ArrayList<>();
                for (org.jxmapviewer.viewer.GeoPosition gp : route) {
                    osmPoints.add(new Coordinate(gp.getLatitude(), gp.getLongitude()));
                }
                return osmPoints;
            }

            @Override
            protected void done() {
                try {
                    List<Coordinate> route = get();
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

    private void cancelRide() {
        if (currentRide != null) {
            simulationService.stopRide();
            view.getMapPanel().clearAllRoutes();
            view.getMapPanel().clearMap();
            view.hideActiveRide();
            currentRide = null;
        }
    }

    private void setupPeriodicRefresh() {
        // Refresh requests every 3 seconds when online
        refreshTimer = new Timer(3000, e -> {
            if (view.getOnlineToggle() != null && view.getOnlineToggle().isSelected() && currentRide == null) {
                refreshRequests();
            }
        });
        refreshTimer.start();
    }

}
