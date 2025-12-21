package org.yamdut.model;

import java.sql.Timestamp;

public class Trip {
    public enum TripStatus {
        PENDING,
        ACCEPTED,
        GOING_TO_PICKUP,
        AT_PICKUP,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }

    private int id;
    private int driverId;
    private int riderId;
    private String riderName;
    private String riderPhone;
    private String pickupLocation;
    private double pickupLat;
    private double pickupLng;
    private String dropLocation;
    private double dropoffLat;
    private double dropoffLng;
    private int baseFare;
    private int adjustedFare;
    private TripStatus status;
    private long requestTime;
    private Timestamp arrivedAt;
    private Timestamp startedAt;
    private Timestamp endedAt;
    private double distance;
    private double distanceTraveled;
    private Timestamp createdAt;
    private double driverLat;
    private double driverLng;

    public Trip() {}

    public Trip(int driverId, int riderId, String riderName, String riderPhone,
                String pickupLocation, double pickupLat, double pickupLng,
                String dropLocation, double dropoffLat, double dropoffLng, int baseFare) {
        this.driverId = driverId;
        this.riderId = riderId;
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.pickupLocation = pickupLocation;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.dropLocation = dropLocation;
        this.dropoffLat = dropoffLat;
        this.dropoffLng = dropoffLng;
        this.baseFare = baseFare;
        this.adjustedFare = baseFare;
        this.status = TripStatus.PENDING;
        this.requestTime = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getRiderId() {
        return riderId;
    }

    public void setRiderId(int riderId) {
        this.riderId = riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public double getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(double dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public double getDropoffLng() {
        return dropoffLng;
    }

    public void setDropoffLng(double dropoffLng) {
        this.dropoffLng = dropoffLng;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public int getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(int baseFare) {
        this.baseFare = baseFare;
    }

    public int getAdjustedFare() {
        return adjustedFare;
    }

    public void setAdjustedFare(int adjustedFare) {
        this.adjustedFare = adjustedFare;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(double driverLat) {
        this.driverLat = driverLat;
    }

    public double getDriverLng() {
        return driverLng;
    }

    public void setDriverLng(double driverLng) {
        this.driverLng = driverLng;
    }

    public Timestamp getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(Timestamp arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Timestamp endedAt) {
        this.endedAt = endedAt;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getFare() {
        return adjustedFare;
    }

    public void setFare(double fare) {
        this.adjustedFare = (int) fare;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
