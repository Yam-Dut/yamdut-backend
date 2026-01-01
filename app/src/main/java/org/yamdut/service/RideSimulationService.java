package org.yamdut.service;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import org.yamdut.view.map.MapPanel;

public class RideSimulationService {
    private static RideSimulationService instance;
    private Timer simulationTimer;
    private MapPanel mapPanel;
    private List<double[]> routePoints;
    private int currentIndex = 0;
    private String driverId = "driver1";
    private Runnable onComplete;

    private RideSimulationService() {
    }

    public static RideSimulationService getInstance() {
        if (instance == null) {
            instance = new RideSimulationService();
        }
        return instance;
    }

    public void setDriverId(String id) {
        this.driverId = id;
    }

    /**
     * Phase 1: Driver moves from their location to the Pickup location.
     */
    public void startPhase1(
            MapPanel mapPanel,
            double driverLat, double driverLon,
            double pickupLat, double pickupLon,
            Runnable onComplete) {
        this.mapPanel = mapPanel;
        this.onComplete = onComplete;
        this.routePoints = new ArrayList<>();

        // Generate route for Phase 1
        generateRoutePoints(driverLat, driverLon, pickupLat, pickupLon);

        // Show driver marker at start
        mapPanel.showEntities(String.format(
                "[{\"id\":\"%s\",\"lat\":%.6f,\"lon\":%.6f,\"type\":\"driver\",\"name\":\"Driver\"}]",
                driverId, driverLat, driverLon));

        // Show route on map
        mapPanel.setRoute(driverLat, driverLon, pickupLat, pickupLon);

        currentIndex = 0;
        startAnimation();
    }

    /**
     * Phase 2: Driver moves from Pickup to Destination.
     */
    public void startPhase2(
            MapPanel mapPanel,
            double pickupLat, double pickupLon,
            double destLat, double destLon,
            Runnable onComplete) {
        this.mapPanel = mapPanel;
        this.onComplete = onComplete;
        this.routePoints = new ArrayList<>();

        // Generate route for Phase 2
        generateRoutePoints(pickupLat, pickupLon, destLat, destLon);

        // Show route on map
        mapPanel.setRoute(pickupLat, pickupLon, destLat, destLon);

        currentIndex = 0;
        startAnimation();
    }

    /**
     * Legacy method for combined simulation (kept for backwards compatibility).
     */
    public void startRide(
            MapPanel mapPanel,
            double driverLat, double driverLon,
            double pickupLat, double pickupLon,
            double destLat, double destLon,
            Runnable onComplete) {
        this.mapPanel = mapPanel;
        this.onComplete = onComplete;
        this.routePoints = new ArrayList<>();

        generateRoutePoints(driverLat, driverLon, pickupLat, pickupLon);
        generateRoutePoints(pickupLat, pickupLon, destLat, destLon);

        currentIndex = 0;
        startAnimation();
    }

    private void generateRoutePoints(double fromLat, double fromLon, double toLat, double toLon) {
        int steps = 20;
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            double lat = fromLat + (toLat - fromLat) * ratio;
            double lon = fromLon + (toLon - fromLon) * ratio;
            routePoints.add(new double[] { lat, lon });
        }
    }

    private void startAnimation() {
        if (simulationTimer != null) {
            simulationTimer.stop();
        }

        simulationTimer = new Timer(200, e -> {
            if (routePoints == null || currentIndex >= routePoints.size()) {
                stopRide();
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }

            double[] point = routePoints.get(currentIndex++);
            mapPanel.updateEntityPosition(driverId, point[0], point[1]);

            // Center map on driver
            mapPanel.setCenter(point[0], point[1], 15);
        });

        simulationTimer.start();
    }

    public void stopRide() {
        if (simulationTimer != null) {
            simulationTimer.stop();
            simulationTimer = null;
        }
        currentIndex = 0;
        routePoints = null;
    }

    public boolean isRunning() {
        return simulationTimer != null && simulationTimer.isRunning();
    }

    /**
     * Get current driver position for sharing with passenger map.
     */
    public double[] getCurrentPosition() {
        if (routePoints != null && currentIndex > 0 && currentIndex <= routePoints.size()) {
            return routePoints.get(currentIndex - 1);
        }
        return null;
    }
}
