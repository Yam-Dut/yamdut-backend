package org.yamdut.service;

import java.util.List;
import javax.swing.Timer;
import org.yamdut.view.map.MapPanel;

public class RideSimulationService {
    private static RideSimulationService instance;
    private Timer simulationTimer;
    private MapPanel mapPanel;
    private List<double[]> routePoints;
    private int currentIndex = 0;
    private String targetId = "ME";
    private double speedMultiplier = 2.2;
    private Runnable onComplete;

    private RideSimulationService() {
    }

    public static RideSimulationService getInstance() {
        if (instance == null) {
            instance = new RideSimulationService();
        }
        return instance;
    }

    public void startRide(
            MapPanel mapPanel,
            String targetId,
            List<double[]> interpolatedPoints,
            java.util.function.BiConsumer<double[], Integer> onUpdate,
            Runnable onComplete) {
        this.mapPanel = mapPanel;
        this.targetId = targetId;
        this.onComplete = onComplete;
        this.routePoints = interpolatedPoints;

        currentIndex = 0;
        startAnimation(onUpdate);
    }

    public List<double[]> interpolatePoints(List<double[]> points, int stepsPerSegment) {
        List<double[]> interpolated = new java.util.ArrayList<>();
        if (points == null || points.size() < 2)
            return points;

        for (int i = 0; i < points.size() - 1; i++) {
            double[] start = points.get(i);
            double[] end = points.get(i + 1);

            for (int j = 0; j < stepsPerSegment; j++) {
                double ratio = (double) j / stepsPerSegment;
                double lat = start[0] + (end[0] - start[0]) * ratio;
                double lon = start[1] + (end[1] - start[1]) * ratio;
                interpolated.add(new double[] { lat, lon });
            }
        }
        interpolated.add(points.get(points.size() - 1));
        return interpolated;
    }

    private void startAnimation(java.util.function.BiConsumer<double[], Integer> onUpdate) {
        stopRide();

        int delay = (int) (100 / speedMultiplier); // Faster interval for smoother motion
        simulationTimer = new Timer(delay, e -> {
            if (routePoints == null || currentIndex >= routePoints.size()) {
                stopRide();
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }

            double[] point = routePoints.get(currentIndex++);
            if (mapPanel != null) {
                mapPanel.updateEntityPosition(targetId, point[0], point[1], "DRIVER");
                // Center map less frequently or smoothly to avoid vertigo
                if (currentIndex % 3 == 0) {
                    mapPanel.setCenter(point[0], point[1], 15);
                }
            }

            if (onUpdate != null) {
                onUpdate.accept(point, currentIndex);
            }
        });

        simulationTimer.start();
    }

    public void stopRide() {
        if (simulationTimer != null) {
            simulationTimer.stop();
            simulationTimer = null;
        }
        currentIndex = 0;
    }

    public boolean isRunning() {
        return simulationTimer != null && simulationTimer.isRunning();
    }

    public void setSpeedMultiplier(double multiplier) {
        this.speedMultiplier = Math.max(0.1, multiplier); // Prevent division by zero/slow
    }
}
