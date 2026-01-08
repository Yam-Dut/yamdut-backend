package org.yamdut.model;

public class RideRequest {

    // =====================
    // DB fields
    // =====================
    // =====================
    // DB fields
    // =====================
    private long id; // BIGINT
    private long passengerId; // BIGINT
    private String passengerName;

    private String pickup;
    private String dropoff;

    // Coordinates
    private double pickupLat;
    private double pickupLon;
    private double destLat;
    private double destLon;

    // Driver info (assigned)
    private long driverId;
    private String driverName;
    private double driverLat;
    private double driverLon;
    private double fare;
    private String status; // REQUESTING, ACCEPTED, CANCELLED
    private boolean accepted;

    // =====================
    // Constructors
    // =====================

    // Used when passenger books a ride (before DB id exists)
    public RideRequest(long passengerId, String passengerName, String pickup, String dropoff) {
        this.passengerId = passengerId;
        this.passengerName = passengerName;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.driverId = 0;
        this.status = "REQUESTING";
        this.accepted = false;
    }

    // Constructor with coordinates (used by Controller)
    public RideRequest(String pickup, String dropoff, double pickupLat, double pickupLon,
            double destLat, double destLon, String passengerIdStr, String passengerName) {
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
        this.destLat = destLat;
        this.destLon = destLon;

        // Handle passenger ID parsing
        try {
            this.passengerId = Long.parseLong(passengerIdStr.replaceAll("\\D", ""));
            if (this.passengerId == 0)
                this.passengerId = 1; // Fallback
        } catch (NumberFormatException e) {
            this.passengerId = 1; // Fallback
        }

        this.passengerName = passengerName;
        this.driverId = 0;
        this.status = "REQUESTING";
        this.accepted = false;
    }

    // =====================
    // Getters
    // =====================

    public long getId() {
        return id;
    }

    public long getPassengerId() {
        return passengerId;
    }

    public String getPassengerName() {
        return passengerName != null ? passengerName : "Unknown";
    }

    public String getPickup() {
        return pickup;
    }

    public String getDropoff() {
        return dropoff;
    }

    // Alias for getDropoff to match controller usage
    public String getDestination() {
        return dropoff;
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

    public long getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public double getFare() {
        return fare;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(double driverLat) {
        this.driverLat = driverLat;
    }

    public double getDriverLon() {
        return driverLon;
    }

    public void setDriverLon(double driverLon) {
        this.driverLon = driverLon;
    }

    // =====================
    // Setters / State
    // =====================

    public void setId(long id) {
        this.id = id;
    }

    public void markAccepted() {
        this.accepted = true;
        this.status = "ACCEPTED";
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    // =====================
    // Object overrides
    // =====================

    @Override
    public String toString() {
        return getPassengerName() + ": " + getPickup() + " → " + getDropoff();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RideRequest))
            return false;
        RideRequest other = (RideRequest) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
