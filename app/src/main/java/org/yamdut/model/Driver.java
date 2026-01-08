package org.yamdut.model;

public class Driver {
    private long id;
    private long userId;
    private String name;
    private String phone;
    private double rating;
    private int totalRides;
    private String status;
    private String vehicleType;
    private String licenseNumber;
    private double totalEarnings;
    private double lat;
    private double lon;

    public Driver() {
    }

    public Driver(String name, String phone, double rating, int totalRides, String status) {
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.totalRides = totalRides;
        this.status = status;
        this.totalEarnings = 0.0;
    }

    public Driver(long userId, String name, String phone, String vehicleType, String licenseNumber, String status) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.status = status;
        this.rating = 5.0; // Default
        this.totalRides = 0; // Default
        this.totalEarnings = 0.0; // Default
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
