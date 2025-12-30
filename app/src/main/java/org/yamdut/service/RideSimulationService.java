// package org.yamdut.service;

// import java.util.List;

// import javax.swing.Timer;

// import org.jxmapviewer.viewer.GeoPosition;
// import org.yamdut.view.map.MapPanel;

// public class RideSimulationService {

//     private final MapPanel mapPanel;
//     private final MapService mapService;

//     private List<GeoPosition> currentRoute;
//     private int routeIndex;
//     private Timer timer;

//     public RideSimulationService(MapPanel mapPanel, MapService mapService) {
//         this.mapPanel = mapPanel;
//         this.mapService = mapService;
//     }

//     // Entry point
//     public void startRide(
//             GeoPosition driver,
//             GeoPosition passenger,
//             GeoPosition destination
//     ) {
//         // Phase 1: driver → passenger
//         startRoute(driver, passenger, () -> {
//             // Phase 2: passenger → destination
//             startRoute(passenger, destination, null);
//         });
//     }

//     private void startRoute(
//             GeoPosition from,
//             GeoPosition to,
//             Runnable onComplete
//     ) {
//         currentRoute = mapService.fetchRoute(List.of(from, to));
//         routeIndex = 0;

//         mapPanel.drawRoute(toJson(currentRoute));
//         startAnimation(onComplete);
//     }

//     private void startAnimation(Runnable onComplete) {
//         stopRide();

//         timer = new Timer(100, e -> {
//             if (routeIndex >= currentRoute.size()) {
//                 stopRide();
//                 if (onComplete != null) onComplete.run();
//                 return;
//             }

//             GeoPosition pos = currentRoute.get(routeIndex++);
//             mapPanel.updateEntityPosition(
//                     "driver1",
//                     pos.getLatitude(),
//                     pos.getLongitude()
//             );
//         });

//         timer.start();
//     }

//     public void stopRide() {
//         if (timer != null) {
//             timer.stop();
//             timer = null;
//         }
//         mapPanel.stopSimulation();
//     }

//     private String toJson(List<GeoPosition> route) {
//         StringBuilder sb = new StringBuilder("[");
//         for (GeoPosition gp : route) {
//             sb.append(String.format(
//                     "{\"lat\":%f,\"lon\":%f},",
//                     gp.getLatitude(),
//                     gp.getLongitude()
//             ));
//         }
//         if (sb.length() > 1) sb.setLength(sb.length() - 1);
//         sb.append("]");
//         return sb.toString();
//     }
// }
