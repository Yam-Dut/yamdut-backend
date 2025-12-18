package org.yamdut.model;

public class Driver {
    private int id;
    private String name;
    private String phone;
    private double rating;
    private int totalRides;
    private String status;

    public Driver() {}

    public Driver(String name, String phone, double rating, int totalRides, String status) {
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.totalRides = totalRides;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


