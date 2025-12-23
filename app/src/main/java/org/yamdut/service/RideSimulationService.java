package org.yamdut.service;


import org.jxmapviewer.viewer.*;
import org.yamdut.view.map.MapPanel;

import java.util.*;

public class RideSimulationService {
    private final MapPanel mapPanel;
    private final MapService mapService;

    private List<GeoPosition> currentRoute;
    private int routeIndex;
    private Timer timer;

    public RideSimulationService(MapPanel mapPanel, MapService mapService) {
        this.mapPanel =  mapPanel;
        this.mapService = mapService;
    }

    public void startRide(GeoPosition driver, GeoPosition passenger, GeoPosition destination) {
        //fetch route of diver to pickup

        currentRoute = mapService.fetchRoute(List.of(driver, passenger));
        routeIndex = 0;
        mapPanel.drawRoute(toJson(currentRoute));

        startSimulation(() -> {
            //After driver reaches to passenger fetch pickup -> destination
            currentRoute = mapService.fetchRoute(List.of(passenger, destination));
            routeIndex = 0;
            mapPanel.drawRoute(toJson(currentRoute));
            startSimulation(null);
        });
    }

    public void startSimulation(Runnable onComplete) {
        if (currentRoute == null || currentRoute.size() < 2) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (routeIndex >= currentRoute.size()) {
                    timer.cancel();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                    return;
                }
            
            GeoPosition pos = currentRoute.get(routeIndex++);
            mapPanel.updateEntityPosition("driver1", pos.getLatitude(), pos.getLongitude());
            }
        }, 0, 100);
    }

    public void stopRide() {
        if (timer != null) timer.cancel();
        mapPanel.stopSimulation();
    }

    private String toJson(List<GeoPosition> route) {
        StringBuilder sb = new StringBuilder("[");
        for (GeoPosition gp: route) {
            sb.append(String.format("{lat:%f, lon:%f},", gp.getLatitude(), gp.getLongitude()));
        }
        if (sb.length() > 1) sb.setLength(sb.length() - 1); 
        sb.append("]");
        return sb.toString();
    }
}