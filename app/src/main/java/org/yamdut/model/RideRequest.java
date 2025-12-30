package org.yamdut.model;

public class RideRequest {
    private final String pickup;
    private final String destination;
    private final double pickupLat;
    private final double pickupLon;
    private final double destLat;
    private final double destLon;
    private boolean accepted;
    private String passengerId;
    private String passengerName;

    public RideRequest(String pickup, String destination) {
        this.pickup = pickup;
        this.destination = destination;
        this.pickupLat = 0;
        this.pickupLon = 0;
        this.destLat = 0;
        this.destLon = 0;
        this.accepted = false;
    }
    
    public RideRequest(String pickup, String destination, double pickupLat, double pickupLon, 
                      double destLat, double destLon, String passengerId, String passengerName) {
        this.pickup = pickup;
        this.destination = destination;
        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
        this.destLat = destLat;
        this.destLon = destLon;
        this.accepted = false;
        this.passengerId = passengerId;
        this.passengerName = passengerName;
    }

    public void markAccepted() {
        this.accepted = true;
    }

    public boolean isAccepted() {
        return accepted;
    }
    
    public String getPickup() {
        return pickup;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public double getPickupLat() {
        return pickupLat;
    }
    
    public double getPickupLon() {
        return pickupLon;
    }
    
    public double getDestLat() {
        return destLat;
    }
    
    public double getDestLon() {
        return destLon;
    }
    
    public String getPassengerId() {
        return passengerId;
    }
    
    public String getPassengerName() {
        return passengerName != null ? passengerName : "Unknown";
    }
    
    public void setPassengerName(String name) {
        this.passengerName = name;
    }

    @Override
    public String toString() {
        String name = passengerName != null ? passengerName : "Passenger";
        // Shorten addresses for display
        String shortPickup = pickup.length() > 30 ? pickup.substring(0, 27) + "..." : pickup;
        String shortDest = destination.length() > 30 ? destination.substring(0, 27) + "..." : destination;
        return name + ": " + shortPickup + " → " + shortDest;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RideRequest that = (RideRequest) obj;
        return passengerId != null && passengerId.equals(that.passengerId) &&
               pickup.equals(that.pickup) && destination.equals(that.destination);
    }
    
    @Override
    public int hashCode() {
        return (passengerId != null ? passengerId.hashCode() : 0) + 
               pickup.hashCode() + destination.hashCode();
    }
}
