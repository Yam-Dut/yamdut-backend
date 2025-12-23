package org.yamdut.controller;
import org.jxmapviewer.viewer.GeoPosition;
import java.util.concurrent.CopyOnWriteArrayList;

public class RideController {
   private PassengerDashboard passenger;
    private DriverDashboard driver;
    private RideSimulationService simulationService;

    private GeoPosition pickup;
    private GeoPosition dropoff;

    // Listener list to notify driver map updates
    private final CopyOnWriteArrayList<GeoPosition> routeUpdates = new CopyOnWriteArrayList<>();

    public RideController(PassengerDashboard passenger, DriverDashboard driver, RideSimulationService simulationService) {
        this.passenger = passenger;
        this.driver = driver;
        this.simulationService = simulationService;
        wirePassengerActions();
    }

    private void wirePassengerActions() {
        // this to to be modify in passenger dashboard and in passenger controller 
        passenger.getBookRideButton().addActionListener(e -> {
            pickup = passenger.getPickupLocation();
            dropoff = passenger.getDropoffLocation();

            if (pickup == null || dropoff == null) return;

            // Update driver map with pickup

            //this is to be in controller 

            driver.getMapPanel().showEntities("[{\"id\":\"driver1\",\"name\":\"Driver1\",\"lat\":" 
                        + driver.getCurrentLocation().getLatitude() + ",\"lon\":"
                        + driver.getCurrentLocation().getLongitude() + ",\"type\":\"driver\"},"
                        + "{\"id\":\"passenger1\",\"name\":\"Passenger1\",\"lat\":" + pickup.getLatitude()
                        + ",\"lon\":" + pickup.getLongitude() + ",\"type\":\"passenger\"}]");

            // Start ride simulation (driver moves to passenger)
            simulationService.startRide(driver.getCurrentLocation(), pickup, dropoff);
        });
    }
}
