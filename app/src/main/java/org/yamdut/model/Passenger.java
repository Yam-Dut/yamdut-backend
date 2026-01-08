package org.yamdut.model;

import java.sql.Timestamp;

public class Passenger {
    private long id;
    private long userId;
    private String name;
    private String phone;
    private double rating;
    private int totalRides;
    private double totalSpent;
    private Timestamp createdAt;

    public Passenger() {
    }

    public Passenger(long userId, String name, String phone) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.rating = 5.0;
        this.totalRides = 0;
        this.totalSpent = 0.0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
}
