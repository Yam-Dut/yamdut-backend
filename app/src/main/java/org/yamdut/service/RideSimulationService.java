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
    
    private RideSimulationService() {}
    
    public static RideSimulationService getInstance() {
        if (instance == null) {
            instance = new RideSimulationService();
        }
        return instance;
    }
    
    public void startRide(
            MapPanel mapPanel,
            double driverLat, double driverLon,
            double pickupLat, double pickupLon,
            double destLat, double destLon,
            Runnable onComplete
    ) {
        this.mapPanel = mapPanel;
        this.onComplete = onComplete;
        this.routePoints = new ArrayList<>();
        
        // Generate route points (simplified - in real app, use OSRM route)
        generateRoutePoints(driverLat, driverLon, pickupLat, pickupLon);
        generateRoutePoints(pickupLat, pickupLon, destLat, destLon);
        
        currentIndex = 0;
        startAnimation();
    }
    
    private void generateRoutePoints(double fromLat, double fromLon, double toLat, double toLon) {
        // Generate intermediate points for smooth animation
        int steps = 20;
        for (int i = 0; i <= steps; i++) {
            double ratio = (double) i / steps;
            double lat = fromLat + (toLat - fromLat) * ratio;
            double lon = fromLon + (toLon - fromLon) * ratio;
            routePoints.add(new double[]{lat, lon});
        }
    }
    
    private void startAnimation() {
        stopRide();
        
        simulationTimer = new Timer(200, e -> {
            if (currentIndex >= routePoints.size()) {
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
}
