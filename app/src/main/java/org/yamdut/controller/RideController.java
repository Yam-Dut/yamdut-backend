// package org.yamdut.controller;

// import org.jxmapviewer.viewer.GeoPosition;
// import org.yamdut.service.RideSimulationService;
// import org.yamdut.view.dashboard.DriverDashboard;
// import org.yamdut.view.dashboard.PassengerDashboard;

// public class RideController {

//     private final PassengerDashboard passenger;
//     private final DriverDashboard driver;
//     private final RideSimulationService simulationService;

//     public RideController(
//             PassengerDashboard passenger,
//             DriverDashboard driver,
//             RideSimulationService simulationService
//     ) {
//         this.passenger = passenger;
//         this.driver = driver;
//         this.simulationService = simulationService;
//         wirePassengerActions();
//     }

//     private void wirePassengerActions() {
//         passenger.getBookRideButton().addActionListener(e -> {
//             GeoPosition pickup = passenger.getPickupLocation();
//             GeoPosition dropoff = passenger.getDropoffLocation();
//             GeoPosition driverPos = driver.getCurrentLocation();

//             if (pickup == null || dropoff == null || driverPos == null)
//                 return;

//             initializeMapEntities(pickup);

//             simulationService.startRide(
//                     driverPos,
//                     pickup,
//                     dropoff
//             );
//         });
//     }

//     private void initializeMapEntities(GeoPosition pickup) {
//         // TODO: Implement entity display logic
//         // The JXMapViewer does not have a showEntities method.
//         // Consider adding custom overlays or painters to display driver and passenger positions.
//         // Example: Create custom waypoints or use a custom painter to render the entities on the map.
//     }
// }
