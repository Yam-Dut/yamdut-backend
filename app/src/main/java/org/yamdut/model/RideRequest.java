package org.yamdut.model;


public class RideRequest {
   private final String pickup;
    private final String destination;
    private String acceptedDriver;

    public RideRequest(String pickup, String destination) {
        this.pickup = pickup;
        this.destination = destination;
    }

    public String getPickup() {
        return pickup;
    }

    public String getDestination() {
        return destination;
    }

    public String getAcceptedDriver() {
        return acceptedDriver;
    }

    public void accept(String driverName) {
        this.acceptedDriver = driverName;
    }

    public boolean isAccepted() {
        return acceptedDriver != null;
    }
}
